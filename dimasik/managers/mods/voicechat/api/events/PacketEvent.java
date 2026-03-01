package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.ServerEvent;
import dimasik.managers.mods.voicechat.api.packets.Packet;
import javax.annotation.Nullable;

public interface PacketEvent<T extends Packet>
extends ServerEvent {
    public T getPacket();

    @Nullable
    public VoicechatConnection getReceiverConnection();

    @Nullable
    public VoicechatConnection getSenderConnection();
}
