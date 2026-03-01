package dimasik.managers.mods.voicechat.net;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface Packet<T extends Packet<T>> {
    public ResourceLocation getIdentifier();

    public T fromBytes(PacketBuffer var1);

    public void toBytes(PacketBuffer var1);
}
