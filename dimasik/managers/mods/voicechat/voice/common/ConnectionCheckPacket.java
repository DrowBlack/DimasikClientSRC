package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.Packet;
import net.minecraft.network.PacketBuffer;

public class ConnectionCheckPacket
implements Packet<ConnectionCheckPacket> {
    @Override
    public ConnectionCheckPacket fromBytes(PacketBuffer buf) {
        return new ConnectionCheckPacket();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
    }
}
