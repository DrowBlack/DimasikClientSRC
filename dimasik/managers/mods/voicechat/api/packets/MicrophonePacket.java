package dimasik.managers.mods.voicechat.api.packets;

import dimasik.managers.mods.voicechat.api.packets.ConvertablePacket;
import dimasik.managers.mods.voicechat.api.packets.Packet;

public interface MicrophonePacket
extends Packet,
ConvertablePacket {
    public boolean isWhispering();

    public byte[] getOpusEncodedData();

    public void setOpusEncodedData(byte[] var1);
}
