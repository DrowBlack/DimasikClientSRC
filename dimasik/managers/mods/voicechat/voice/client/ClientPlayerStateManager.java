package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.api.events.ClientVoicechatConnectionEvent;
import dimasik.managers.mods.voicechat.api.events.MicrophoneMuteEvent;
import dimasik.managers.mods.voicechat.api.events.VoicechatDisableEvent;
import dimasik.managers.mods.voicechat.gui.CreateGroupScreen;
import dimasik.managers.mods.voicechat.gui.EnterPasswordScreen;
import dimasik.managers.mods.voicechat.gui.group.GroupList;
import dimasik.managers.mods.voicechat.gui.group.GroupScreen;
import dimasik.managers.mods.voicechat.gui.group.JoinGroupList;
import dimasik.managers.mods.voicechat.gui.group.JoinGroupScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingManager;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumeList;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.net.ClientServerNetManager;
import dimasik.managers.mods.voicechat.net.UpdateStatePacket;
import dimasik.managers.mods.voicechat.plugins.PluginManager;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientVoicechatConnectionEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.MicrophoneMuteEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.VoicechatDisableEventImpl;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechatConnection;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ClientPlayerStateManager {
    private boolean disconnected = true;
    @Nullable
    private UUID group = null;
    private Map<UUID, PlayerState> states = new HashMap<UUID, PlayerState>();

    public ClientPlayerStateManager() {
        ClientServerNetManager.setClientListener(CommonCompatibilityManager.INSTANCE.getNetManager().playerStateChannel, (client, handler, packet) -> {
            ClientVoicechat c;
            this.states.put(packet.getPlayerState().getUuid(), packet.getPlayerState());
            Voicechat.LOGGER.debug("Got state for {}: {}", packet.getPlayerState().getName(), packet.getPlayerState());
            VoicechatClient.USERNAME_CACHE.updateUsernameAndSave(packet.getPlayerState().getUuid(), packet.getPlayerState().getName());
            if (packet.getPlayerState().isDisconnected() && (c = ClientManager.getClient()) != null) {
                c.closeAudioChannel(packet.getPlayerState().getUuid());
            }
            AdjustVolumeList.update();
            JoinGroupList.update();
            GroupList.update();
        });
        ClientServerNetManager.setClientListener(CommonCompatibilityManager.INSTANCE.getNetManager().playerStatesChannel, (client, handler, packet) -> {
            this.states = packet.getPlayerStates();
            Voicechat.LOGGER.debug("Received {} state(s)", this.states.size());
            for (PlayerState state : this.states.values()) {
                VoicechatClient.USERNAME_CACHE.updateUsername(state.getUuid(), state.getName());
            }
            VoicechatClient.USERNAME_CACHE.save();
            AdjustVolumeList.update();
            JoinGroupList.update();
            GroupList.update();
        });
        ClientServerNetManager.setClientListener(CommonCompatibilityManager.INSTANCE.getNetManager().joinedGroupChannel, (client, handler, packet) -> {
            Screen screen = Minecraft.getInstance().currentScreen;
            this.group = packet.getGroup();
            if (packet.isWrongPassword()) {
                if (screen instanceof JoinGroupScreen || screen instanceof CreateGroupScreen || screen instanceof EnterPasswordScreen) {
                    Minecraft.getInstance().displayGuiScreen(null);
                }
                client.player.sendStatusMessage(new TranslationTextComponent("message.voicechat.wrong_password").mergeStyle(TextFormatting.DARK_RED), true);
            } else if (this.group != null && screen instanceof JoinGroupScreen || screen instanceof CreateGroupScreen || screen instanceof EnterPasswordScreen) {
                ClientGroup clientGroup = this.getGroup();
                if (clientGroup != null) {
                    Minecraft.getInstance().displayGuiScreen(new GroupScreen(clientGroup));
                } else {
                    Voicechat.LOGGER.warn("Received join group packet without group being present", new Object[0]);
                }
            }
            GroupList.update();
        });
        ClientCompatibilityManager.INSTANCE.onVoiceChatConnected(this::onVoiceChatConnected);
        ClientCompatibilityManager.INSTANCE.onVoiceChatDisconnected(this::onVoiceChatDisconnected);
        ClientCompatibilityManager.INSTANCE.onDisconnect(this::onDisconnect);
    }

    private void resetOwnState() {
        this.disconnected = true;
        this.group = null;
    }

    public void onVoiceChatDisconnected() {
        this.disconnected = true;
        this.syncOwnState();
        PluginManager.instance().dispatchEvent(ClientVoicechatConnectionEvent.class, new ClientVoicechatConnectionEventImpl(false));
    }

    public void onVoiceChatConnected(ClientVoicechatConnection client) {
        this.disconnected = false;
        this.syncOwnState();
        PluginManager.instance().dispatchEvent(ClientVoicechatConnectionEvent.class, new ClientVoicechatConnectionEventImpl(true));
    }

    private void onDisconnect() {
        this.clearStates();
        this.resetOwnState();
    }

    public boolean isPlayerDisabled(PlayerEntity player) {
        PlayerState playerState = this.states.get(player.getUniqueID());
        if (playerState == null) {
            return false;
        }
        return playerState.isDisabled();
    }

    public boolean isPlayerDisconnected(PlayerEntity player) {
        PlayerState playerState = this.states.get(player.getUniqueID());
        if (playerState == null) {
            return VoicechatClient.CLIENT_CONFIG.showFakePlayersDisconnected.get();
        }
        return playerState.isDisconnected();
    }

    public void syncOwnState() {
        ClientServerNetManager.sendToServer(new UpdateStatePacket(this.isDisabled()));
        Voicechat.LOGGER.debug("Sent own state to server: disabled={}", this.isDisabled());
    }

    public boolean isDisabled() {
        if (!this.canEnable()) {
            return true;
        }
        return VoicechatClient.CLIENT_CONFIG.disabled.get();
    }

    public boolean canEnable() {
        if (OnboardingManager.isOnboarding()) {
            return false;
        }
        ClientVoicechat client = ClientManager.getClient();
        if (client == null) {
            return false;
        }
        return client.getSoundManager() != null;
    }

    public void setDisabled(boolean disabled) {
        VoicechatClient.CLIENT_CONFIG.disabled.set(disabled).save();
        this.syncOwnState();
        PluginManager.instance().dispatchEvent(VoicechatDisableEvent.class, new VoicechatDisableEventImpl(disabled));
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    public boolean isMuted() {
        return VoicechatClient.CLIENT_CONFIG.muted.get();
    }

    public void setMuted(boolean muted) {
        VoicechatClient.CLIENT_CONFIG.muted.set(muted).save();
        PluginManager.instance().dispatchEvent(MicrophoneMuteEvent.class, new MicrophoneMuteEventImpl(muted));
    }

    public void onFinishOnboarding() {
        this.syncOwnState();
    }

    public boolean isInGroup(PlayerEntity player) {
        PlayerState state = this.states.get(player.getUniqueID());
        if (state == null) {
            return false;
        }
        return state.hasGroup();
    }

    @Nullable
    public UUID getGroup(PlayerEntity player) {
        PlayerState state = this.states.get(player.getUniqueID());
        if (state == null) {
            return null;
        }
        return state.getGroup();
    }

    @Nullable
    public ClientGroup getGroup() {
        if (this.group == null) {
            return null;
        }
        return ClientManager.getGroupManager().getGroup(this.group);
    }

    @Nullable
    public UUID getGroupID() {
        return this.group;
    }

    public List<PlayerState> getPlayerStates(boolean includeSelf) {
        if (includeSelf) {
            return new ArrayList<PlayerState>(this.states.values());
        }
        return this.states.values().stream().filter(playerState -> !playerState.getUuid().equals(this.getOwnID())).collect(Collectors.toList());
    }

    public UUID getOwnID() {
        ClientVoicechatConnection connection;
        ClientVoicechat client = ClientManager.getClient();
        if (client != null && (connection = client.getConnection()) != null) {
            return connection.getData().getPlayerUUID();
        }
        return Minecraft.getInstance().player.getGameProfile().getId();
    }

    @Nullable
    public PlayerState getState(UUID player) {
        return this.states.get(player);
    }

    public void clearStates() {
        this.states.clear();
    }
}
