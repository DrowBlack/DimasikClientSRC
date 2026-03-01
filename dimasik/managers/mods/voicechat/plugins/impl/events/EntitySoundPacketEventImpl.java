package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.EntitySoundPacketEvent;
import dimasik.managers.mods.voicechat.api.packets.EntitySoundPacket;
import dimasik.managers.mods.voicechat.plugins.impl.events.SoundPacketEventImpl;
import javax.annotation.Nullable;

public class EntitySoundPacketEventImpl
extends SoundPacketEventImpl<EntitySoundPacket>
implements EntitySoundPacketEvent {
    public EntitySoundPacketEventImpl(EntitySoundPacket packet, @Nullable VoicechatConnection senderConnection, VoicechatConnection receiverConnection, String source) {
        super(packet, senderConnection, receiverConnection, source);
    }
}
