package dimasik.ui.screen.element.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.managers.module.option.main.BindOption;
import dimasik.ui.screen.element.Element;
import dimasik.utils.client.KeyUtils;

public class BindElement
extends Element<BindOption> {
    private boolean bind;
    private final Animation fade = new Animation();

    public BindElement(BindOption option) {
        super(20.0f, 20.0f, option);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float animate = this.getModule() != null ? this.getModule().getToggleFade().getAnimationValue() : 1.0f;
        float show = this.getModule() != null ? this.getShow().getAnimationValue() : 1.0f;
        this.fade.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
        int textColor = ColorHelpers.rgba(255, 255, 255, (122.39999999999999 + 132.6 * (double)animate) * (double)show);
        int bindColor = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, (91.8 + 30.599999999999998 * (double)this.fade.getAnimationValue()) * (double)show), ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 183.6 * (double)show), ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 255.0f * show), this.fade.getAnimationValue() * show), animate * show);
        suisse_intl.drawText(matrixStack, ((BindOption)this.option).getVisualName(), this.x + 10.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, textColor, 12.0f);
        suisse_intl.drawText(matrixStack, KeyUtils.getKey(((BindOption)this.option).getKey()), this.x + this.width - suisse_intl.getWidth(KeyUtils.getKey(((BindOption)this.option).getKey()), 12.0f) - 10.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, bindColor, 12.0f);
        this.setHeight(16.0f);
    }

    @Override
    public void exit() {
    }

    @Override
    public void tick() {
        this.fade.update(this.bind);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.bind) {
            if (keyCode == 261) {
                ((BindOption)this.option).setKey(-1);
                this.bind = false;
                return;
            }
            ((BindOption)this.option).setKey(keyCode);
            this.bind = false;
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (this.bind) {
            ((BindOption)this.option).setKey(button);
            this.bind = false;
        }
        if (ScreenHelpers.isHovered(mouseX, mouseY, this.x + this.width - suisse_intl.getWidth(KeyUtils.getKey(((BindOption)this.option).getKey()), 12.0f) - 10.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, suisse_intl.getWidth(KeyUtils.getKey((Integer)((BindOption)this.option).getValue()), 12.0f), suisse_intl.getHeight(12.0f)) && button == 0) {
            this.bind = !this.bind;
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
        ((BindOption)this.option).setVisualName(this.getTranslation(((BindOption)this.option).getSettingName()));
    }
}
