package dimasik.managers.mods.voicechat.plugins.impl.audiochannel;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.ServerLevel;
import dimasik.managers.mods.voicechat.api.audiochannel.LocationalAudioChannel;
import dimasik.managers.mods.voicechat.api.packets.MicrophonePacket;
import dimasik.managers.mods.voicechat.plugins.impl.PositionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.ServerLevelImpl;
import dimasik.managers.mods.voicechat.plugins.impl.ServerPlayerImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.AudioChannelImpl;
import dimasik.managers.mods.voicechat.voice.common.LocationSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import dimasik.managers.mods.voicechat.voice.server.Server;
import dimasik.managers.mods.voicechat.voice.server.ServerWorldUtils;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;

public class LocationalAudioChannelImpl
extends AudioChannelImpl
implements LocationalAudioChannel {
    protected ServerLevel level;
    protected PositionImpl position;
    protected float distance;

    public LocationalAudioChannelImpl(UUID channelId, Server server, ServerLevel level, PositionImpl position) {
        super(channelId, server);
        this.level = level;
        this.position = position;
        this.distance = Utils.getDefaultDistanceServer();
    }

    @Override
    public void updateLocation(Position position) {
        if (!(position instanceof PositionImpl)) {
            throw new IllegalArgumentException("position is not an instance of PositionImpl");
        }
        this.position = (PositionImpl)position;
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

    @Override
    public void send(byte[] opusData) {
        this.broadcast(new LocationSoundPacket(this.channelId, this.channelId, this.position.getPosition(), opusData, this.sequenceNumber.getAndIncrement(), this.distance, this.category));
    }

    @Override
    public void send(MicrophonePacket packet) {
        this.send(packet.getOpusEncodedData());
    }

    @Override
    public void flush() {
        this.broadcast(new LocationSoundPacket(this.channelId, this.channelId, this.position.getPosition(), new byte[0], this.sequenceNumber.getAndIncrement(), this.distance, this.category));
    }

    private void broadcast(LocationSoundPacket packet) {
        if (!(this.level instanceof ServerLevelImpl)) {
            throw new IllegalArgumentException("level is not an instance of ServerLevelImpl");
        }
        ServerLevelImpl serverLevel = (ServerLevelImpl)this.level;
        this.server.broadcast(ServerWorldUtils.getPlayersInRange(serverLevel.getRawServerLevel(), this.position.getPosition(), this.server.getBroadcastRange(this.distance), this.filter == null ? player -> true : player -> this.filter.test(new ServerPlayerImpl((ServerPlayerEntity)player))), packet, null, null, null, "plugin");
    }
}
