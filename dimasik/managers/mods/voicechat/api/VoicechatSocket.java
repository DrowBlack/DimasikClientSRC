package dimasik.managers.mods.voicechat.api;

import dimasik.managers.mods.voicechat.api.RawUdpPacket;
import java.net.SocketAddress;

public interface VoicechatSocket {
    public void open(int var1, String var2) throws Exception;

    public RawUdpPacket read() throws Exception;

    public void send(byte[] var1, SocketAddress var2) throws Exception;

    public int getLocalPort();

    public void close();

    public boolean isClosed();
}
