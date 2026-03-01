package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.api.RawUdpPacket;
import java.net.SocketAddress;

public class RawUdpPacketImpl
implements RawUdpPacket {
    private final byte[] data;
    private final SocketAddress socketAddress;
    private final long timestamp;

    public RawUdpPacketImpl(byte[] data, SocketAddress socketAddress, long timestamp) {
        this.data = data;
        this.socketAddress = socketAddress;
        this.timestamp = timestamp;
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public SocketAddress getSocketAddress() {
        return this.socketAddress;
    }
}
