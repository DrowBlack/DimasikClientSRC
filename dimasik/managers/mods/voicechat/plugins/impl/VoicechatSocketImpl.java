package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.RawUdpPacket;
import dimasik.managers.mods.voicechat.api.VoicechatSocket;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatSocketBase;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nullable;

public class VoicechatSocketImpl
extends VoicechatSocketBase
implements VoicechatSocket {
    @Nullable
    private DatagramSocket socket;

    @Override
    public void open(int port, String bindAddress) throws Exception {
        if (this.socket != null) {
            throw new IllegalStateException("Socket already opened");
        }
        this.checkCorrectHost();
        InetAddress address = null;
        try {
            if (!bindAddress.isEmpty()) {
                address = InetAddress.getByName(bindAddress);
            }
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to parse bind IP address '{}'", bindAddress, e);
            Voicechat.LOGGER.info("Binding to wildcard IP address", new Object[0]);
        }
        try {
            try {
                this.socket = new DatagramSocket(port, address);
            }
            catch (BindException e) {
                if (address == null || bindAddress.equals("0.0.0.0")) {
                    throw e;
                }
                Voicechat.LOGGER.error("Failed to bind to address '{}', binding to wildcard IP instead", bindAddress);
                this.socket = new DatagramSocket(port);
            }
        }
        catch (BindException e) {
            Voicechat.LOGGER.error("Failed to run voice chat at UDP port {}, make sure no other application is running at that port", port);
            Voicechat.LOGGER.error("Voice chat server error", e);
            if (CommonCompatibilityManager.INSTANCE.isDedicatedServer()) {
                Voicechat.LOGGER.error("Shutting down server", new Object[0]);
                System.exit(1);
            }
            throw e;
        }
    }

    private void checkCorrectHost() throws Exception {
        String host = Voicechat.SERVER_CONFIG.voiceHost.get();
        if (!host.isEmpty()) {
            try {
                new URI("voicechat://" + host);
                Voicechat.LOGGER.info("Voice host is '{}'", host);
            }
            catch (URISyntaxException e) {
                Voicechat.LOGGER.warn("Failed to parse voice host", e);
                System.exit(1);
                throw e;
            }
        }
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
        if (this.socket == null || this.socket.isClosed()) {
            return;
        }
        this.socket.send(new DatagramPacket(data, data.length, address));
    }

    @Override
    public int getLocalPort() {
        if (this.socket == null) {
            return -1;
        }
        return this.socket.getLocalPort();
    }

    @Override
    public void close() {
        if (this.socket != null) {
            this.socket.close();
        }
    }

    @Override
    public boolean isClosed() {
        if (this.socket == null) {
            return true;
        }
        return this.socket.isClosed();
    }
}
