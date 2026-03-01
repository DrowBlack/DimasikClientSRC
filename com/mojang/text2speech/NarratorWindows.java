package com.mojang.text2speech;

import com.google.common.collect.Queues;
import com.mojang.text2speech.Narrator;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.WString;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NarratorWindows
implements Narrator {
    private static boolean libraryFound = false;
    private static final Logger LOGGER = LogManager.getLogger();
    private static long voice;
    private static boolean stopping;
    private final NarratorThread narratorThread = new NarratorThread();
    private boolean crashed = false;

    public NarratorWindows() {
        Thread thread = new Thread(this.narratorThread);
        thread.setName("Narrator");
        thread.start();
    }

    @Override
    public void say(String msg, boolean interrupt) {
        if (this.crashed) {
            return;
        }
        try {
            this.narratorThread.add(new Message(msg, interrupt));
        }
        catch (Throwable e) {
            this.crashed = true;
            LOGGER.error(String.format("Narrator crashed : %s", e));
        }
    }

    @Override
    public void clear() {
        this.narratorThread.clear();
        this.narratorThread.add(new Message("", true));
    }

    @Override
    public boolean active() {
        return libraryFound;
    }

    @Override
    public void destroy() {
        stopping = true;
        try {
            this.narratorThread.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        SAPIWrapperSolutionDLL.uninit(voice);
    }

    static {
        String result = "";
        try {
            Native.register(SAPIWrapperSolutionDLL.class, NativeLibrary.getInstance("SAPIWrapper_x64"));
            libraryFound = true;
            LOGGER.info("Narrator library for x64 successfully loaded");
            voice = SAPIWrapperSolutionDLL.init();
            if (voice == 0L) {
                result = result + "ERROR : Couldn't create a voice\n";
            }
        }
        catch (UnsatisfiedLinkError e) {
            result = result + "ERROR : Couldn't load Narrator library : " + e.getMessage() + "\n";
        }
        catch (Throwable e) {
            result = result + "ERROR : Generic error while loading narrator : " + e.getMessage() + "\n";
        }
        if (!libraryFound) {
            try {
                Native.register(SAPIWrapperSolutionDLL.class, NativeLibrary.getInstance("SAPIWrapper_x86"));
                libraryFound = true;
                LOGGER.info("Narrator library for x86 successfully loaded");
                voice = SAPIWrapperSolutionDLL.init();
                if (voice == 0L) {
                    result = result + "ERROR : Couldn't create a voice\n";
                }
            }
            catch (UnsatisfiedLinkError e) {
                result = result + "ERROR : Couldn't load Narrator library : " + e.getMessage() + "\n";
            }
            catch (Throwable e) {
                result = result + "ERROR : Generic error while loading narrator : " + e.getMessage() + "\n";
            }
        }
        if (!libraryFound) {
            LOGGER.warn(result);
        }
    }

    private class Message {
        final String text;
        final boolean interrupt;

        private Message(String text, boolean interrupt) {
            this.text = text;
            this.interrupt = interrupt;
        }

        public void apply() {
            SAPIWrapperSolutionDLL.queue(voice, new WString(this.text.replaceAll("[<>]", "")), this.interrupt);
        }
    }

    private static class NarratorThread
    extends Thread {
        protected final Queue<Message> msgs = Queues.newConcurrentLinkedQueue();

        private NarratorThread() {
        }

        @Override
        public void run() {
            while (!stopping) {
                if (this.msgs.peek() != null) {
                    this.msgs.poll().apply();
                }
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void add(Message msg) {
            this.msgs.add(msg);
        }

        public void clear() {
            this.msgs.clear();
        }
    }

    private static class SAPIWrapperSolutionDLL {
        private SAPIWrapperSolutionDLL() {
        }

        public static native long init();

        public static native void uninit(long var0);

        public static native void queue(long var0, WString var2, boolean var3);
    }
}
