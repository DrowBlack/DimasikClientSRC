package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.Packet;
import net.minecraft.network.PacketBuffer;

public class KeepAlivePacket
implements Packet<KeepAlivePacket> {
    @Override
    public KeepAlivePacket fromBytes(PacketBuffer buf) {
        return new KeepAlivePacket();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
    }
}
