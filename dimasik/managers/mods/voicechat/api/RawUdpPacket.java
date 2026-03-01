package dimasik.managers.mods.voicechat.api;

import java.net.SocketAddress;

public interface RawUdpPacket {
    public byte[] getData();

    public long getTimestamp();

    public SocketAddress getSocketAddress();
}
