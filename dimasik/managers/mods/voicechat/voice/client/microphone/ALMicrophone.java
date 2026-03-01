package dimasik.managers.mods.voicechat.voice.client.microphone;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneException;
import dimasik.managers.mods.voicechat.voice.client.SoundManager;
import dimasik.managers.mods.voicechat.voice.client.microphone.Microphone;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALUtil;

public class ALMicrophone
implements Microphone {
    private final int sampleRate;
    @Nullable
    private final String deviceName;
    private long device;
    private final int bufferSize;
    private boolean started;

    public ALMicrophone(int sampleRate, int bufferSize, @Nullable String deviceName) {
        this.sampleRate = sampleRate;
        this.deviceName = deviceName;
        this.bufferSize = bufferSize;
    }

    @Override
    public void open() throws MicrophoneException {
        if (this.isOpen()) {
            throw new MicrophoneException("Microphone already open");
        }
        this.device = this.openMic(this.deviceName);
    }

    @Override
    public void start() {
        if (!this.isOpen()) {
            return;
        }
        if (this.started) {
            return;
        }
        ALC11.alcCaptureStart(this.device);
        SoundManager.checkAlcError(this.device);
        this.started = true;
    }

    @Override
    public void stop() {
        if (!this.isOpen()) {
            return;
        }
        if (!this.started) {
            return;
        }
        ALC11.alcCaptureStop(this.device);
        SoundManager.checkAlcError(this.device);
        this.started = false;
        int available = this.available();
        float[] data = new float[available];
        ALC11.alcCaptureSamples(this.device, data, data.length);
        SoundManager.checkAlcError(this.device);
        Voicechat.LOGGER.debug("Clearing {} samples", available);
    }

    @Override
    public void close() {
        if (!this.isOpen()) {
            return;
        }
        this.stop();
        ALC11.alcCaptureCloseDevice(this.device);
        SoundManager.checkAlcError(this.device);
        this.device = 0L;
    }

    @Override
    public boolean isOpen() {
        return this.device != 0L;
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }

    @Override
    public int available() {
        int samples = ALC11.alcGetInteger(this.device, 786);
        SoundManager.checkAlcError(this.device);
        return samples;
    }

    @Override
    public short[] read() {
        int available = this.available();
        if (this.bufferSize > available) {
            throw new IllegalStateException(String.format("Failed to read from microphone: Capacity %s, available %s", this.bufferSize, available));
        }
        float[] buff = new float[this.bufferSize];
        ALC11.alcCaptureSamples(this.device, buff, buff.length);
        SoundManager.checkAlcError(this.device);
        return Utils.floatsToShortsNormalized(buff);
    }

    private long openMic(@Nullable String name) throws MicrophoneException {
        try {
            return this.tryOpenMic(name);
        }
        catch (MicrophoneException e) {
            if (name != null) {
                Voicechat.LOGGER.warn("Failed to open microphone '{}', falling back to default microphone", name);
            }
            try {
                return this.tryOpenMic(ALMicrophone.getDefaultMicrophone());
            }
            catch (MicrophoneException ex) {
                return this.tryOpenMic(null);
            }
        }
    }

    private long tryOpenMic(@Nullable String string) throws MicrophoneException {
        long device = ALC11.alcCaptureOpenDevice(string, this.sampleRate, 65552, this.bufferSize);
        if (device == 0L) {
            SoundManager.checkAlcError(0L);
            throw new MicrophoneException(String.format("Failed to open microphone: %s", SoundManager.getAlcError(0)));
        }
        SoundManager.checkAlcError(device);
        return device;
    }

    @Nullable
    public static String getDefaultMicrophone() {
        if (!SoundManager.canEnumerate()) {
            return null;
        }
        String mic = ALC11.alcGetString(0L, 784);
        SoundManager.checkAlcError(0L);
        return mic;
    }

    public static List<String> getAllMicrophones() {
        if (!SoundManager.canEnumerate()) {
            return Collections.emptyList();
        }
        List<String> devices = ALUtil.getStringList(0L, 784);
        SoundManager.checkAlcError(0L);
        return devices == null ? Collections.emptyList() : devices;
    }
}
