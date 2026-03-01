package dimasik.managers.mods.voicechat.api.audiochannel;

public interface AudioPlayer {
    public void startPlaying();

    public void stopPlaying();

    public boolean isStarted();

    public boolean isPlaying();

    public boolean isStopped();

    public void setOnStopped(Runnable var1);
}
