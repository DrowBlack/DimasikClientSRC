package dimasik.managers.mods.voicechat.gui.onboarding;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.onboarding.FinalOnboardingScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingScreenBase;
import dimasik.managers.mods.voicechat.gui.widgets.KeybindButton;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class PttOnboardingScreen
extends OnboardingScreenBase {
    private static final ITextComponent TITLE = new TranslationTextComponent("message.voicechat.onboarding.ptt.title").mergeStyle(TextFormatting.BOLD);
    private static final ITextComponent DESCRIPTION = new TranslationTextComponent("message.voicechat.onboarding.ptt.description");
    private static final ITextComponent BUTTON_DESCRIPTION = new TranslationTextComponent("message.voicechat.onboarding.ptt.button_description");
    protected KeybindButton keybindButton;
    protected int keybindButtonPos;

    public PttOnboardingScreen(@Nullable Screen previous) {
        super(TITLE, previous);
    }

    @Override
    protected void init() {
        super.init();
        this.keybindButtonPos = this.guiTop + this.contentHeight - 60 - 16 - 40;
        this.keybindButton = new KeybindButton(this.minecraft.gameSettings.KEY_PTT, this.guiLeft + 40, this.keybindButtonPos, this.contentWidth - 80, 20);
        this.addButton(this.keybindButton);
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
        this.font.drawStringWithShadow(stack, BUTTON_DESCRIPTION.getString(), (float)(this.width / 2 - this.font.getStringWidth(BUTTON_DESCRIPTION.getString()) / 2), (float)(this.keybindButtonPos - this.font.FONT_HEIGHT - 8), -1);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.keybindButton.isListening()) {
            return false;
        }
        return super.shouldCloseOnEsc();
    }
}
