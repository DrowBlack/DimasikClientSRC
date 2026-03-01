package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.events.ClientReceiveSoundEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientEventImpl;
import java.util.UUID;
import javax.annotation.Nullable;

public class ClientReceiveSoundEventImpl
extends ClientEventImpl
implements ClientReceiveSoundEvent {
    private UUID id;
    private short[] rawAudio;

    public ClientReceiveSoundEventImpl(UUID id, short[] rawAudio) {
        this.id = id;
        this.rawAudio = rawAudio;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    @Nullable
    public short[] getRawAudio() {
        return this.rawAudio;
    }

    @Override
    public void setRawAudio(@Nullable short[] rawAudio) {
        this.rawAudio = rawAudio;
    }

    public static class StaticSoundImpl
    extends ClientReceiveSoundEventImpl
    implements ClientReceiveSoundEvent.StaticSound {
        public StaticSoundImpl(UUID id, short[] rawAudio) {
            super(id, rawAudio);
        }
    }

    public static class LocationalSoundImpl
    extends ClientReceiveSoundEventImpl
    implements ClientReceiveSoundEvent.LocationalSound {
        private Position position;
        private float distance;

        public LocationalSoundImpl(UUID id, short[] rawAudio, Position position, float distance) {
            super(id, rawAudio);
            this.position = position;
            this.distance = distance;
        }

        @Override
        public Position getPosition() {
            return this.position;
        }

        @Override
        public float getDistance() {
            return this.distance;
        }
    }

    public static class EntitySoundImpl
    extends ClientReceiveSoundEventImpl
    implements ClientReceiveSoundEvent.EntitySound {
        private boolean whispering;
        private float distance;

        public EntitySoundImpl(UUID id, short[] rawAudio, boolean whispering, float distance) {
            super(id, rawAudio);
            this.whispering = whispering;
            this.distance = distance;
        }

        @Override
        public boolean isWhispering() {
            return this.whispering;
        }

        @Override
        public float getDistance() {
            return this.distance;
        }
    }
}
