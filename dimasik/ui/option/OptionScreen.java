package dimasik.ui.option;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.ui.option.component.theme.Theme;
import dimasik.ui.option.list.Type;
import lombok.Generated;
import lombok.NonNull;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.StringTextComponent;

public class OptionScreen
extends Screen
implements IFastAccess {
    private Type currentType;
    private final Animation animation = new Animation();
    private final Animation backgroundAnimation = new Animation();
    private final Theme theme = new Theme();
    private boolean update = true;

    public OptionScreen() {
        super(new StringTextComponent("Powered by Ragerik"));
    }

    @Override
    public void tick() {
        this.animation.update(this.update);
        this.backgroundAnimation.update(this.update);
        this.theme.tick();
    }

    @Override
    public void render(@NonNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (matrixStack == null) {
            throw new NullPointerException("matrixStack is marked non-null but is null");
        }
        Load.getInstance().getUiScreen().getAnimation().setPrevValue(0.0f);
        Load.getInstance().getUiScreen().getAnimation().setValue(0.0f);
        Load.getInstance().getUiScreen().getAnimation().setAnimationValue(0.0f);
        Load.getInstance().getUiScreen().getBackgroundAnimation().setAnimationValue(0.0f);
        Load.getInstance().getUiScreen().getBackgroundAnimation().setPrevValue(0.0f);
        Load.getInstance().getUiScreen().getBackgroundAnimation().setValue(0.0f);
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        this.backgroundAnimation.animate(0.0f, 1.0f, 0.125f, EasingList.NONE, OptionScreen.mc.getTimer().renderPartialTicks);
        if (Load.getInstance().getHooks().getModuleManagers().getClickGui().better.getSelected("Darkening background")) {
            VisualHelpers.drawRoundedRect(matrixStack, -1000.0f, -1000.0f, (float)(mc.getMainWindow().getWidth() + 2000), (float)(mc.getMainWindow().getHeight() + 2000), 0.0f, ColorHelpers.rgba(0, 0, 0, (int)(120.0f * this.backgroundAnimation.getAnimationValue())));
        }
        if (Load.getInstance().getHooks().getModuleManagers().getClickGui().better.getSelected("Colorful background")) {
            VisualHelpers.drawRoundedGradientRect(matrixStack, -10.0f, -10.0f, (float)(mc.getMainWindow().getWidth() + 20), (float)(mc.getMainWindow().getHeight() - 100), 0.0f, ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.getColorWithAlpha(ColorHelpers.getTheme(45), 255.0f * this.backgroundAnimation.getAnimationValue()), ColorHelpers.getColorWithAlpha(ColorHelpers.getTheme(90), 255.0f * this.backgroundAnimation.getAnimationValue()));
        }
        GLHelpers.INSTANCE.rescale(1.0);
        this.animation.animate(0.0f, 1.0f, 0.125f, EasingList.BACK_OUT, OptionScreen.mc.getTimer().renderPartialTicks);
        float width = (float)mc.getMainWindow().getWidth() / 1.5f;
        float height = (float)mc.getMainWindow().getHeight() / 1.5f;
        float x = (float)mc.getMainWindow().getWidth() / 2.0f - width / 2.0f;
        float y = (float)mc.getMainWindow().getHeight() / 2.0f - height / 2.0f;
        GLHelpers.INSTANCE.scaleAnimation(matrixStack, 0.0f, 0.0f, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), this.animation.getAnimationValue());
        VisualHelpers.drawRoundedRect(matrixStack, x + 148.0f, y + 58.0f, width - 148.0f, height - 58.0f, new Vector4f(20.0f, 0.0f, 0.0f, 0.0f), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(5, 5, 5, 255), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(5, 5, 5, 255));
        VisualHelpers.drawRoundedRect(matrixStack, x + 148.0f, y, width - 148.0f, 60.0f, new Vector4f(0.0f, 20.0f, 0.0f, 0.0f), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(25, 25, 25, 255), ColorHelpers.rgba(15, 15, 15, 255));
        VisualHelpers.drawRoundedRect(matrixStack, x, y, 150.0f, height, new Vector4f(0.0f, 0.0f, 20.0f, 20.0f), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(25, 25, 25, 255));
        int i = 0;
        for (Type type : Type.values()) {
            sf_semibold.drawCenteredText(matrixStack, type.getName(), x + 50.0f, y + (float)i + 90.0f, ColorHelpers.rgba(255, 255, 255, 255), 18.0f);
            i += (int)(sf_semibold.getHeight(18.0f) + 20.0f);
        }
        if (this.currentType == Type.THEME) {
            this.theme.render(matrixStack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks, x + 150.0f, y + 60.0f, width - 150.0f, height - 58.0f);
        }
        GLHelpers.INSTANCE.rescaleMC();
        if (this.animation.getPrevValue() == 0.0f && this.animation.getValue() == 0.0f && !this.update) {
            this.update = true;
            super.closeScreen();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse) {
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        float width = (float)mc.getMainWindow().getWidth() / 1.5f;
        float height = (float)mc.getMainWindow().getHeight() / 1.5f;
        float x = (float)mc.getMainWindow().getWidth() / 2.0f - width / 2.0f;
        float y = (float)mc.getMainWindow().getHeight() / 2.0f - height / 2.0f;
        float offset = 0.0f;
        for (Type type : Type.values()) {
            if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, x, y + offset + 90.0f, 150.0f, sf_semibold.getHeight(18.0f))) {
                this.currentType = type;
            }
            offset += sf_semibold.getHeight(18.0f) + 20.0f;
        }
        if (this.currentType == Type.THEME) {
            this.theme.mouseClicked(fixedMouse.x, fixedMouse.y, mouse);
        }
        return super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void closeScreen() {
        this.update = false;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Animation getBackgroundAnimation() {
        return this.backgroundAnimation;
    }
}
