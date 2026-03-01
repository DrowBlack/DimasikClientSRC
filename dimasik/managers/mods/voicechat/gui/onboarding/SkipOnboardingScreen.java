package dimasik.managers.mods.voicechat.gui.onboarding;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingManager;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingScreenBase;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class SkipOnboardingScreen
extends OnboardingScreenBase {
    private static final ITextComponent TITLE = new TranslationTextComponent("message.voicechat.onboarding.skip.title").mergeStyle(TextFormatting.BOLD);
    private static final ITextComponent DESCRIPTION = new TranslationTextComponent("message.voicechat.onboarding.skip.description");
    private static final ITextComponent CONFIRM = new TranslationTextComponent("message.voicechat.onboarding.confirm");

    public SkipOnboardingScreen(@Nullable Screen previous) {
        super(TITLE, previous);
    }

    @Override
    protected void init() {
        super.init();
        this.addBackOrCancelButton();
        this.addPositiveButton(CONFIRM, button -> OnboardingManager.finishOnboarding());
    }

    @Override
    public Screen getNextScreen() {
        return this.previous;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTitle(stack, TITLE);
        this.renderMultilineText(stack, DESCRIPTION);
    }
}
