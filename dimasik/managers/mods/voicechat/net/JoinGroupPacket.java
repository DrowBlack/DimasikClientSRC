package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.Packet;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class JoinGroupPacket
implements Packet<JoinGroupPacket> {
    public static final ResourceLocation SET_GROUP = new ResourceLocation("voicechat", "set_group");
    private UUID group;
    @Nullable
    private String password;

    public JoinGroupPacket() {
    }

    public JoinGroupPacket(UUID group, @Nullable String password) {
        this.group = group;
        this.password = password;
    }

    public UUID getGroup() {
        return this.group;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return SET_GROUP;
    }

    @Override
    public JoinGroupPacket fromBytes(PacketBuffer buf) {
        this.group = buf.readUniqueId();
        if (buf.readBoolean()) {
            this.password = buf.readString(512);
        }
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(this.group);
        buf.writeBoolean(this.password != null);
        if (this.password != null) {
            buf.writeString(this.password, 512);
        }
    }
}
