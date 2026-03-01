package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.MicrophonePacketEvent;
import dimasik.managers.mods.voicechat.api.packets.MicrophonePacket;
import dimasik.managers.mods.voicechat.plugins.impl.events.PacketEventImpl;

public class MicrophonePacketEventImpl
extends PacketEventImpl<MicrophonePacket>
implements MicrophonePacketEvent {
    public MicrophonePacketEventImpl(MicrophonePacket packet, VoicechatConnection connection) {
        super(packet, connection, null);
    }
}
