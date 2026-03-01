package dimasik.managers.mods.voicechat.api.audiosender;

public interface AudioSender {
    public AudioSender whispering(boolean var1);

    public boolean isWhispering();

    public AudioSender sequenceNumber(long var1);

    public boolean canSend();

    public boolean send(byte[] var1);

    public boolean reset();
}
