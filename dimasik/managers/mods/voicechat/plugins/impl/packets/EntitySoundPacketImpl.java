package dimasik.managers.mods.voicechat.plugins.impl.packets;

import dimasik.managers.mods.voicechat.api.packets.EntitySoundPacket;
import dimasik.managers.mods.voicechat.plugins.impl.packets.LocationalSoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.SoundPacketImpl;
import dimasik.managers.mods.voicechat.voice.common.PlayerSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntitySoundPacketImpl
extends SoundPacketImpl
implements EntitySoundPacket {
    private final PlayerSoundPacket packet;

    public EntitySoundPacketImpl(PlayerSoundPacket packet) {
        super(packet);
        this.packet = packet;
    }

    @Override
    public UUID getEntityUuid() {
        return this.packet.getSender();
    }

    @Override
    public boolean isWhispering() {
        return this.packet.isWhispering();
    }

    @Override
    public float getDistance() {
        return this.packet.getDistance();
    }

    public PlayerSoundPacket getPacket() {
        return this.packet;
    }

    @Override
    public UUID getChannelId() {
        return this.packet.getChannelId();
    }

    public static class BuilderImpl
    extends SoundPacketImpl.BuilderImpl<BuilderImpl, EntitySoundPacket>
    implements EntitySoundPacket.Builder<BuilderImpl> {
        protected UUID entityUuid;
        protected boolean whispering;
        protected float distance;

        public BuilderImpl(SoundPacketImpl soundPacket) {
            super(soundPacket);
            if (soundPacket instanceof EntitySoundPacketImpl) {
                EntitySoundPacketImpl p = (EntitySoundPacketImpl)soundPacket;
                this.entityUuid = p.getEntityUuid();
                this.whispering = p.isWhispering();
                this.distance = p.getDistance();
            } else if (soundPacket instanceof LocationalSoundPacketImpl) {
                LocationalSoundPacketImpl p = (LocationalSoundPacketImpl)soundPacket;
                this.distance = p.getDistance();
            } else {
                this.distance = Utils.getDefaultDistanceServer();
            }
        }

        public BuilderImpl(UUID channelId, UUID sender, byte[] opusEncodedData, long sequenceNumber, @Nullable String category) {
            super(channelId, sender, opusEncodedData, sequenceNumber, category);
            this.distance = Utils.getDefaultDistanceServer();
        }

        @Override
        public BuilderImpl entityUuid(UUID entityUuid) {
            this.entityUuid = entityUuid;
            return this;
        }

        @Override
        public BuilderImpl whispering(boolean whispering) {
            this.whispering = whispering;
            return this;
        }

        @Override
        public BuilderImpl distance(float distance) {
            this.distance = distance;
            return this;
        }

        @Override
        public EntitySoundPacket build() {
            if (this.entityUuid == null) {
                throw new IllegalStateException("entityUuid missing");
            }
            return new EntitySoundPacketImpl(new PlayerSoundPacket(this.channelId, this.sender, this.opusEncodedData, this.sequenceNumber, this.whispering, this.distance, this.category));
        }
    }
}
