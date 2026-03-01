package dimasik.managers.mods.voicechat.gui.onboarding;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class OnboardingScreenBase
extends Screen {
    public static final ITextComponent NEXT = new TranslationTextComponent("message.voicechat.onboarding.next");
    public static final ITextComponent BACK = new TranslationTextComponent("message.voicechat.onboarding.back");
    public static final ITextComponent CANCEL = new TranslationTextComponent("message.voicechat.onboarding.cancel");
    protected static final int TEXT_COLOR = -1;
    protected static final int PADDING = 8;
    protected static final int SMALL_PADDING = 2;
    protected static final int BUTTON_HEIGHT = 20;
    protected int contentWidth;
    protected int guiLeft;
    protected int guiTop;
    protected int contentHeight;
    @Nullable
    protected Screen previous;

    public OnboardingScreenBase(ITextComponent title, @Nullable Screen previous) {
        super(title);
        this.previous = previous;
    }

    @Override
    protected void init() {
        super.init();
        this.contentWidth = this.width / 2;
        this.guiLeft = (this.width - this.contentWidth) / 2;
        this.guiTop = 20;
        this.contentHeight = this.height - this.guiTop * 2;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Nullable
    public Screen getNextScreen() {
        return null;
    }

    protected void addPositiveButton(ITextComponent text, Button.IPressable onPress) {
        Button nextButton = new Button(this.guiLeft + this.contentWidth / 2 + 4, this.guiTop + this.contentHeight - 20, this.contentWidth / 2 - 4, 20, text, onPress);
        this.addButton(nextButton);
    }

    protected void addNextButton() {
        this.addPositiveButton(NEXT, button -> this.minecraft.displayGuiScreen(this.getNextScreen()));
    }

    protected void addBackOrCancelButton(boolean big) {
        ITextComponent text = CANCEL;
        if (this.previous instanceof OnboardingScreenBase) {
            text = BACK;
        }
        Button cancel = new Button(this.guiLeft, this.guiTop + this.contentHeight - 20, big ? this.contentWidth : this.contentWidth / 2 - 4, 20, text, button -> this.minecraft.displayGuiScreen(this.previous));
        this.addButton(cancel);
    }

    protected void addBackOrCancelButton() {
        this.addBackOrCancelButton(false);
    }

    protected void renderTitle(MatrixStack stack, ITextComponent titleComponent) {
        int titleWidth = this.font.getStringWidth(titleComponent.getString());
        this.font.drawStringWithShadow(stack, titleComponent.getString(), (float)(this.width / 2 - titleWidth / 2), (float)this.guiTop, -1);
    }

    protected void renderMultilineText(MatrixStack stack, ITextComponent textComponent) {
        List<IReorderingProcessor> text = this.font.trimStringToWidth(textComponent, this.contentWidth);
        for (int i = 0; i < text.size(); ++i) {
            IReorderingProcessor line = text.get(i);
            this.font.drawStringWithShadow(stack, line.toString(), (float)(this.width / 2 - this.font.getStringWidth(line.toString()) / 2), (float)(this.guiTop + this.font.FONT_HEIGHT + 20 + i * (this.font.FONT_HEIGHT + 1)), -1);
        }
    }
}
