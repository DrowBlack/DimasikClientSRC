package dimasik.managers.mods.voicechat.debug;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.voice.server.Server;
import io.netty.buffer.Unpooled;
import java.net.SocketAddress;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class PingHandler {
    public static final UUID PING_V1 = UUID.fromString("58bc9ae9-c7a8-45e4-a11c-efbb67199425");

    public static boolean onPacket(Server server, SocketAddress socketAddress, UUID playerID, PacketBuffer buf) {
        if (!Voicechat.SERVER_CONFIG.allowPings.get().booleanValue()) {
            return false;
        }
        if (!PING_V1.equals(playerID)) {
            return false;
        }
        try {
            byte[] payload = buf.readByteArray();
            PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(payload));
            UUID id = buffer.readUniqueId();
            long timestamp = buffer.readLong();
            Voicechat.LOGGER.debug("Received ping {} from {}", id, socketAddress);
            PacketBuffer responseBuffer = new PacketBuffer(Unpooled.buffer(24));
            responseBuffer.writeUniqueId(id);
            responseBuffer.writeLong(timestamp);
            byte[] response = new byte[responseBuffer.readableBytes()];
            responseBuffer.readBytes(response);
            server.getSocket().send(response, socketAddress);
        }
        catch (Exception e) {
            Voicechat.LOGGER.debug("Failed to send ping to {}: {}", socketAddress, e.getMessage());
        }
        return true;
    }
}
