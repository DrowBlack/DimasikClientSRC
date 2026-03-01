package dimasik.managers.mods.voicechat.plugins.impl.packets;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.packets.EntitySoundPacket;
import dimasik.managers.mods.voicechat.api.packets.LocationalSoundPacket;
import dimasik.managers.mods.voicechat.api.packets.MicrophonePacket;
import dimasik.managers.mods.voicechat.api.packets.StaticSoundPacket;
import dimasik.managers.mods.voicechat.plugins.impl.PositionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.EntitySoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.LocationalSoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.StaticSoundPacketImpl;
import dimasik.managers.mods.voicechat.voice.common.GroupSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.LocationSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.MicPacket;
import dimasik.managers.mods.voicechat.voice.common.PlayerSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.util.Objects;
import java.util.UUID;

public class MicrophonePacketImpl
implements MicrophonePacket {
    private final MicPacket packet;
    private final UUID sender;

    public MicrophonePacketImpl(MicPacket packet, UUID sender) {
        this.packet = packet;
        this.sender = sender;
    }

    @Override
    public boolean isWhispering() {
        return this.packet.isWhispering();
    }

    @Override
    public byte[] getOpusEncodedData() {
        return this.packet.getData();
    }

    @Override
    public void setOpusEncodedData(byte[] data) {
        this.packet.setData(Objects.requireNonNull(data));
    }

    @Override
    public EntitySoundPacket.Builder<?> entitySoundPacketBuilder() {
        return new EntitySoundPacketImpl.BuilderImpl(this.sender, this.sender, this.packet.getData(), this.packet.getSequenceNumber(), null);
    }

    @Override
    public LocationalSoundPacket.Builder<?> locationalSoundPacketBuilder() {
        return new LocationalSoundPacketImpl.BuilderImpl(this.sender, this.sender, this.packet.getData(), this.packet.getSequenceNumber(), null);
    }

    @Override
    public StaticSoundPacket.Builder<?> staticSoundPacketBuilder() {
        return new StaticSoundPacketImpl.BuilderImpl(this.sender, this.sender, this.packet.getData(), this.packet.getSequenceNumber(), null);
    }

    @Override
    @Deprecated
    public EntitySoundPacket toEntitySoundPacket(UUID entityUuid, boolean whispering) {
        return new EntitySoundPacketImpl(new PlayerSoundPacket(this.sender, this.sender, this.packet.getData(), this.packet.getSequenceNumber(), whispering, Utils.getDefaultDistanceServer(), null));
    }

    @Override
    @Deprecated
    public LocationalSoundPacket toLocationalSoundPacket(Position position) {
        if (position instanceof PositionImpl) {
            PositionImpl p = (PositionImpl)position;
            return new LocationalSoundPacketImpl(new LocationSoundPacket(this.sender, this.sender, p.getPosition(), this.packet.getData(), this.packet.getSequenceNumber(), Utils.getDefaultDistanceServer(), null));
        }
        throw new IllegalArgumentException("position is not an instance of PositionImpl");
    }

    @Override
    @Deprecated
    public StaticSoundPacket toStaticSoundPacket() {
        return new StaticSoundPacketImpl(new GroupSoundPacket(this.sender, this.sender, this.packet.getData(), this.packet.getSequenceNumber(), null));
    }
}
