package dimasik.managers.mods.voicechat.gui.onboarding;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.onboarding.MicOnboardingScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingScreenBase;
import dimasik.managers.mods.voicechat.gui.onboarding.SkipOnboardingScreen;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class IntroductionOnboardingScreen
extends OnboardingScreenBase {
    private static final ITextComponent TITLE = new TranslationTextComponent("message.voicechat.onboarding.introduction.title", CommonCompatibilityManager.INSTANCE.getModName()).mergeStyle(TextFormatting.BOLD);
    private static final ITextComponent DESCRIPTION = new TranslationTextComponent("message.voicechat.onboarding.introduction.description");
    private static final ITextComponent SKIP = new TranslationTextComponent("message.voicechat.onboarding.introduction.skip");

    public IntroductionOnboardingScreen(@Nullable Screen previous) {
        super(TITLE, previous);
    }

    @Override
    protected void init() {
        super.init();
        Button skipButton = new Button(this.guiLeft, this.guiTop + this.contentHeight - 40 - 8, this.contentWidth, 20, SKIP, button -> this.minecraft.displayGuiScreen(new SkipOnboardingScreen(this)));
        this.addButton(skipButton);
        this.addBackOrCancelButton();
        this.addNextButton();
    }

    @Override
    public Screen getNextScreen() {
        return new MicOnboardingScreen(this);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTitle(stack, TITLE);
        this.renderMultilineText(stack, DESCRIPTION);
    }
}
