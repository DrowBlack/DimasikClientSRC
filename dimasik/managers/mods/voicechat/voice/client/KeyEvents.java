package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.VoiceChatScreen;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public class KeyEvents {
    private final Minecraft minecraft = Minecraft.getInstance();

    public KeyEvents() {
        ClientCompatibilityManager.INSTANCE.onHandleKeyBinds(this::handleKeybinds);
    }

    public void handleKeybinds() {
        ClientPlayerEntity player = this.minecraft.player;
        if (player == null) {
            return;
        }
        ClientVoicechat client = ClientManager.getClient();
        ClientPlayerStateManager playerStateManager = ClientManager.getPlayerStateManager();
        if (this.minecraft.gameSettings.KEY_VOICE_CHAT.isPressed()) {
            if (Screen.hasAltDown()) {
                if (Screen.hasControlDown()) {
                    VoicechatClient.CLIENT_CONFIG.onboardingFinished.set(false).save();
                    player.sendStatusMessage(new TranslationTextComponent("message.voicechat.onboarding.reset"), true);
                } else {
                    ClientManager.getDebugOverlay().toggle();
                }
            } else {
                this.minecraft.displayGuiScreen(new VoiceChatScreen());
            }
        }
        if (this.minecraft.gameSettings.KEY_GROUP.isPressed()) {
            player.sendStatusMessage(new TranslationTextComponent("message.voicechat.groups_disabled"), true);
        }
        if (this.minecraft.gameSettings.KEY_PTT.isPressed()) {
            this.checkConnected();
        }
        if (this.minecraft.gameSettings.KEY_WHISPER.isPressed()) {
            this.checkConnected();
        }
        if (this.minecraft.gameSettings.KEY_MUTE.isPressed()) {
            playerStateManager.setMuted(!playerStateManager.isMuted());
        }
        if (this.minecraft.gameSettings.KEY_DISABLE.isPressed()) {
            playerStateManager.setDisabled(!playerStateManager.isDisabled());
        }
        if (this.minecraft.gameSettings.KEY_HIDE_ICONS.isPressed()) {
            boolean hidden = VoicechatClient.CLIENT_CONFIG.hideIcons.get() == false;
            VoicechatClient.CLIENT_CONFIG.hideIcons.set(hidden).save();
            if (hidden) {
                player.sendStatusMessage(new TranslationTextComponent("message.voicechat.icons_hidden"), true);
            } else {
                player.sendStatusMessage(new TranslationTextComponent("message.voicechat.icons_visible"), true);
            }
        }
    }

    private boolean checkConnected() {
        if (ClientManager.getClient() == null || ClientManager.getClient().getConnection() == null || !ClientManager.getClient().getConnection().isInitialized()) {
            this.sendNotConnectedMessage();
            return false;
        }
        return true;
    }

    private void sendNotConnectedMessage() {
        ClientPlayerEntity player = this.minecraft.player;
        if (player == null) {
            Voicechat.LOGGER.warn("Voice chat not connected", new Object[0]);
            return;
        }
        player.sendStatusMessage(new TranslationTextComponent("message.voicechat.voice_chat_not_connected"), true);
    }
}
