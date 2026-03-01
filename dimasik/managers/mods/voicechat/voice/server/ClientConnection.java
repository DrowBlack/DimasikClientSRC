package dimasik.managers.mods.voicechat.voice.server;

import dimasik.managers.mods.voicechat.voice.common.NetworkMessage;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.net.SocketAddress;
import java.util.UUID;

public class ClientConnection {
    private final UUID playerUUID;
    private final SocketAddress address;
    private long lastKeepAliveResponse;

    public ClientConnection(UUID playerUUID, SocketAddress address) {
        this.playerUUID = playerUUID;
        this.address = address;
        this.lastKeepAliveResponse = System.currentTimeMillis();
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public SocketAddress getAddress() {
        return this.address;
    }

    public long getLastKeepAliveResponse() {
        return this.lastKeepAliveResponse;
    }

    public void setLastKeepAliveResponse(long lastKeepAliveResponse) {
        this.lastKeepAliveResponse = lastKeepAliveResponse;
    }

    public void send(Server server, NetworkMessage message) throws Exception {
        server.getSocket().send(message.writeServer(server, this), this.address);
    }
}
