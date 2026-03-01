package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;

public class GroupSoundPacket
extends SoundPacket<GroupSoundPacket> {
    public GroupSoundPacket(UUID channelId, UUID sender, byte[] data, long sequenceNumber, @Nullable String category) {
        super(channelId, sender, data, sequenceNumber, category);
    }

    public GroupSoundPacket(UUID channelId, UUID sender, short[] data, @Nullable String category) {
        super(channelId, sender, data, category);
    }

    public GroupSoundPacket() {
    }

    @Override
    public GroupSoundPacket fromBytes(PacketBuffer buf) {
        GroupSoundPacket soundPacket = new GroupSoundPacket();
        soundPacket.channelId = buf.readUniqueId();
        soundPacket.sender = buf.readUniqueId();
        soundPacket.data = buf.readByteArray();
        soundPacket.sequenceNumber = buf.readLong();
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
        buf.writeByteArray(this.data);
        buf.writeLong(this.sequenceNumber);
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
