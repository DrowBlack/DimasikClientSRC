package dimasik.managers.mods.voicechat.voice.server;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.net.NetManager;
import dimasik.managers.mods.voicechat.net.PlayerStatePacket;
import dimasik.managers.mods.voicechat.net.PlayerStatesPacket;
import dimasik.managers.mods.voicechat.plugins.PluginManager;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;

public class PlayerStateManager {
    private final ConcurrentHashMap<UUID, PlayerState> states;
    private final Server voicechatServer;

    public PlayerStateManager(Server voicechatServer) {
        this.voicechatServer = voicechatServer;
        this.states = new ConcurrentHashMap();
        CommonCompatibilityManager.INSTANCE.getNetManager().updateStateChannel.setServerListener((server, player, handler, packet) -> {
            PlayerState state = this.states.get(player.getUniqueID());
            if (state == null) {
                state = PlayerStateManager.defaultDisconnectedState(player);
            }
            state.setDisabled(packet.isDisabled());
            this.states.put(player.getUniqueID(), state);
            this.broadcastState(state);
            Voicechat.LOGGER.debug("Got state of {}: {}", player.getName().getString(), state);
        });
    }

    public void broadcastState(PlayerState state) {
        PlayerStatePacket packet = new PlayerStatePacket(state);
        this.voicechatServer.getServer().getPlayerList().getPlayers().forEach(p -> NetManager.sendToClient(p, packet));
        PluginManager.instance().onPlayerStateChanged(state);
    }

    public void onPlayerCompatibilityCheckSucceeded(ServerPlayerEntity player) {
        PlayerStatesPacket packet = new PlayerStatesPacket(this.states);
        NetManager.sendToClient(player, packet);
        Voicechat.LOGGER.debug("Sending initial states to {}", player.getName().getString());
    }

    public void onPlayerLoggedIn(ServerPlayerEntity player) {
        PlayerState state = PlayerStateManager.defaultDisconnectedState(player);
        this.states.put(player.getUniqueID(), state);
        this.broadcastState(state);
        Voicechat.LOGGER.debug("Setting default state of {}: {}", player.getName().getString(), state);
    }

    public void onPlayerLoggedOut(ServerPlayerEntity player) {
        this.states.remove(player.getUniqueID());
        this.broadcastState(new PlayerState(player.getUniqueID(), player.getGameProfile().getName(), false, true));
        Voicechat.LOGGER.debug("Removing state of {}", player.getName().getString());
    }

    public void onPlayerVoicechatDisconnect(UUID uuid) {
        PlayerState state = this.states.get(uuid);
        if (state == null) {
            return;
        }
        state.setDisconnected(true);
        this.broadcastState(state);
        Voicechat.LOGGER.debug("Set state of {} to disconnected: {}", uuid, state);
    }

    public void onPlayerVoicechatConnect(ServerPlayerEntity player) {
        PlayerState state = this.states.get(player.getUniqueID());
        if (state == null) {
            state = PlayerStateManager.defaultDisconnectedState(player);
        }
        state.setDisconnected(false);
        this.states.put(player.getUniqueID(), state);
        this.broadcastState(state);
        Voicechat.LOGGER.debug("Set state of {} to connected: {}", player.getName().getString(), state);
    }

    @Nullable
    public PlayerState getState(UUID playerUUID) {
        return this.states.get(playerUUID);
    }

    public static PlayerState defaultDisconnectedState(ServerPlayerEntity player) {
        return new PlayerState(player.getUniqueID(), player.getGameProfile().getName(), false, true);
    }

    public void setGroup(ServerPlayerEntity player, @Nullable UUID group) {
        PlayerState state = this.states.get(player.getUniqueID());
        if (state == null) {
            state = PlayerStateManager.defaultDisconnectedState(player);
            Voicechat.LOGGER.debug("Defaulting to default state for {}: {}", player.getName().getString(), state);
        }
        state.setGroup(group);
        this.states.put(player.getUniqueID(), state);
        this.broadcastState(state);
        Voicechat.LOGGER.debug("Setting group of {}: {}", player.getName().getString(), state);
    }

    public Collection<PlayerState> getStates() {
        return this.states.values();
    }
}
