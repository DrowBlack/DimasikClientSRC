package dimasik.managers.mods.voicechat.plugins.impl.audiochannel;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.audiochannel.ClientLocationalAudioChannel;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.ClientAudioChannelImpl;
import dimasik.managers.mods.voicechat.voice.client.ClientUtils;
import dimasik.managers.mods.voicechat.voice.common.LocationSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import java.util.UUID;
import net.minecraft.util.math.vector.Vector3d;

public class ClientLocationalAudioChannelImpl
extends ClientAudioChannelImpl
implements ClientLocationalAudioChannel {
    private Position position;
    private float distance;

    public ClientLocationalAudioChannelImpl(UUID id, Position position) {
        super(id);
        this.position = position;
        this.distance = ClientUtils.getDefaultDistanceClient();
    }

    @Override
    protected SoundPacket<?> createSoundPacket(short[] rawAudio) {
        return new LocationSoundPacket(this.id, this.id, rawAudio, new Vector3d(this.position.getX(), this.position.getY(), this.position.getZ()), this.distance, this.category);
    }

    @Override
    public void setLocation(Position position) {
        this.position = position;
    }

    @Override
    public Position getLocation() {
        return this.position;
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
