package net.minecraft.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Generated;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.ITextComponent;

public class Button
extends AbstractButton {
    public static final ITooltip field_238486_s_ = (button, matrixStack, mouseX, mouseY) -> {};
    protected final IPressable onPress;
    protected final ITooltip onTooltip;
    int height;

    public Button(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction) {
        this(x, y, width, height, title, pressedAction, field_238486_s_);
    }

    public Button(int x, int y, int width, int height1, ITextComponent title, IPressable pressedAction, ITooltip onTooltip) {
        super(x, y, width, height1, title);
        this.height = height1;
        this.onPress = pressedAction;
        this.onTooltip = onTooltip;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        if (this.isHovered()) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.onTooltip.onTooltip(this, matrixStack, mouseX, mouseY);
    }

    @Generated
    public int getHeight() {
        return this.height;
    }

    public static interface ITooltip {
        public void onTooltip(AbstractButton var1, MatrixStack var2, int var3, int var4);
    }

    public static interface IPressable {
        public void onPress(AbstractButton var1);
    }
}
