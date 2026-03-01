package dimasik.managers.mods.voicechat.plugins.impl.audiochannel;

import dimasik.managers.mods.voicechat.api.audiochannel.ClientStaticAudioChannel;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.ClientAudioChannelImpl;
import dimasik.managers.mods.voicechat.voice.common.GroupSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import java.util.UUID;

public class ClientStaticAudioChannelImpl
extends ClientAudioChannelImpl
implements ClientStaticAudioChannel {
    public ClientStaticAudioChannelImpl(UUID id) {
        super(id);
    }

    @Override
    protected SoundPacket<?> createSoundPacket(short[] rawAudio) {
        return new GroupSoundPacket(this.id, this.id, rawAudio, this.category);
    }
}
