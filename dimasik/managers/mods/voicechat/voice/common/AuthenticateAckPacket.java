package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.Packet;
import net.minecraft.network.PacketBuffer;

public class AuthenticateAckPacket
implements Packet<AuthenticateAckPacket> {
    @Override
    public AuthenticateAckPacket fromBytes(PacketBuffer buf) {
        return new AuthenticateAckPacket();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
    }
}
