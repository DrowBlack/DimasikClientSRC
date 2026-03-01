package dimasik.ui.option.component.theme;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.ui.option.component.theme.ThemeComponent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class Theme
implements IFastAccess {
    private List<ThemeComponent> themeComponents = new ArrayList<ThemeComponent>();
    public int edit;
    private Animation animation = new Animation();
    float x;
    float y;
    float width;
    float height;
    boolean open;
    float hsb;
    float satur;
    float brithe;

    public Theme() {
        dimasik.managers.theme.api.Theme custom = Load.getInstance().getHooks().getThemeManagers().themes.get(Load.getInstance().getHooks().getThemeManagers().themes.size() - 1);
        for (dimasik.managers.theme.api.Theme theme : Load.getInstance().getHooks().getThemeManagers().themes) {
            if (theme.name.equalsIgnoreCase("Custom")) continue;
            this.themeComponents.add(new ThemeComponent(theme));
        }
        float[] rgb = ColorHelpers.rgb(custom.colors[this.edit]);
        float[] hsb = Color.RGBtoHSB((int)(rgb[0] * 255.0f), (int)(rgb[1] * 255.0f), (int)(rgb[2] * 255.0f), null);
        this.hsb = hsb[0];
        this.satur = hsb[1];
        this.brithe = hsb[2];
    }

    public void tick() {
        this.animation.update(this.open);
    }

    public void render(@NonNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, float x, float y, float width, float height) {
        int col;
        if (matrixStack == null) {
            throw new NullPointerException("matrixStack is marked non-null but is null");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        float offsetX = 10.0f;
        float offsetY = 6.0f;
        dimasik.managers.theme.api.Theme custom = Load.getInstance().getHooks().getThemeManagers().themes.get(Load.getInstance().getHooks().getThemeManagers().themes.size() - 1);
        for (ThemeComponent component : this.themeComponents) {
            component.x = x + offsetX;
            component.y = y + offsetY;
            component.width = width - offsetX * 2.0f;
            component.height = 50.0f;
            offsetY += 56.0f;
            component.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        int color1 = custom.colors[0];
        int color2 = custom.colors[1];
        int color3 = custom.colors[1];
        int color4 = custom.colors[0];
        VisualHelpers.drawRoundedRect(matrixStack, x + offsetX, y + height - 58.0f, width - offsetX * 2.0f, 50.0f, 10.0f, color1, color2, color3, color4);
        this.animation.animate(0.0f, 1.0f, 0.125f, EasingList.BACK_OUT, Theme.mc.getTimer().renderPartialTicks);
        GLHelpers.INSTANCE.scaleAnimation(matrixStack, x + width - 150.0f, y + height - 300.0f, 250.0f, 350.0f, this.animation.getAnimationValue());
        VisualHelpers.drawRoundedRect(matrixStack, x + width - 150.0f, y + height - 300.0f, 250.0f, 350.0f, 20.0f, ColorHelpers.rgba(15, 15, 15, 255));
        VisualHelpers.drawRoundedRect(matrixStack, x + width - 140.0f, y + height - 260.0f, 230.0f, 280.0f, 10.0f, -1, Color.HSBtoRGB(this.hsb, 1.0f, 1.0f), Color.BLACK.getRGB(), Color.BLACK.getRGB());
        for (float i = 0.0f; i < 225.0f; i += 1.0f) {
            float hue = i / 225.0f;
            VisualHelpers.drawRoundedRect(matrixStack, x + width - 140.0f + i, y + height + 30.0f, 7.0f, 7.0f, 4.0f, Color.HSBtoRGB(hue, 1.0f, 1.0f));
        }
        if (GLFW.glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), 0) == 1 && this.open && ScreenHelpers.isHovered(mouseX, mouseY, x + width - 140.0f, y + height + 30.0f, 225.0f, 15.0f)) {
            this.hsb = ((float)mouseX - (x + width - 140.0f)) / 225.0f;
        }
        if (ScreenHelpers.isHovered(mouseX, mouseY, x + width - 145.0f, y + height - 265.0f, 230.0f, 280.0f) && GLFW.glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), 0) == 1 && this.open) {
            this.brithe = MathHelper.clamp(1.0f - ((float)mouseY - (y + height - 260.0f)) / 280.0f, 0.0f, 1.0f);
            this.satur = MathHelper.clamp(((float)mouseX - (x + width - 140.0f)) / 230.0f, 0.0f, 1.0f);
        }
        VisualHelpers.drawRoundedRect(matrixStack, x + width - 137.0f, y + height - 290.0f, 110.0f, 25.0f, 5.0f, color1);
        VisualHelpers.drawRoundedRect(matrixStack, x + width - 24.0f, y + height - 290.0f, 110.0f, 25.0f, 5.0f, color2);
        VisualHelpers.drawRoundedRect(matrixStack, x + width - 140.0f + this.satur * 230.0f, y + height - 260.0f + (1.0f - this.brithe) * 280.0f, 7.0f, 7.0f, 3.5f, -1);
        VisualHelpers.drawRoundedRect(matrixStack, x + width - 147.0f + this.hsb * 230.0f, y + height + 28.0f, 10.0f, 10.0f, 5.0f, -1);
        custom.colors[this.edit] = col = Color.HSBtoRGB(this.hsb, this.satur, this.brithe);
    }

    public void mouseClicked(double mouseX, double mouseY, int mouse) {
        float[] hsb;
        float[] rgb;
        dimasik.managers.theme.api.Theme custom = Load.getInstance().getHooks().getThemeManagers().themes.get(Load.getInstance().getHooks().getThemeManagers().themes.size() - 1);
        float offsetX = 10.0f;
        if (!ScreenHelpers.isHovered(mouseX, mouseY, this.x + this.width - 150.0f, this.y + this.height - 300.0f, 250.0f, 350.0f) || !this.open) {
            if (ScreenHelpers.isHovered(mouseX, mouseY, this.x + offsetX, this.y + this.height - 58.0f, this.width - offsetX * 2.0f, 50.0f)) {
                if (mouse == 1) {
                    boolean bl = this.open = !this.open;
                }
                if (mouse == 0) {
                    Load.getInstance().getHooks().getThemeManagers().setCurrentTheme(custom);
                }
            }
            for (ThemeComponent component : this.themeComponents) {
                if (!ScreenHelpers.isHovered(mouseX, mouseY, component.x, component.y, component.width, component.height) || ScreenHelpers.isHovered(mouseX, mouseY, this.x + offsetX, this.y + this.height - 58.0f, this.width - offsetX * 2.0f, 50.0f) || mouse != 0) continue;
                Load.getInstance().getHooks().getThemeManagers().setCurrentTheme(component.theme);
            }
        }
        if (ScreenHelpers.isHovered(mouseX, mouseY, this.x + this.width - 137.0f, this.y + this.height - 290.0f, 110.0f, 25.0f)) {
            this.edit = 0;
            rgb = ColorHelpers.rgb(custom.colors[this.edit]);
            hsb = Color.RGBtoHSB((int)(rgb[0] * 255.0f), (int)(rgb[1] * 255.0f), (int)(rgb[2] * 255.0f), null);
            this.hsb = hsb[0];
            this.satur = hsb[1];
            this.brithe = hsb[2];
        }
        if (ScreenHelpers.isHovered(mouseX, mouseY, this.x + this.width - 24.0f, this.y + this.height - 290.0f, 110.0f, 25.0f)) {
            this.edit = 1;
            rgb = ColorHelpers.rgb(custom.colors[this.edit]);
            hsb = Color.RGBtoHSB((int)(rgb[0] * 255.0f), (int)(rgb[1] * 255.0f), (int)(rgb[2] * 255.0f), null);
            this.hsb = hsb[0];
            this.satur = hsb[1];
            this.brithe = hsb[2];
        }
    }
}
