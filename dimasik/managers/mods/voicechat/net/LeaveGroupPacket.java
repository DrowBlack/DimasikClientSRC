package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class LeaveGroupPacket
implements Packet<LeaveGroupPacket> {
    public static final ResourceLocation LEAVE_GROUP = new ResourceLocation("voicechat", "leave_group");

    @Override
    public ResourceLocation getIdentifier() {
        return LEAVE_GROUP;
    }

    @Override
    public LeaveGroupPacket fromBytes(PacketBuffer buf) {
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
    }
}
