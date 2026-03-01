package dimasik.managers.mods.voicechat.gui.widgets;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.widgets.BooleanConfigButton;
import dimasik.managers.mods.voicechat.voice.client.Denoiser;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DenoiserButton
extends BooleanConfigButton {
    private static final ITextComponent ENABLED = new TranslationTextComponent("message.voicechat.denoiser.on");
    private static final ITextComponent DISABLED = new TranslationTextComponent("message.voicechat.denoiser.off");

    public DenoiserButton(int x, int y, int width, int height) {
        super(x, y, width, height, VoicechatClient.CLIENT_CONFIG.denoiser, enabled -> new TranslationTextComponent("message.voicechat.denoiser", enabled != false ? ENABLED : DISABLED));
        if (Denoiser.createDenoiser() == null) {
            this.active = false;
        }
    }
}
