package dimasik.ui.option.component.theme;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.theme.api.Theme;
import lombok.NonNull;

public class ThemeComponent {
    public float x;
    public float y;
    public float width;
    public float height;
    public Theme theme;

    public ThemeComponent(Theme theme) {
        this.theme = theme;
    }

    public void render(@NonNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (matrixStack == null) {
            throw new NullPointerException("matrixStack is marked non-null but is null");
        }
        int color1 = this.theme.colors[0];
        int color2 = this.theme.colors[1];
        int color3 = this.theme.colors[1];
        int color4 = this.theme.colors[0];
        VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 10.0f, color1, color2, color3, color4);
    }
}
