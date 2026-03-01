package dimasik.managers.mods.voicechat.plugins.impl.audiochannel;

import dimasik.managers.mods.voicechat.api.audiochannel.ClientAudioChannel;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class ClientAudioChannelImpl
implements ClientAudioChannel {
    protected UUID id;
    @Nullable
    protected String category;

    public ClientAudioChannelImpl(UUID id) {
        this.id = id;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    protected abstract SoundPacket<?> createSoundPacket(short[] var1);

    @Override
    public void play(short[] rawAudio) {
        ClientVoicechat client = ClientManager.getClient();
        if (client != null) {
            client.processSoundPacket(this.createSoundPacket(rawAudio));
        }
    }

    @Override
    @Nullable
    public String getCategory() {
        return this.category;
    }

    @Override
    public void setCategory(@Nullable String category) {
        this.category = category;
    }
}
