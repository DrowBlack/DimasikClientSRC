package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

public class LocationSoundPacket
extends SoundPacket<LocationSoundPacket> {
    protected Vector3d location;
    protected float distance;

    public LocationSoundPacket(UUID channelId, UUID sender, Vector3d location, byte[] data, long sequenceNumber, float distance, @Nullable String category) {
        super(channelId, sender, data, sequenceNumber, category);
        this.location = location;
        this.distance = distance;
    }

    public LocationSoundPacket(UUID channelId, UUID sender, short[] data, Vector3d location, float distance, @Nullable String category) {
        super(channelId, sender, data, category);
        this.location = location;
        this.distance = distance;
    }

    public LocationSoundPacket() {
    }

    public Vector3d getLocation() {
        return this.location;
    }

    public float getDistance() {
        return this.distance;
    }

    @Override
    public LocationSoundPacket fromBytes(PacketBuffer buf) {
        LocationSoundPacket soundPacket = new LocationSoundPacket();
        soundPacket.channelId = buf.readUniqueId();
        soundPacket.sender = buf.readUniqueId();
        soundPacket.location = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        soundPacket.data = buf.readByteArray();
        soundPacket.sequenceNumber = buf.readLong();
        soundPacket.distance = buf.readFloat();
        byte data = buf.readByte();
        if (this.hasFlag(data, (byte)2)) {
            soundPacket.category = buf.readString(16);
        }
        return soundPacket;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(this.channelId);
        buf.writeUniqueId(this.sender);
        buf.writeDouble(this.location.x);
        buf.writeDouble(this.location.y);
        buf.writeDouble(this.location.z);
        buf.writeByteArray(this.data);
        buf.writeLong(this.sequenceNumber);
        buf.writeFloat(this.distance);
        byte data = 0;
        if (this.category != null) {
            data = this.setFlag(data, (byte)2);
        }
        buf.writeByte(data);
        if (this.category != null) {
            buf.writeString(this.category, 16);
        }
    }
}
