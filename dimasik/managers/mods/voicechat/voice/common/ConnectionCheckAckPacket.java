package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.Packet;
import net.minecraft.network.PacketBuffer;

public class ConnectionCheckAckPacket
implements Packet<ConnectionCheckAckPacket> {
    @Override
    public ConnectionCheckAckPacket fromBytes(PacketBuffer buf) {
        return new ConnectionCheckAckPacket();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
    }
}
