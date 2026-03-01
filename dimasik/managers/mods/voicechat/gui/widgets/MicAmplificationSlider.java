package dimasik.managers.mods.voicechat.gui.widgets;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.widgets.DebouncedSlider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MicAmplificationSlider
extends DebouncedSlider {
    private static final float MAXIMUM = 4.0f;

    public MicAmplificationSlider(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn, new StringTextComponent(""), VoicechatClient.CLIENT_CONFIG.microphoneAmplification.get().floatValue() / 4.0f);
        this.updateMessage();
    }

    protected void updateMessage() {
        long amp = Math.round(this.sliderValue * 4.0 * 100.0 - 100.0);
        this.setMessage(new TranslationTextComponent("message.voicechat.microphone_amplification", ((float)amp > 0.0f ? "+" : "") + amp + "%"));
    }

    @Override
    public void applyDebounced() {
        VoicechatClient.CLIENT_CONFIG.microphoneAmplification.set(this.sliderValue * 4.0).save();
    }

    @Override
    protected void func_230979_b_() {
    }

    @Override
    protected void func_230972_a_() {
    }
}
