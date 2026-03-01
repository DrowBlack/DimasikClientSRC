package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.PacketEvent;
import dimasik.managers.mods.voicechat.api.packets.Packet;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;
import javax.annotation.Nullable;

public class PacketEventImpl<T extends Packet>
extends ServerEventImpl
implements PacketEvent<T> {
    private final T packet;
    @Nullable
    private final VoicechatConnection receiverConnection;
    @Nullable
    private final VoicechatConnection senderConnection;

    public PacketEventImpl(T packet, @Nullable VoicechatConnection senderConnection, @Nullable VoicechatConnection receiverConnection) {
        this.packet = packet;
        this.senderConnection = senderConnection;
        this.receiverConnection = receiverConnection;
    }

    @Override
    public T getPacket() {
        return this.packet;
    }

    @Override
    @Nullable
    public VoicechatConnection getReceiverConnection() {
        return this.receiverConnection;
    }

    @Override
    @Nullable
    public VoicechatConnection getSenderConnection() {
        return this.senderConnection;
    }
}
