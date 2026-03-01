package dimasik.managers.mods.voicechat.api;

import dimasik.managers.mods.voicechat.api.RawUdpPacket;
import java.net.SocketAddress;

public interface ClientVoicechatSocket {
    public void open() throws Exception;

    public RawUdpPacket read() throws Exception;

    public void send(byte[] var1, SocketAddress var2) throws Exception;

    public void close();

    public boolean isClosed();
}
