package dimasik.managers.mods.voicechat.api.mp3;

import java.io.IOException;

public interface Mp3Encoder {
    public void encode(short[] var1) throws IOException;

    public void close() throws IOException;
}
