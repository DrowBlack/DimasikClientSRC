package dimasik.managers.mods.voicechat.voice.common;

import net.minecraft.network.PacketBuffer;

public interface Packet<T extends Packet> {
    public T fromBytes(PacketBuffer var1);

    public void toBytes(PacketBuffer var1);

    default public long getTTL() {
        return 10000L;
    }
}
