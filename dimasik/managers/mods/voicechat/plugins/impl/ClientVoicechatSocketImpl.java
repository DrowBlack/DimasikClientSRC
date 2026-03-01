package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.api.ClientVoicechatSocket;
import dimasik.managers.mods.voicechat.api.RawUdpPacket;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatSocketBase;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class ClientVoicechatSocketImpl
extends VoicechatSocketBase
implements ClientVoicechatSocket {
    private DatagramSocket socket;

    @Override
    public void open() throws Exception {
        this.socket = new DatagramSocket();
    }

    @Override
    public RawUdpPacket read() throws Exception {
        if (this.socket == null) {
            throw new IllegalStateException("Socket not opened yet");
        }
        return this.read(this.socket);
    }

    @Override
    public void send(byte[] data, SocketAddress address) throws Exception {
        if (this.socket == null) {
            return;
        }
        this.socket.send(new DatagramPacket(data, data.length, address));
    }

    @Override
    public void close() {
        if (this.socket != null) {
            this.socket.close();
        }
    }

    @Override
    public boolean isClosed() {
        return this.socket == null;
    }
}
