package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.Packet;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class PingPacket
implements Packet<PingPacket> {
    private UUID id;
    private long timestamp;

    public PingPacket(UUID id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public PingPacket() {
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public UUID getId() {
        return this.id;
    }

    @Override
    public PingPacket fromBytes(PacketBuffer buf) {
        PingPacket soundPacket = new PingPacket();
        soundPacket.id = buf.readUniqueId();
        soundPacket.timestamp = buf.readLong();
        return soundPacket;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(this.id);
        buf.writeLong(this.timestamp);
    }
}
