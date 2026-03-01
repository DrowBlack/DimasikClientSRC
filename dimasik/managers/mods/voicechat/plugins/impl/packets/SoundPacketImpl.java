package dimasik.managers.mods.voicechat.plugins.impl.packets;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.packets.EntitySoundPacket;
import dimasik.managers.mods.voicechat.api.packets.LocationalSoundPacket;
import dimasik.managers.mods.voicechat.api.packets.SoundPacket;
import dimasik.managers.mods.voicechat.api.packets.StaticSoundPacket;
import dimasik.managers.mods.voicechat.plugins.impl.PositionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.EntitySoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.LocationalSoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.StaticSoundPacketImpl;
import dimasik.managers.mods.voicechat.voice.common.GroupSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.LocationSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.PlayerSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.util.UUID;
import javax.annotation.Nullable;

public class SoundPacketImpl
implements dimasik.managers.mods.voicechat.api.packets.SoundPacket {
    private final SoundPacket<?> packet;

    public SoundPacketImpl(SoundPacket<?> packet) {
        this.packet = packet;
    }

    @Override
    public UUID getChannelId() {
        return this.packet.getChannelId();
    }

    @Override
    public UUID getSender() {
        return this.packet.getSender();
    }

    @Override
    public byte[] getOpusEncodedData() {
        return this.packet.getData();
    }

    @Override
    public long getSequenceNumber() {
        return this.packet.getSequenceNumber();
    }

    @Override
    @Nullable
    public String getCategory() {
        return this.packet.getCategory();
    }

    public SoundPacket<?> getPacket() {
        return this.packet;
    }

    @Override
    public EntitySoundPacket.Builder<?> entitySoundPacketBuilder() {
        return new EntitySoundPacketImpl.BuilderImpl(this);
    }

    @Override
    public LocationalSoundPacket.Builder<?> locationalSoundPacketBuilder() {
        return new LocationalSoundPacketImpl.BuilderImpl(this);
    }

    @Override
    public StaticSoundPacket.Builder<?> staticSoundPacketBuilder() {
        return new StaticSoundPacketImpl.BuilderImpl(this);
    }

    @Override
    public EntitySoundPacket toEntitySoundPacket(UUID entityUuid, boolean whispering) {
        return new EntitySoundPacketImpl(new PlayerSoundPacket(this.packet.getChannelId(), this.packet.getSender(), this.packet.getData(), this.packet.getSequenceNumber(), whispering, this.getDistance(), null));
    }

    @Override
    public LocationalSoundPacket toLocationalSoundPacket(Position position) {
        if (position instanceof PositionImpl) {
            PositionImpl p = (PositionImpl)position;
            return new LocationalSoundPacketImpl(new LocationSoundPacket(this.packet.getChannelId(), this.packet.getSender(), p.getPosition(), this.packet.getData(), this.packet.getSequenceNumber(), this.getDistance(), null));
        }
        throw new IllegalArgumentException("position is not an instance of PositionImpl");
    }

    private float getDistance() {
        if (this instanceof EntitySoundPacket) {
            EntitySoundPacket p = (EntitySoundPacket)((Object)this);
            return p.getDistance();
        }
        if (this instanceof LocationalSoundPacket) {
            LocationalSoundPacket p = (LocationalSoundPacket)((Object)this);
            return p.getDistance();
        }
        return Utils.getDefaultDistanceServer();
    }

    @Override
    public StaticSoundPacket toStaticSoundPacket() {
        return new StaticSoundPacketImpl(new GroupSoundPacket(this.packet.getChannelId(), this.packet.getSender(), this.packet.getData(), this.packet.getSequenceNumber(), null));
    }

    public static abstract class BuilderImpl<T extends BuilderImpl<T, P>, P extends dimasik.managers.mods.voicechat.api.packets.SoundPacket>
    implements SoundPacket.Builder<T, P> {
        protected UUID channelId;
        protected UUID sender;
        protected byte[] opusEncodedData;
        protected long sequenceNumber;
        @Nullable
        protected String category;

        public BuilderImpl(SoundPacketImpl soundPacket) {
            this.channelId = soundPacket.getChannelId();
            this.sender = soundPacket.getSender();
            this.opusEncodedData = soundPacket.getOpusEncodedData();
            this.sequenceNumber = soundPacket.getSequenceNumber();
            this.category = soundPacket.getCategory();
        }

        public BuilderImpl(UUID channelId, UUID sender, byte[] opusEncodedData, long sequenceNumber, @Nullable String category) {
            this.channelId = channelId;
            this.sender = sender;
            this.opusEncodedData = opusEncodedData;
            this.sequenceNumber = sequenceNumber;
            this.category = category;
        }

        @Override
        public T channelId(UUID channelId) {
            if (channelId == null) {
                throw new IllegalArgumentException("channelId can't be null");
            }
            this.channelId = channelId;
            return (T)this;
        }

        @Override
        public T opusEncodedData(byte[] data) {
            this.opusEncodedData = data;
            return (T)this;
        }

        @Override
        public T category(@Nullable String category) {
            this.category = category;
            return (T)this;
        }
    }
}
