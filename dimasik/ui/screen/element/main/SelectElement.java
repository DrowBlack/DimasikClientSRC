package dimasik.ui.screen.element.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.ui.screen.element.Element;
import dimasik.utils.client.StringUtils;

public class SelectElement
extends Element<SelectOption> {
    private boolean open = false;
    private final Animation offsetAnimation = new Animation();

    public SelectElement(SelectOption option) {
        super(20.0f, 20.0f, option);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float animate = this.getModule() != null ? this.getModule().getToggleFade().getAnimationValue() : 1.0f;
        float show = this.getModule() != null ? this.getShow().getAnimationValue() : 1.0f;
        int textColor1 = ColorHelpers.rgba(255, 255, 255, (122.39999999999999 + 132.6 * (double)animate) * (double)show);
        int valueColor = ColorHelpers.rgba(255, 255, 255, (91.8 + 91.8 * (double)animate) * (double)show);
        int i = 0;
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        float gw = 0.0f;
        for (SelectOptionValue value : ((SelectOption)this.option).getValues()) {
            value.getAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
            int rectColor = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, (2.5500000000000003 + 12.75 * (double)value.getAnimation().getAnimationValue()) * (double)show), ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 5.1000000000000005 * (double)show), ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 30.599999999999998 * (double)show), value.getAnimation().getAnimationValue() * show), animate * show);
            int outlineColor = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, (5.1000000000000005 + 25.5 * (double)value.getAnimation().getAnimationValue()) * (double)show), ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 10.200000000000001 * (double)show), ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 61.199999999999996 * (double)show), value.getAnimation().getAnimationValue() * show), animate * show);
            int textColor = ColorHelpers.rgba(255, 255, 255, (61.199999999999996 + 122.39999999999999 * (double)animate + 61.199999999999996 * (double)value.getAnimation().getAnimationValue() + 10.200000000000001 * (double)value.getAnimation().getAnimationValue() * (double)animate) * (double)show);
            VisualHelpers.drawRoundedRect(matrixStack, this.x + offsetX + 10.0f, this.y + suisse_intl.getHeight(12.0f) + 4.0f + offsetY, suisse_intl.getWidth(StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), 12.0f) + 12.0f, 22.0f, 4.0f, rectColor);
            VisualHelpers.drawRoundedOutline(matrixStack, this.x + offsetX + 10.0f, this.y + suisse_intl.getHeight(12.0f) + 4.0f + offsetY, suisse_intl.getWidth(StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), 12.0f) + 12.0f, 22.0f, 4.0f, 1.0f, outlineColor);
            suisse_intl.drawText(matrixStack, StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), this.x + offsetX + 16.0f, this.y + suisse_intl.getHeight(12.0f) / 2.0f + 15.0f + offsetY, textColor, 12.0f);
            offsetX += suisse_intl.getWidth(StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), 12.0f) + 14.0f;
            gw += suisse_intl.getWidth(((SelectOption)this.option).getValues()[i + 1 >= ((SelectOption)this.option).getValues().length ? i : i + 1].getVisualName(), 12.0f) + 12.0f;
            if (offsetX + gw > this.width - 10.0f && i != ((SelectOption)this.option).getValues().length - 1) {
                offsetX = 0.0f;
                offsetY += 24.0f;
                gw = 0.0f;
            }
            ++i;
        }
        suisse_intl.drawText(matrixStack, ((SelectOption)this.option).getVisualName(), this.x + 10.0f, this.y, textColor1, 12.0f);
        this.setHeight(suisse_intl.getHeight(12.0f) + 26.0f + offsetY);
    }

    @Override
    public void exit() {
    }

    @Override
    public void tick() {
        this.offsetAnimation.update(this.open);
        for (SelectOptionValue optionValue : ((SelectOption)this.option).getValues()) {
            optionValue.getAnimation().update(((SelectOption)this.option).getValue() == optionValue);
        }
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
        int i = 0;
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        float gw = 0.0f;
        for (SelectOptionValue value : ((SelectOption)this.option).getValues()) {
            if (ScreenHelpers.isHovered(mouseX, mouseY, this.x + offsetX + 10.0f, this.y + suisse_intl.getHeight(12.0f) + 4.0f + offsetY, suisse_intl.getWidth(StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), 12.0f) + 12.0f, 22.0f)) {
                ((SelectOption)this.option).setValue(value);
            }
            if ((offsetX += suisse_intl.getWidth(StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), 12.0f) + 14.0f) + (gw += suisse_intl.getWidth(((SelectOption)this.option).getValues()[i + 1 >= ((SelectOption)this.option).getValues().length ? i : i + 1].getVisualName(), 12.0f) + 12.0f) > this.width - 10.0f && i != ((SelectOption)this.option).getValues().length - 1) {
                offsetX = 0.0f;
                offsetY += 24.0f;
                gw = 0.0f;
            }
            ++i;
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
        ((SelectOption)this.option).setVisualName(this.getTranslation(((SelectOption)this.option).getSettingName()));
        for (SelectOptionValue optionValue : ((SelectOption)this.option).getValues()) {
            optionValue.setVisualName(this.getTranslation(optionValue.getName()));
        }
    }
}
