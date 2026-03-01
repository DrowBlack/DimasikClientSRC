package dimasik.ui.screen.element.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.option.main.ColorOption;
import dimasik.ui.screen.element.Element;
import dimasik.utils.client.StringUtils;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class ColorElement
extends Element<ColorOption> {
    private boolean open = false;
    private float hsb;
    private float saturation;
    private float bright;

    public ColorElement(ColorOption option) {
        super(20.0f, 50.0f, option);
        float[] rgb = ColorHelpers.rgb((Integer)option.getValue());
        float[] hsb = Color.RGBtoHSB((int)(rgb[0] * 255.0f), (int)(rgb[1] * 255.0f), (int)(rgb[2] * 255.0f), null);
        this.hsb = hsb[0];
        this.saturation = hsb[1];
        this.bright = hsb[2];
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.setHeight(100.0f);
        float x = this.getX() + 5.0f;
        float y = this.getY() + sf_regular.getHeight(12.0f) + 4.0f;
        float height = this.getHeight() - sf_regular.getHeight(12.0f) - 4.0f;
        float width = this.getWidth() - 10.0f;
        sf_regular.drawText(matrixStack, StringUtils.trim(((ColorOption)this.option).getVisualName(), this.getWidth(), sf_regular, 12.0f), x, this.getY(), ColorHelpers.rgba(255, 255, 255, 255), 12.0f);
        VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 5.0f, -1, Color.HSBtoRGB(this.hsb, 1.0f, 1.0f), Color.BLACK.getRGB(), Color.BLACK.getRGB());
        for (float i = 0.0f; i < width - 5.0f; i += 1.0f) {
            float hue = i / (width - 5.0f);
            VisualHelpers.drawRoundedRect(matrixStack, x + i, y + height + 10.0f, 7.0f, 7.0f, 3.0f, Color.HSBtoRGB(hue, 1.0f, 1.0f));
        }
        if (GLFW.glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), 0) == 1 && ScreenHelpers.isHovered(mouseX, mouseY, x, y + height + 5.0f, width, 15.0f)) {
            this.hsb = ((float)mouseX - x) / width;
        }
        if (ScreenHelpers.isHovered(mouseX, mouseY, x, y, width, height) && GLFW.glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), 0) == 1) {
            this.bright = MathHelper.clamp(1.0f - ((float)mouseY - y) / height, 0.0f, 1.0f);
            this.saturation = MathHelper.clamp(((float)mouseX - x) / width, 0.0f, 1.0f);
        }
        VisualHelpers.drawRoundedRect(matrixStack, x + this.saturation * (width - 5.0f) - 2.0f, y + (1.0f - this.bright) * (height - 5.0f) - 2.0f, 7.0f, 7.0f, 3.5f, -1);
        VisualHelpers.drawRoundedRect(matrixStack, x + this.hsb * width - 5.0f, y + height + 8.0f, 10.0f, 10.0f, 5.0f, -1);
        int col = Color.HSBtoRGB(this.hsb, this.saturation, this.bright);
        ((ColorOption)this.option).setValue(col);
        this.setHeight(120.0f);
    }

    @Override
    public void exit() {
    }

    @Override
    public void tick() {
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        float x = this.getX() + 5.0f;
        float y = this.getY() + sf_regular.getHeight(12.0f) + 4.0f;
        float height = this.getHeight() - sf_regular.getHeight(12.0f) - 4.0f;
        float width = this.getWidth() - 10.0f;
        if (ScreenHelpers.isHovered(mouseX, mouseY, x, y, width, height)) {
            this.open = !this.open;
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    @Override
    public void translate() {
        ((ColorOption)this.option).setVisualName(this.getTranslation(((ColorOption)this.option).getSettingName()));
    }
}
