package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RemoveCategoryPacket
implements Packet<RemoveCategoryPacket> {
    public static final ResourceLocation REMOVE_CATEGORY = new ResourceLocation("voicechat", "remove_category");
    private String categoryId;

    public RemoveCategoryPacket() {
    }

    public RemoveCategoryPacket(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return REMOVE_CATEGORY;
    }

    @Override
    public RemoveCategoryPacket fromBytes(PacketBuffer buf) {
        this.categoryId = buf.readString(16);
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeString(this.categoryId, 16);
    }
}
