package dimasik.managers.mods.voicechat.gui.onboarding;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingScreenBase;
import dimasik.managers.mods.voicechat.gui.onboarding.PttOnboardingScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.VoiceActivationOnboardingScreen;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneActivationType;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ActivationOnboardingScreen
extends OnboardingScreenBase {
    private static final ITextComponent TITLE = new TranslationTextComponent("message.voicechat.onboarding.activation.title").mergeStyle(TextFormatting.BOLD);
    private static final ITextComponent DESCRIPTION = new TranslationTextComponent("message.voicechat.onboarding.activation").append(new StringTextComponent("\n\n")).append(new TranslationTextComponent("message.voicechat.onboarding.activation.ptt", new TranslationTextComponent("message.voicechat.onboarding.activation.ptt.name").mergeStyle(TextFormatting.BOLD, TextFormatting.UNDERLINE))).append(new StringTextComponent("\n\n")).append(new TranslationTextComponent("message.voicechat.onboarding.activation.voice", new TranslationTextComponent("message.voicechat.onboarding.activation.voice.name").mergeStyle(TextFormatting.BOLD, TextFormatting.UNDERLINE)));

    public ActivationOnboardingScreen(@Nullable Screen previous) {
        super(TITLE, previous);
    }

    @Override
    protected void init() {
        super.init();
        Button ptt = new Button(this.guiLeft, this.guiTop + this.contentHeight - 40 - 8, this.contentWidth / 2 - 4, 20, new TranslationTextComponent("message.voicechat.onboarding.activation.ptt.name"), button -> {
            VoicechatClient.CLIENT_CONFIG.microphoneActivationType.set(MicrophoneActivationType.PTT).save();
            this.minecraft.displayGuiScreen(new PttOnboardingScreen(this));
        });
        this.addButton(ptt);
        Button voice = new Button(this.guiLeft + this.contentWidth / 2 + 4, this.guiTop + this.contentHeight - 40 - 8, this.contentWidth / 2 - 4, 20, new TranslationTextComponent("message.voicechat.onboarding.activation.voice.name"), button -> {
            VoicechatClient.CLIENT_CONFIG.microphoneActivationType.set(MicrophoneActivationType.VOICE).save();
            this.minecraft.displayGuiScreen(new VoiceActivationOnboardingScreen(this));
        });
        this.addButton(voice);
        this.addBackOrCancelButton(true);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTitle(stack, TITLE);
        this.renderMultilineText(stack, DESCRIPTION);
    }
}
