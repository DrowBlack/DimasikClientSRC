package dimasik.ui.screen.element.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.ui.screen.element.Element;
import dimasik.utils.client.KeyUtils;
import dimasik.utils.client.StringUtils;

public class MultiSelectElement
extends Element<MultiOption> {
    private boolean bind;

    public MultiSelectElement(MultiOption option) {
        super(20.0f, 20.0f, option);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float animate = ((MultiOption)this.option).getModule() != null ? ((MultiOption)this.option).getModule().getToggleFade().getAnimationValue() : 1.0f;
        float show = this.getModule() != null ? this.getShow().getAnimationValue() : 1.0f;
        int textColor1 = ColorHelpers.rgba(255, 255, 255, (122.39999999999999 + 132.6 * (double)animate) * (double)show);
        int valueColor = ColorHelpers.rgba(255, 255, 255, (91.8 + 91.8 * (double)animate) * (double)show);
        int active = 0;
        int i = 0;
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        float gw = 0.0f;
        for (MultiOptionValue value : ((MultiOption)this.option).getValues()) {
            value.getAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
            value.getBindAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, MultiSelectElement.mc.getTimer().renderPartialTicks);
            int rectColor = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, (2.5500000000000003 + 12.75 * (double)value.getAnimation().getAnimationValue()) * (double)show), ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 5.1000000000000005 * (double)show), ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 30.599999999999998 * (double)show), value.getAnimation().getAnimationValue() * show), animate * show);
            int outlineColor = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, (5.1000000000000005 + 25.5 * (double)value.getAnimation().getAnimationValue()) * (double)show), ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 10.200000000000001 * (double)show), ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 61.199999999999996 * (double)show), value.getAnimation().getAnimationValue() * show), animate * show);
            int textColor = ColorHelpers.rgba(255, 255, 255, (61.199999999999996 + 122.39999999999999 * (double)animate + 61.199999999999996 * (double)value.getAnimation().getAnimationValue() + 10.200000000000001 * (double)value.getAnimation().getAnimationValue() * (double)animate) * (double)show * (double)(1.0f - value.getBindAnimation().getAnimationValue()));
            int bindColor = ColorHelpers.rgba(255, 255, 255, (61.199999999999996 + 122.39999999999999 * (double)animate + 61.199999999999996 * (double)value.getAnimation().getAnimationValue() + 10.200000000000001 * (double)value.getAnimation().getAnimationValue() * (double)animate) * (double)show * (double)value.getBindAnimation().getAnimationValue());
            float staticWidth = suisse_intl.getWidth(StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), 12.0f);
            String bindValue = ": " + KeyUtils.getKey(value.getKey());
            if ((double)value.getBindAnimation().getAnimationValue() > 0.1) {
                staticWidth = suisse_intl.getWidth(StringUtils.trim(value.getBindName() + bindValue, this.width - 10.0f, suisse_intl, 12.0f), 12.0f);
            }
            value.setNameWidth(Animation.animate(value.getNameWidth(), staticWidth));
            VisualHelpers.drawRoundedRect(matrixStack, this.x + offsetX + 10.0f, this.y + suisse_intl.getHeight(12.0f) + 4.0f + offsetY, value.getNameWidth() + 12.0f, 22.0f, 4.0f, rectColor);
            VisualHelpers.drawRoundedOutline(matrixStack, this.x + offsetX + 10.0f, this.y + suisse_intl.getHeight(12.0f) + 4.0f + offsetY, value.getNameWidth() + 12.0f, 22.0f, 4.0f, 1.0f, outlineColor);
            suisse_intl.drawText(matrixStack, StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), this.x + offsetX + 16.0f, this.y + suisse_intl.getHeight(12.0f) / 2.0f + 15.0f + offsetY, textColor, 12.0f);
            suisse_intl.drawText(matrixStack, StringUtils.trim(value.getBindName() + bindValue, this.width - 10.0f, suisse_intl, 12.0f), this.x + offsetX + 16.0f, this.y + suisse_intl.getHeight(12.0f) / 2.0f + 15.0f + offsetY, bindColor, 12.0f);
            offsetX += value.getNameWidth() + 14.0f;
            gw += suisse_intl.getWidth(((MultiOption)this.option).getValues()[i + 1 >= ((MultiOption)this.option).getValues().length ? i : i + 1].getVisualName(), 12.0f) + 12.0f;
            if (offsetX + gw > this.width - 10.0f && i != ((MultiOption)this.option).getValues().length - 1) {
                offsetX = 0.0f;
                offsetY += 24.0f;
                gw = 0.0f;
            }
            if (value.isToggle()) {
                ++active;
            }
            ++i;
        }
        suisse_intl.drawText(matrixStack, ((MultiOption)this.option).getVisualName(), this.x + 10.0f, this.y, textColor1, 12.0f);
        suisse_intl.drawText(matrixStack, active + "/" + ((MultiOption)this.option).getValues().length, this.x - suisse_intl.getWidth(active + "/" + ((MultiOption)this.option).getValues().length, 12.0f) + this.width - 10.0f, this.y, valueColor, 12.0f);
        this.setHeight(suisse_intl.getHeight(12.0f) + 26.0f + offsetY);
    }

    @Override
    public void exit() {
    }

    @Override
    public void tick() {
        for (MultiOptionValue value : ((MultiOption)this.option).getValues()) {
            value.getAnimation().update(value.isToggle());
            value.getBindAnimation().update(value.isBind());
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (MultiOptionValue value : ((MultiOption)this.option).getValues()) {
            if (!value.isBind()) continue;
            if (keyCode == 261) {
                value.setKey(-1);
                value.setBind(false);
                return;
            }
            value.setKey(keyCode);
            value.setBind(false);
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
        int i = 0;
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        float gw = 0.0f;
        for (MultiOptionValue value : ((MultiOption)this.option).getValues()) {
            if (value.isBind()) {
                value.setKey(button);
                value.setBind(false);
            }
            if (ScreenHelpers.isHovered(mouseX, mouseY, this.x + offsetX + 10.0f, this.y + suisse_intl.getHeight(12.0f) + 4.0f + offsetY, suisse_intl.getWidth(StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), 12.0f) + 12.0f, 22.0f)) {
                if (button == 0) {
                    value.setToggle(!value.isToggle());
                }
                if (button == 2) {
                    value.setBind(!value.isBind());
                    Load.getInstance().getUiScreen().setSearching(false);
                }
            }
            if ((offsetX += suisse_intl.getWidth(StringUtils.trim(value.getVisualName(), this.width - 10.0f, suisse_intl, 12.0f), 12.0f) + 14.0f) + (gw += suisse_intl.getWidth(((MultiOption)this.option).getValues()[i + 1 >= ((MultiOption)this.option).getValues().length ? i : i + 1].getVisualName(), 12.0f) + 12.0f) > this.width - 10.0f && i != ((MultiOption)this.option).getValues().length - 1) {
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
        ((MultiOption)this.option).setVisualName(this.getTranslation(((MultiOption)this.option).getSettingName()));
        for (MultiOptionValue multiOptionValue : ((MultiOption)this.option).getValues()) {
            multiOptionValue.setVisualName(this.getTranslation(multiOptionValue.getName()));
            multiOptionValue.setBindName(this.getTranslation("Bind"));
        }
    }
}
