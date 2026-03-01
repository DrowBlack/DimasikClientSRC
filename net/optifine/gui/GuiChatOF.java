package net.optifine.gui;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public class GuiChatOF
extends ChatScreen {
    private static final String CMD_RELOAD_SHADERS = "/reloadShaders";
    private static final String CMD_RELOAD_CHUNKS = "/reloadChunks";

    public GuiChatOF(ChatScreen guiChat) {
        super(VideoSettingsScreen.getGuiChatText(guiChat));
    }

    @Override
    public void sendMessage(String msg) {
        if (this.checkCustomCommand(msg)) {
            this.minecraft.ingameGUI.getChatGUI().addToSentMessages(msg);
        } else {
            super.sendMessage(msg);
        }
    }

    private boolean checkCustomCommand(String msg) {
        if (msg == null) {
            return false;
        }
        if ((msg = msg.trim()).equals(CMD_RELOAD_SHADERS)) {
            if (Config.isShaders()) {
                Shaders.uninit();
                Shaders.loadShaderPack();
            }
            return true;
        }
        if (msg.equals(CMD_RELOAD_CHUNKS)) {
            this.minecraft.worldRenderer.loadRenderers();
            return true;
        }
        return false;
    }
}
