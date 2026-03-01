package dimasik.managers.mods.voicechat.gui.widgets;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.widgets.EnumButton;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneActivationType;
import java.util.function.Consumer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MicActivationButton
extends EnumButton<MicrophoneActivationType> {
    protected Consumer<MicrophoneActivationType> onChange;

    public MicActivationButton(int xIn, int yIn, int widthIn, int heightIn, Consumer<MicrophoneActivationType> onChange) {
        super(xIn, yIn, widthIn, heightIn, VoicechatClient.CLIENT_CONFIG.microphoneActivationType);
        this.onChange = onChange;
        this.updateText();
        onChange.accept((MicrophoneActivationType)((Object)this.entry.get()));
    }

    @Override
    protected ITextComponent getText(MicrophoneActivationType type) {
        return new TranslationTextComponent("message.voicechat.activation_type", type.getText());
    }

    @Override
    protected void onUpdate(MicrophoneActivationType type) {
        this.onChange.accept(type);
    }
}
