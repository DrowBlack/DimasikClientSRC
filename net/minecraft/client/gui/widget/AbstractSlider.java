package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class AbstractSlider
extends Widget {
    protected double sliderValue;

    public AbstractSlider(int x, int y, int width, int height, ITextComponent message, double defaultValue) {
        super(x, y, width, height, message);
        this.sliderValue = defaultValue;
    }

    @Override
    protected int getYImage(boolean isHovered) {
        return 0;
    }

    @Override
    protected IFormattableTextComponent getNarrationMessage() {
        return new TranslationTextComponent("gui.narrate.slider", this.getMessage());
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int i = (this.isHovered() ? 2 : 1) * 20;
        this.blit(matrixStack, this.x + (int)(this.sliderValue * (double)(this.width - 8)), this.y, 0, 46 + i, 4, 20);
        this.blit(matrixStack, this.x + (int)(this.sliderValue * (double)(this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.changeSliderValue(mouseX);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean flag;
        boolean bl = flag = keyCode == 263;
        if (flag || keyCode == 262) {
            float f = flag ? -1.0f : 1.0f;
            this.setSliderValue(this.sliderValue + (double)(f / (float)(this.width - 8)));
        }
        return false;
    }

    private void changeSliderValue(double mouseX) {
        this.setSliderValue((mouseX - (double)(this.x + 4)) / (double)(this.width - 8));
    }

    private void setSliderValue(double value) {
        double d0 = this.sliderValue;
        this.sliderValue = MathHelper.clamp(value, 0.0, 1.0);
        if (d0 != this.sliderValue) {
            this.func_230972_a_();
        }
        this.func_230979_b_();
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.changeSliderValue(mouseX);
        super.onDrag(mouseX, mouseY, dragX, dragY);
    }

    @Override
    public void playDownSound(SoundHandler handler) {
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.playDownSound(Minecraft.getInstance().getSoundHandler());
    }

    protected abstract void func_230979_b_();

    protected abstract void func_230972_a_();
}
