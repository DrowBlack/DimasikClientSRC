package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.net.Packet;
import dimasik.managers.mods.voicechat.plugins.impl.GroupImpl;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class CreateGroupPacket
implements Packet<CreateGroupPacket> {
    public static final ResourceLocation CREATE_GROUP = new ResourceLocation("voicechat", "create_group");
    private String name;
    @Nullable
    private String password;
    private Group.Type type;

    public CreateGroupPacket() {
    }

    public CreateGroupPacket(String name, @Nullable String password, Group.Type type) {
        this.name = name;
        this.password = password;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    public Group.Type getType() {
        return this.type;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return CREATE_GROUP;
    }

    @Override
    public CreateGroupPacket fromBytes(PacketBuffer buf) {
        this.name = buf.readString(512);
        this.password = null;
        if (buf.readBoolean()) {
            this.password = buf.readString(512);
        }
        this.type = GroupImpl.TypeImpl.fromInt(buf.readShort());
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeString(this.name, 512);
        buf.writeBoolean(this.password != null);
        if (this.password != null) {
            buf.writeString(this.password, 512);
        }
        buf.writeShort(GroupImpl.TypeImpl.toInt(this.type));
    }
}
