package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.LocationalSoundPacketEvent;
import dimasik.managers.mods.voicechat.api.packets.LocationalSoundPacket;
import dimasik.managers.mods.voicechat.plugins.impl.events.SoundPacketEventImpl;
import javax.annotation.Nullable;

public class LocationalSoundPacketEventImpl
extends SoundPacketEventImpl<LocationalSoundPacket>
implements LocationalSoundPacketEvent {
    public LocationalSoundPacketEventImpl(LocationalSoundPacket packet, @Nullable VoicechatConnection senderConnection, VoicechatConnection receiverConnection, String source) {
        super(packet, senderConnection, receiverConnection, source);
    }
}
