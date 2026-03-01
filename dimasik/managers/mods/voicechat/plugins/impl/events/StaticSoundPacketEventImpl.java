package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.StaticSoundPacketEvent;
import dimasik.managers.mods.voicechat.api.packets.StaticSoundPacket;
import dimasik.managers.mods.voicechat.plugins.impl.events.SoundPacketEventImpl;
import javax.annotation.Nullable;

public class StaticSoundPacketEventImpl
extends SoundPacketEventImpl<StaticSoundPacket>
implements StaticSoundPacketEvent {
    public StaticSoundPacketEventImpl(StaticSoundPacket packet, @Nullable VoicechatConnection senderConnection, VoicechatConnection receiverConnection, String source) {
        super(packet, senderConnection, receiverConnection, source);
    }
}
