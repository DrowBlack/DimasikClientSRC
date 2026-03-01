package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;

public class PlayerSoundPacket
extends SoundPacket<PlayerSoundPacket> {
    protected boolean whispering;
    protected float distance;

    public PlayerSoundPacket(UUID channelId, UUID sender, byte[] data, long sequenceNumber, boolean whispering, float distance, @Nullable String category) {
        super(channelId, sender, data, sequenceNumber, category);
        this.whispering = whispering;
        this.distance = distance;
    }

    public PlayerSoundPacket(UUID channelId, UUID sender, short[] data, boolean whispering, float distance, @Nullable String category) {
        super(channelId, sender, data, category);
        this.whispering = whispering;
        this.distance = distance;
    }

    public PlayerSoundPacket() {
    }

    @Override
    public UUID getSender() {
        return this.sender;
    }

    public boolean isWhispering() {
        return this.whispering;
    }

    public float getDistance() {
        return this.distance;
    }

    @Override
    public PlayerSoundPacket fromBytes(PacketBuffer buf) {
        PlayerSoundPacket soundPacket = new PlayerSoundPacket();
        soundPacket.channelId = buf.readUniqueId();
        soundPacket.sender = buf.readUniqueId();
        soundPacket.data = buf.readByteArray();
        soundPacket.sequenceNumber = buf.readLong();
        soundPacket.distance = buf.readFloat();
        byte data = buf.readByte();
        soundPacket.whispering = this.hasFlag(data, (byte)1);
        if (this.hasFlag(data, (byte)2)) {
            soundPacket.category = buf.readString(16);
        }
        return soundPacket;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(this.channelId);
        buf.writeUniqueId(this.sender);
        buf.writeByteArray(this.data);
        buf.writeLong(this.sequenceNumber);
        buf.writeFloat(this.distance);
        byte data = 0;
        if (this.whispering) {
            data = this.setFlag(data, (byte)1);
        }
        if (this.category != null) {
            data = this.setFlag(data, (byte)2);
        }
        buf.writeByte(data);
        if (this.category != null) {
            buf.writeString(this.category, 16);
        }
    }
}
