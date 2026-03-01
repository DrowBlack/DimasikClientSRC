package dimasik.managers.mods.voicechat.plugins.impl.audiochannel;

import dimasik.managers.mods.voicechat.api.Entity;
import dimasik.managers.mods.voicechat.api.audiochannel.EntityAudioChannel;
import dimasik.managers.mods.voicechat.api.packets.MicrophonePacket;
import dimasik.managers.mods.voicechat.plugins.impl.EntityImpl;
import dimasik.managers.mods.voicechat.plugins.impl.ServerPlayerImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.AudioChannelImpl;
import dimasik.managers.mods.voicechat.voice.common.PlayerSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import dimasik.managers.mods.voicechat.voice.server.Server;
import dimasik.managers.mods.voicechat.voice.server.ServerWorldUtils;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class EntityAudioChannelImpl
extends AudioChannelImpl
implements EntityAudioChannel {
    protected Entity entity;
    protected boolean whispering;
    protected float distance;

    public EntityAudioChannelImpl(UUID channelId, Server server, Entity entity) {
        super(channelId, server);
        this.entity = entity;
        this.whispering = false;
        this.distance = Utils.getDefaultDistanceServer();
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
    public void updateEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
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
        this.broadcast(new PlayerSoundPacket(this.channelId, this.entity.getUuid(), opusData, this.sequenceNumber.getAndIncrement(), this.whispering, this.distance, this.category));
    }

    @Override
    public void send(MicrophonePacket microphonePacket) {
        this.broadcast(new PlayerSoundPacket(this.channelId, this.entity.getUuid(), microphonePacket.getOpusEncodedData(), this.sequenceNumber.getAndIncrement(), this.whispering, this.distance, this.category));
    }

    @Override
    public void flush() {
        this.broadcast(new PlayerSoundPacket(this.channelId, this.entity.getUuid(), new byte[0], this.sequenceNumber.getAndIncrement(), this.whispering, this.distance, this.category));
    }

    private void broadcast(PlayerSoundPacket packet) {
        if (!(this.entity instanceof EntityImpl)) {
            throw new IllegalArgumentException("entity is not an instance of EntityImpl");
        }
        EntityImpl entityimpl = (EntityImpl)this.entity;
        this.server.broadcast(ServerWorldUtils.getPlayersInRange((ServerWorld)entityimpl.getRealEntity().world, entityimpl.getRealEntity().getEyePosition(1.0f), this.server.getBroadcastRange(this.distance), this.filter == null ? player -> true : player -> this.filter.test(new ServerPlayerImpl((ServerPlayerEntity)player))), packet, null, null, null, "plugin");
    }
}
