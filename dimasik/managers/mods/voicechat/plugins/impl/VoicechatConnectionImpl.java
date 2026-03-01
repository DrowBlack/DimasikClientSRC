package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.ServerPlayer;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.plugins.impl.GroupImpl;
import dimasik.managers.mods.voicechat.plugins.impl.ServerPlayerImpl;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import dimasik.managers.mods.voicechat.voice.server.PlayerStateManager;
import dimasik.managers.mods.voicechat.voice.server.Server;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;

public class VoicechatConnectionImpl
implements VoicechatConnection {
    private final ServerPlayer player;
    private final ServerPlayerEntity serverPlayer;
    private final PlayerState state;
    @Nullable
    private final Group group;

    public VoicechatConnectionImpl(ServerPlayerEntity player, PlayerState state) {
        this.serverPlayer = player;
        this.player = new ServerPlayerImpl(player);
        this.state = state;
        this.group = GroupImpl.create(state);
    }

    @Nullable
    public static VoicechatConnectionImpl fromPlayer(@Nullable ServerPlayerEntity player) {
        if (player == null) {
            return null;
        }
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return null;
        }
        PlayerState state = server.getPlayerStateManager().getState(player.getUniqueID());
        if (state == null) {
            return null;
        }
        return new VoicechatConnectionImpl(player, state);
    }

    @Override
    @Nullable
    public Group getGroup() {
        return this.group;
    }

    @Override
    public boolean isInGroup() {
        return this.group != null;
    }

    @Override
    public void setGroup(@Nullable Group group) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return;
        }
        if (group == null) {
            server.getGroupManager().leaveGroup(this.serverPlayer);
            return;
        }
        if (group instanceof GroupImpl) {
            GroupImpl g = (GroupImpl)group;
            dimasik.managers.mods.voicechat.voice.server.Group actualGroup = server.getGroupManager().getGroup(g.getGroup().getId());
            if (actualGroup == null) {
                server.getGroupManager().addGroup(g.getGroup(), this.serverPlayer);
                actualGroup = g.getGroup();
            }
            server.getGroupManager().joinGroup(actualGroup, this.serverPlayer, g.getGroup().getPassword());
        }
    }

    @Override
    public boolean isConnected() {
        return !this.state.isDisconnected();
    }

    @Override
    public void setConnected(boolean connected) {
        if (this.isInstalled()) {
            return;
        }
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return;
        }
        PlayerStateManager manager = server.getPlayerStateManager();
        PlayerState actualState = manager.getState(this.state.getUuid());
        if (actualState == null) {
            return;
        }
        if (actualState.isDisconnected() != connected) {
            return;
        }
        actualState.setDisconnected(!connected);
        manager.broadcastState(actualState);
    }

    @Override
    public boolean isDisabled() {
        return this.state.isDisabled();
    }

    @Override
    public void setDisabled(boolean disabled) {
        if (this.isInstalled()) {
            return;
        }
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return;
        }
        PlayerStateManager manager = server.getPlayerStateManager();
        PlayerState actualState = manager.getState(this.state.getUuid());
        if (actualState == null) {
            return;
        }
        if (actualState.isDisabled() == disabled) {
            return;
        }
        actualState.setDisabled(disabled);
        manager.broadcastState(actualState);
    }

    @Override
    public boolean isInstalled() {
        return Voicechat.SERVER.isCompatible(this.serverPlayer);
    }

    @Override
    public ServerPlayer getPlayer() {
        return this.player;
    }
}
