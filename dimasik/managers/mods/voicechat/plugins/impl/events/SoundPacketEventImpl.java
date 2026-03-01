package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.SoundPacketEvent;
import dimasik.managers.mods.voicechat.api.packets.Packet;
import dimasik.managers.mods.voicechat.plugins.impl.events.PacketEventImpl;
import javax.annotation.Nullable;

public class SoundPacketEventImpl<T extends Packet>
extends PacketEventImpl<T>
implements SoundPacketEvent<T> {
    private final String source;

    public SoundPacketEventImpl(T packet, @Nullable VoicechatConnection senderConnection, @Nullable VoicechatConnection receiverConnection, String source) {
        super(packet, senderConnection, receiverConnection);
        this.source = source;
    }

    @Override
    public String getSource() {
        return this.source;
    }
}
