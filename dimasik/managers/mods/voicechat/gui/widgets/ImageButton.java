package dimasik.managers.mods.voicechat.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class ImageButton
extends AbstractButton {
    protected Minecraft mc = Minecraft.getInstance();
    protected ResourceLocation texture;
    @Nullable
    protected PressAction onPress;
    protected TooltipSupplier tooltipSupplier;

    public ImageButton(int x, int y, ResourceLocation texture, @Nullable PressAction onPress, TooltipSupplier tooltipSupplier) {
        super(x, y, 20, 20, new StringTextComponent(""));
        this.texture = texture;
        this.onPress = onPress;
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    public void onPress() {
        if (this.onPress != null) {
            this.onPress.onPress(this);
        }
    }

    protected void renderImage(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.mc.getTextureManager().bindTexture(this.texture);
        ImageButton.blit(matrices, this.x + 2, this.y + 2, 0.0f, 0.0f, 16, 16, 16, 16);
    }

    protected boolean shouldRenderTooltip() {
        return this.isHovered;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        this.renderImage(matrices, mouseX, mouseY, delta);
        if (this.shouldRenderTooltip()) {
            this.renderToolTip(matrices, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
    }

    public static interface PressAction {
        public void onPress(ImageButton var1);
    }

    public static interface TooltipSupplier {
        public void onTooltip(ImageButton var1, MatrixStack var2, int var3, int var4);
    }
}
