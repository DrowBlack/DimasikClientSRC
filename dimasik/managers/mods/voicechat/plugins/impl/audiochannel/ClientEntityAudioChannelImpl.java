package dimasik.managers.mods.voicechat.plugins.impl.audiochannel;

import dimasik.managers.mods.voicechat.api.audiochannel.ClientEntityAudioChannel;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.ClientAudioChannelImpl;
import dimasik.managers.mods.voicechat.voice.client.ClientUtils;
import dimasik.managers.mods.voicechat.voice.common.PlayerSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import java.util.UUID;

public class ClientEntityAudioChannelImpl
extends ClientAudioChannelImpl
implements ClientEntityAudioChannel {
    private boolean whispering = false;
    private float distance = ClientUtils.getDefaultDistanceClient();

    public ClientEntityAudioChannelImpl(UUID id) {
        super(id);
    }

    @Override
    protected SoundPacket<?> createSoundPacket(short[] rawAudio) {
        return new PlayerSoundPacket(this.id, this.id, rawAudio, this.whispering, this.distance, this.category);
    }

    @Override
    public void setWhispering(boolean whispering) {
        this.whispering = whispering;
    }

    @Override
    public boolean isWhispering() {
        return this.whispering;
    }

    @Override
    public float getDistance() {
        return this.distance;
    }

    @Override
    public void setDistance(float distance) {
        this.distance = distance;
    }
}
