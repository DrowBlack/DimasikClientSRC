package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.Packet;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class AddGroupPacket
implements Packet<AddGroupPacket> {
    public static final ResourceLocation ADD_ADD_GROUP = new ResourceLocation("voicechat", "add_group");
    private ClientGroup group;

    public AddGroupPacket() {
    }

    public AddGroupPacket(ClientGroup group) {
        this.group = group;
    }

    public ClientGroup getGroup() {
        return this.group;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ADD_ADD_GROUP;
    }

    @Override
    public AddGroupPacket fromBytes(PacketBuffer buf) {
        this.group = ClientGroup.fromBytes(buf);
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        this.group.toBytes(buf);
    }
}
