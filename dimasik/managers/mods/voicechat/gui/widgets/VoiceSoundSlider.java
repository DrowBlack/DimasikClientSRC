package dimasik.managers.mods.voicechat.gui.widgets;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.widgets.DebouncedSlider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class VoiceSoundSlider
extends DebouncedSlider {
    public VoiceSoundSlider(int x, int y, int width, int height) {
        super(x, y, width, height, new StringTextComponent(""), VoicechatClient.CLIENT_CONFIG.voiceChatVolume.get().floatValue() / 2.0f);
        this.updateMessage();
    }

    protected void updateMessage() {
        this.setMessage(this.getMsg());
    }

    public ITextComponent getMsg() {
        return new TranslationTextComponent("message.voicechat.voice_chat_volume", Math.round(this.sliderValue * 200.0) + "%");
    }

    @Override
    public void applyDebounced() {
        VoicechatClient.CLIENT_CONFIG.voiceChatVolume.set(this.sliderValue * 2.0).save();
    }

    @Override
    protected void func_230979_b_() {
    }

    @Override
    protected void func_230972_a_() {
    }
}
