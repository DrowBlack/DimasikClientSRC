package com.mojang.text2speech;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.annotations.Msg;
import com.google.common.collect.Queues;
import com.mojang.text2speech.Narrator;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NarratorOSX
extends NSObject
implements Narrator {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Proxy synth = Client.getInstance().sendProxy("NSSpeechSynthesizer", "alloc", new Object[0]);
    private boolean speaking;
    private boolean crashed;
    private final Queue<String> queue = Queues.newConcurrentLinkedQueue();

    public NarratorOSX() {
        super("NSObject");
        this.synth.send("init", new Object[0]);
        this.synth.send("setDelegate:", this);
    }

    private void startSpeaking(String message) {
        this.synth.send("startSpeakingString:", message);
    }

    @Msg(selector="speechSynthesizer:didFinishSpeaking:", signature="v@:B")
    public void didFinishSpeaking(boolean naturally) {
        if (this.queue.isEmpty()) {
            this.speaking = false;
        } else {
            this.startSpeaking(this.queue.poll());
        }
    }

    @Override
    public void say(String msg, boolean interrupt) {
        if (this.crashed) {
            return;
        }
        try {
            if (interrupt) {
                this.synth.send("stopSpeaking", new Object[0]);
            }
            if (this.speaking) {
                this.queue.offer(msg);
            } else {
                this.speaking = true;
                this.startSpeaking(msg);
            }
        }
        catch (Throwable e) {
            this.crashed = true;
            LOGGER.error(String.format("Narrator crashed : %s", e));
        }
    }

    @Override
    public void clear() {
        this.queue.clear();
        this.synth.send("stopSpeaking", new Object[0]);
    }

    @Override
    public boolean active() {
        return true;
    }

    @Override
    public void destroy() {
    }
}
