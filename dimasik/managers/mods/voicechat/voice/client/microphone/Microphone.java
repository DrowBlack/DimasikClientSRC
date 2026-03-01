package dimasik.managers.mods.voicechat.voice.client.microphone;

import dimasik.managers.mods.voicechat.voice.client.MicrophoneException;

public interface Microphone {
    public void open() throws MicrophoneException;

    public void start();

    public void stop();

    public void close();

    public boolean isOpen();

    public boolean isStarted();

    public int available();

    public short[] read();
}
