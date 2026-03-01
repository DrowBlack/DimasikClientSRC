package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class ErrorScreen
extends Screen {
    private final ITextComponent message;

    public ErrorScreen(ITextComponent p_i232277_1_, ITextComponent p_i232277_2_) {
        super(p_i232277_1_);
        this.message = p_i232277_2_;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, 140, 200, 20, DialogTexts.GUI_CANCEL, p_213034_1_ -> this.minecraft.displayGuiScreen(null)));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, -12574688, -11530224);
        ErrorScreen.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 90, 0xFFFFFF);
        ErrorScreen.drawCenteredString(matrixStack, this.font, this.message, this.width / 2, 110, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
