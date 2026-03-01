package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.plugins.ClientPluginManager;
import dimasik.managers.mods.voicechat.voice.client.speaker.SpeakerException;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EXTThreadLocalContext;

public class SoundManager {
    @Nullable
    private final String deviceName;
    private long device;
    private long context;
    private static final Pattern DEVICE_NAME = Pattern.compile("^(?:OpenAL.+?on )?(.*)$");

    public SoundManager(@Nullable String deviceName) throws SpeakerException {
        this.deviceName = deviceName;
        this.device = this.openSpeaker(deviceName);
        this.context = ALC11.alcCreateContext(this.device, (IntBuffer)null);
        ClientPluginManager.instance().onCreateALContext(this.context, this.device);
    }

    public void close() {
        ClientPluginManager.instance().onDestroyALContext(this.context, this.device);
        if (this.context != 0L) {
            ALC11.alcDestroyContext(this.context);
            SoundManager.checkAlcError(this.device);
        }
        if (this.device != 0L) {
            ALC11.alcCloseDevice(this.device);
            SoundManager.checkAlcError(this.device);
        }
        this.context = 0L;
        this.device = 0L;
    }

    public boolean isClosed() {
        return this.context == 0L || this.device == 0L;
    }

    private long openSpeaker(@Nullable String name) throws SpeakerException {
        try {
            return this.tryOpenSpeaker(name);
        }
        catch (SpeakerException e) {
            if (name != null) {
                Voicechat.LOGGER.warn("Failed to open audio channel '{}', falling back to default", name);
            }
            try {
                return this.tryOpenSpeaker(SoundManager.getDefaultSpeaker());
            }
            catch (SpeakerException ex) {
                return this.tryOpenSpeaker(null);
            }
        }
    }

    private long tryOpenSpeaker(@Nullable String string) throws SpeakerException {
        long l = ALC11.alcOpenDevice(string);
        if (l == 0L) {
            throw new SpeakerException("Failed to open audio device: Audio device not found");
        }
        SoundManager.checkAlcError(this.device);
        return l;
    }

    @Nullable
    public static String getDefaultSpeaker() {
        if (!SoundManager.canEnumerate()) {
            return null;
        }
        String defaultSpeaker = ALC11.alcGetString(0L, 4115);
        SoundManager.checkAlcError(0L);
        return defaultSpeaker;
    }

    public static List<String> getAllSpeakers() {
        if (!SoundManager.canEnumerate()) {
            return Collections.emptyList();
        }
        List<String> devices = ALUtil.getStringList(0L, 4115);
        SoundManager.checkAlcError(0L);
        return devices == null ? Collections.emptyList() : devices;
    }

    public void runInContext(Executor executor, Runnable runnable) {
        long time = System.currentTimeMillis();
        executor.execute(() -> {
            long diff = System.currentTimeMillis() - time;
            if (diff > 20L || diff >= 5L && Voicechat.debugMode()) {
                Voicechat.LOGGER.warn("Sound executor delay: {} ms!", diff);
            }
            if (this.openContext()) {
                runnable.run();
                this.closeContext();
            }
        });
    }

    public boolean openContext() {
        if (this.context == 0L) {
            return false;
        }
        boolean success = EXTThreadLocalContext.alcSetThreadContext(this.context);
        SoundManager.checkAlcError(this.device);
        return success;
    }

    public void closeContext() {
        EXTThreadLocalContext.alcSetThreadContext(0L);
        SoundManager.checkAlcError(this.device);
    }

    public static boolean checkAlError() {
        int error = AL11.alGetError();
        if (error == 0) {
            return false;
        }
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        Voicechat.LOGGER.error("Voicechat sound manager AL error: {}.{}[{}] {}", stack.getClassName(), stack.getMethodName(), stack.getLineNumber(), SoundManager.getAlError(error));
        return true;
    }

    public static boolean checkAlcError(long device) {
        int error = ALC11.alcGetError(device);
        if (error == 0) {
            return false;
        }
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        Voicechat.LOGGER.error("Voicechat sound manager ALC error: {}.{}[{}] {}", stack.getClassName(), stack.getMethodName(), stack.getLineNumber(), SoundManager.getAlcError(error));
        return true;
    }

    private static String getAlError(int i) {
        switch (i) {
            case 40961: {
                return "Invalid name";
            }
            case 40962: {
                return "Invalid enum ";
            }
            case 40963: {
                return "Invalid value";
            }
            case 40964: {
                return "Invalid operation";
            }
            case 40965: {
                return "Out of memory";
            }
        }
        return "Unknown error";
    }

    public static String getAlcError(int i) {
        switch (i) {
            case 40961: {
                return "Invalid device";
            }
            case 40962: {
                return "Invalid context";
            }
            case 40963: {
                return "Invalid enum";
            }
            case 40964: {
                return "Invalid value";
            }
            case 40965: {
                return "Out of memory";
            }
        }
        return "Unknown error";
    }

    public static String cleanDeviceName(String name) {
        Matcher matcher = DEVICE_NAME.matcher(name);
        if (!matcher.matches()) {
            return name;
        }
        return matcher.group(1);
    }

    public static boolean canEnumerate() {
        boolean present = ALC11.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT");
        SoundManager.checkAlcError(0L);
        return present;
    }
}
