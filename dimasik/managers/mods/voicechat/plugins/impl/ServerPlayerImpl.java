package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.api.ServerLevel;
import dimasik.managers.mods.voicechat.api.ServerPlayer;
import dimasik.managers.mods.voicechat.plugins.impl.PlayerImpl;
import dimasik.managers.mods.voicechat.plugins.impl.ServerLevelImpl;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class ServerPlayerImpl
extends PlayerImpl
implements ServerPlayer {
    public ServerPlayerImpl(ServerPlayerEntity entity) {
        super(entity);
    }

    public ServerPlayerEntity getRealServerPlayer() {
        return (ServerPlayerEntity)this.entity;
    }

    @Override
    public ServerLevel getServerLevel() {
        return new ServerLevelImpl((ServerWorld)this.entity.world);
    }
}
