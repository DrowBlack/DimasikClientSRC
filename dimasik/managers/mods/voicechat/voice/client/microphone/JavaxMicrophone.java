package dimasik.managers.mods.voicechat.voice.client.microphone;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneException;
import dimasik.managers.mods.voicechat.voice.client.microphone.Microphone;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class JavaxMicrophone
implements Microphone {
    private final int sampleRate;
    @Nullable
    private final String deviceName;
    private final int bufferSize;
    @Nullable
    private TargetDataLine mic;

    public JavaxMicrophone(int sampleRate, int bufferSize, @Nullable String deviceName) {
        this.sampleRate = sampleRate;
        this.deviceName = deviceName;
        this.bufferSize = bufferSize;
    }

    @Override
    public void open() throws MicrophoneException {
        if (this.isOpen()) {
            throw new MicrophoneException("Microphone already open");
        }
        AudioFormat af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, this.sampleRate, 16, 1, 2, this.sampleRate, false);
        this.mic = JavaxMicrophone.getMicrophoneByName(af, this.deviceName);
        if (this.mic == null) {
            if (this.deviceName != null) {
                Voicechat.LOGGER.warn("Failed to open microphone '{}', falling back to default microphone", this.deviceName);
            }
            this.mic = JavaxMicrophone.getDefaultMicrophone(af);
        }
        if (this.mic == null) {
            throw new MicrophoneException("Could not find any microphone with the specified audio format");
        }
        try {
            this.mic.open(af);
        }
        catch (LineUnavailableException e) {
            throw new MicrophoneException(e.getMessage());
        }
        this.mic.start();
        this.mic.stop();
        this.mic.flush();
    }

    @Override
    public void start() {
        if (!this.isOpen() || this.mic == null) {
            return;
        }
        this.mic.start();
    }

    @Override
    public void stop() {
        if (!this.isOpen() || this.mic == null) {
            return;
        }
        this.mic.stop();
        this.mic.flush();
    }

    @Override
    public void close() {
        if (this.mic == null) {
            return;
        }
        this.mic.stop();
        this.mic.flush();
        this.mic.close();
    }

    @Override
    public boolean isOpen() {
        if (this.mic == null) {
            return false;
        }
        return this.mic.isOpen();
    }

    @Override
    public boolean isStarted() {
        if (this.mic == null) {
            return false;
        }
        return this.mic.isActive();
    }

    @Override
    public int available() {
        if (this.mic == null) {
            return 0;
        }
        return this.mic.available() / 2;
    }

    @Override
    public short[] read() {
        if (this.mic == null) {
            throw new IllegalStateException("Microphone was not opened");
        }
        int available = this.available();
        if (this.bufferSize > available) {
            throw new IllegalStateException(String.format("Failed to read from microphone: Capacity %s, available %s", this.bufferSize, available));
        }
        byte[] buff = new byte[this.bufferSize * 2];
        this.mic.read(buff, 0, buff.length);
        return Utils.bytesToShorts(buff);
    }

    @Nullable
    private static TargetDataLine getDefaultMicrophone(AudioFormat format) {
        return JavaxMicrophone.getDefaultDevice(TargetDataLine.class, format);
    }

    @Nullable
    private static <T> T getDefaultDevice(Class<T> lineClass, AudioFormat format) {
        DataLine.Info info = new DataLine.Info(lineClass, format);
        try {
            return lineClass.cast(AudioSystem.getLine(info));
        }
        catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private static TargetDataLine getMicrophoneByName(AudioFormat format, @Nullable String name) {
        return JavaxMicrophone.getDeviceByName(TargetDataLine.class, format, name);
    }

    @Nullable
    private static <T> T getDeviceByName(Class<T> lineClass, AudioFormat format, @Nullable String name) {
        Mixer.Info[] mixers;
        for (Mixer.Info mixerInfo : mixers = AudioSystem.getMixerInfo()) {
            DataLine.Info lineInfo;
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (!mixer.isLineSupported(lineInfo = new DataLine.Info(lineClass, format)) || !mixerInfo.getName().equals(name)) continue;
            try {
                return lineClass.cast(mixer.getLine(lineInfo));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public static List<String> getAllMicrophones() {
        return JavaxMicrophone.getAllMicrophones(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000.0f, 16, 1, 2, 48000.0f, false));
    }

    private static List<String> getAllMicrophones(AudioFormat format) {
        return JavaxMicrophone.getDeviceNames(TargetDataLine.class, format);
    }

    private static List<String> getDeviceNames(Class<?> lineClass, AudioFormat format) {
        Mixer.Info[] mixers;
        ArrayList<String> names = new ArrayList<String>();
        for (Mixer.Info mixerInfo : mixers = AudioSystem.getMixerInfo()) {
            DataLine.Info lineInfo;
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (!mixer.isLineSupported(lineInfo = new DataLine.Info(lineClass, format))) continue;
            names.add(mixerInfo.getName());
        }
        return names;
    }
}
