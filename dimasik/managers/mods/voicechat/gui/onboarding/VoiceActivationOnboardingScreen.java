package dimasik.managers.mods.voicechat.gui.onboarding;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.onboarding.FinalOnboardingScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingScreenBase;
import dimasik.managers.mods.voicechat.gui.widgets.DenoiserButton;
import dimasik.managers.mods.voicechat.gui.widgets.MicAmplificationSlider;
import dimasik.managers.mods.voicechat.gui.widgets.MicTestButton;
import dimasik.managers.mods.voicechat.gui.widgets.VoiceActivationSlider;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class VoiceActivationOnboardingScreen
extends OnboardingScreenBase {
    private static final ITextComponent TITLE = new TranslationTextComponent("message.voicechat.onboarding.voice.title").mergeStyle(TextFormatting.BOLD);
    private static final ITextComponent DESCRIPTION = new TranslationTextComponent("message.voicechat.onboarding.voice.description");
    protected VoiceActivationSlider slider;
    protected MicTestButton micTestButton;

    public VoiceActivationOnboardingScreen(@Nullable Screen previous) {
        super(TITLE, previous);
    }

    @Override
    protected void init() {
        super.init();
        int bottom = this.guiTop + this.contentHeight - 24 - 40;
        int space = 22;
        this.addButton(new MicAmplificationSlider(this.guiLeft, bottom - space * 2, this.contentWidth, 20));
        this.addButton(new DenoiserButton(this.guiLeft, bottom - space, this.contentWidth, 20));
        this.slider = new VoiceActivationSlider(this.guiLeft + 20 + 2, bottom, this.contentWidth - 20 - 2, 20);
        this.micTestButton = new MicTestButton(this.guiLeft, bottom, this.slider);
        this.addButton(this.micTestButton);
        this.addButton(this.slider);
        this.addBackOrCancelButton();
        this.addNextButton();
    }

    @Override
    public Screen getNextScreen() {
        return new FinalOnboardingScreen(this);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTitle(stack, TITLE);
        this.renderMultilineText(stack, DESCRIPTION);
        ITextComponent sliderTooltip = this.slider.getHoverText();
        if (this.slider.isHovered() && sliderTooltip != null) {
            this.renderTooltip(stack, sliderTooltip, mouseX, mouseY);
        } else if (this.micTestButton.isHovered()) {
            this.micTestButton.onTooltip(this.micTestButton, stack, mouseX, mouseY);
        }
    }
}
