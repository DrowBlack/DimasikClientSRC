package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.Packet;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class JoinedGroupPacket
implements Packet<JoinedGroupPacket> {
    public static final ResourceLocation JOINED_GROUP = new ResourceLocation("voicechat", "joined_group");
    @Nullable
    private UUID group;
    private boolean wrongPassword;

    public JoinedGroupPacket() {
    }

    public JoinedGroupPacket(@Nullable UUID group, boolean wrongPassword) {
        this.group = group;
        this.wrongPassword = wrongPassword;
    }

    @Nullable
    public UUID getGroup() {
        return this.group;
    }

    public boolean isWrongPassword() {
        return this.wrongPassword;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return JOINED_GROUP;
    }

    @Override
    public JoinedGroupPacket fromBytes(PacketBuffer buf) {
        if (buf.readBoolean()) {
            this.group = buf.readUniqueId();
        }
        this.wrongPassword = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(this.group != null);
        if (this.group != null) {
            buf.writeUniqueId(this.group);
        }
        buf.writeBoolean(this.wrongPassword);
    }
}
