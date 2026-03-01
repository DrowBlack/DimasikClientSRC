package dimasik.managers.mods.voicechat.api.opus;

import javax.annotation.Nullable;

public interface OpusDecoder {
    public short[] decode(@Nullable byte[] var1);

    public void resetState();

    public boolean isClosed();

    public void close();
}
