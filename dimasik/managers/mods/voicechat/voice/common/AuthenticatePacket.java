package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.Packet;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class AuthenticatePacket
implements Packet<AuthenticatePacket> {
    private UUID playerUUID;
    private UUID secret;

    public AuthenticatePacket(UUID playerUUID, UUID secret) {
        this.playerUUID = playerUUID;
        this.secret = secret;
    }

    public AuthenticatePacket() {
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public UUID getSecret() {
        return this.secret;
    }

    @Override
    public AuthenticatePacket fromBytes(PacketBuffer buf) {
        AuthenticatePacket packet = new AuthenticatePacket();
        packet.playerUUID = buf.readUniqueId();
        packet.secret = buf.readUniqueId();
        return packet;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(this.playerUUID);
        buf.writeUniqueId(this.secret);
    }
}
