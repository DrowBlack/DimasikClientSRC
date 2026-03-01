package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.Packet;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RemoveGroupPacket
implements Packet<RemoveGroupPacket> {
    public static final ResourceLocation REMOVE_GROUP = new ResourceLocation("voicechat", "remove_group");
    private UUID groupId;

    public RemoveGroupPacket() {
    }

    public RemoveGroupPacket(UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getGroupId() {
        return this.groupId;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return REMOVE_GROUP;
    }

    @Override
    public RemoveGroupPacket fromBytes(PacketBuffer buf) {
        this.groupId = buf.readUniqueId();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(this.groupId);
    }
}
