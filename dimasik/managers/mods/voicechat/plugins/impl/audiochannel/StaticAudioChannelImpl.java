package dimasik.managers.mods.voicechat.plugins.impl.audiochannel;

import dimasik.managers.mods.voicechat.api.audiochannel.StaticAudioChannel;
import dimasik.managers.mods.voicechat.api.packets.MicrophonePacket;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatConnectionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatServerApiImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.AudioChannelImpl;
import dimasik.managers.mods.voicechat.voice.common.GroupSoundPacket;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.UUID;

public class StaticAudioChannelImpl
extends AudioChannelImpl
implements StaticAudioChannel {
    protected VoicechatConnectionImpl connection;

    public StaticAudioChannelImpl(UUID channelId, Server server, VoicechatConnectionImpl connection) {
        super(channelId, server);
        this.connection = connection;
    }

    @Override
    public void send(byte[] opusData) {
        this.broadcast(new GroupSoundPacket(this.channelId, this.channelId, opusData, this.sequenceNumber.getAndIncrement(), this.category));
    }

    @Override
    public void send(MicrophonePacket packet) {
        this.send(packet.getOpusEncodedData());
    }

    @Override
    public void flush() {
        GroupSoundPacket packet = new GroupSoundPacket(this.channelId, this.channelId, new byte[0], this.sequenceNumber.getAndIncrement(), this.category);
        this.broadcast(packet);
    }

    private void broadcast(GroupSoundPacket packet) {
        VoicechatServerApiImpl.sendPacket(this.connection, packet);
    }
}
