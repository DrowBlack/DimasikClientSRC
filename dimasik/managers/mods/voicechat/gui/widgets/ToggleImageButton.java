package dimasik.managers.mods.voicechat.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.widgets.ImageButton;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class ToggleImageButton
extends ImageButton {
    @Nullable
    protected Supplier<Boolean> stateSupplier;

    public ToggleImageButton(int x, int y, ResourceLocation texture, @Nullable Supplier<Boolean> stateSupplier, ImageButton.PressAction onPress, ImageButton.TooltipSupplier tooltipSupplier) {
        super(x, y, texture, onPress, tooltipSupplier);
        this.stateSupplier = stateSupplier;
    }

    @Override
    protected void renderImage(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.stateSupplier == null) {
            return;
        }
        this.mc.getTextureManager().bindTexture(this.texture);
        if (this.stateSupplier.get().booleanValue()) {
            ToggleImageButton.blit(matrices, this.x + 2, this.y + 2, 16.0f, 0.0f, 16, 16, 32, 32);
        } else {
            ToggleImageButton.blit(matrices, this.x + 2, this.y + 2, 0.0f, 0.0f, 16, 16, 32, 32);
        }
    }
}
