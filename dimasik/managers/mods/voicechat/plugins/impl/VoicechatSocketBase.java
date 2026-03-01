package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.plugins.impl.RawUdpPacketImpl;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class VoicechatSocketBase {
    private final byte[] BUFFER = new byte[4096];

    public RawUdpPacketImpl read(DatagramSocket socket) throws IOException {
        DatagramPacket packet = new DatagramPacket(this.BUFFER, this.BUFFER.length);
        socket.receive(packet);
        long timestamp = System.currentTimeMillis();
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
        return new RawUdpPacketImpl(data, packet.getSocketAddress(), timestamp);
    }
}
