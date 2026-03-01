package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.Packet;
import dimasik.managers.mods.voicechat.plugins.impl.VolumeCategoryImpl;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class AddCategoryPacket
implements Packet<AddCategoryPacket> {
    public static final ResourceLocation ADD_CATEGORY = new ResourceLocation("voicechat", "add_category");
    private VolumeCategoryImpl category;

    public AddCategoryPacket() {
    }

    public AddCategoryPacket(VolumeCategoryImpl category) {
        this.category = category;
    }

    public VolumeCategoryImpl getCategory() {
        return this.category;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ADD_CATEGORY;
    }

    @Override
    public AddCategoryPacket fromBytes(PacketBuffer buf) {
        this.category = VolumeCategoryImpl.fromBytes(buf);
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        this.category.toBytes(buf);
    }
}
