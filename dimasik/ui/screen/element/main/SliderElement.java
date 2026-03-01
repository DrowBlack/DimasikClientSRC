package dimasik.ui.screen.element.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.ui.screen.element.Element;
import dimasik.utils.client.StringUtils;
import dimasik.utils.math.MathUtils;

public class SliderElement
extends Element<SliderOption> {
    private float valueAnimation = 0.0f;
    private boolean usingSlider;

    public SliderElement(SliderOption option) {
        super(20.0f, 20.0f, option);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float animate = this.getModule() != null ? this.getModule().getToggleFade().getAnimationValue() : 1.0f;
        this.valueAnimation = Animation.animate(this.valueAnimation, ((Float)((SliderOption)this.option).getValue()).floatValue());
        int textColor = ColorHelpers.rgba(255, 255, 255, 122.39999999999999 + 132.6 * (double)animate * (double)this.getShow().getAnimationValue());
        int valueColor = ColorHelpers.rgba(255, 255, 255, 61.199999999999996 + 122.39999999999999 * (double)animate * (double)this.getShow().getAnimationValue());
        int sliderColor = ColorHelpers.rgba(255, 255, 255, 61.199999999999996 + 193.8 * (double)animate * (double)this.getShow().getAnimationValue());
        int inSliderColor = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 30.599999999999998), ColorHelpers.getThemeColor(1), animate * this.getShow().getAnimationValue());
        int outSliderColor = ColorHelpers.rgba(190, 190, 190, 5.1000000000000005 + 5.1000000000000005 * (double)animate * (double)this.getShow().getAnimationValue());
        VisualHelpers.drawRoundedRect(matrixStack, this.x + 10.0f, this.y + 18.0f, this.width - 20.0f, 4.0f, 2.0f, outSliderColor);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + 10.0f, this.y + 18.0f, Math.max(8.0f, (this.width - 20.0f) * this.getPos() + 2.5f), 4.0f, 2.0f, inSliderColor);
        VisualHelpers.drawRoundedRect(matrixStack, Math.max(this.x + 10.0f, this.x + 5.0f + (this.width - 20.0f) * this.getPos()), this.y + 18.0f, 8.0f, 4.0f, 2.0f, sliderColor);
        suisse_intl.drawText(matrixStack, StringUtils.trim(((SliderOption)this.option).getVisualName(), this.width - suisse_intl.getWidth(String.valueOf(((SliderOption)this.option).getValue()), 12.0f) - 10.0f, suisse_intl, 12.0f), this.x + 10.0f, this.y, textColor, 12.0f);
        suisse_intl.drawText(matrixStack, String.valueOf(((SliderOption)this.option).getValue()), this.x + this.width - suisse_intl.getWidth(String.valueOf(((SliderOption)this.option).getValue()), 12.0f) - 10.0f, this.y, valueColor, 12.0f);
        if (this.usingSlider) {
            ((SliderOption)this.option).setValue(Float.valueOf(this.createSlider(this.x + 10.0f, mouseX)));
        }
        this.setHeight(22.0f);
    }

    @Override
    public void exit() {
        this.usingSlider = false;
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
        float x = this.getX() + 10.0f;
        float y = this.getY() + 16.0f;
        float height = 8.0f;
        float width = this.getWidth() - 20.0f;
        if (ScreenHelpers.isHovered(mouseX, mouseY, x, y, width, height)) {
            this.usingSlider = true;
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        this.usingSlider = false;
    }

    @Override
    public void translate() {
        ((SliderOption)this.option).setVisualName(this.getTranslation(((SliderOption)this.option).getSettingName()));
    }

    private float createSlider(float posX, float mouseX) {
        float width = this.getWidth() - 20.0f;
        float value = (mouseX - posX) / width * (((SliderOption)this.option).getMax() - ((SliderOption)this.option).getMin()) + ((SliderOption)this.option).getMin();
        value = (float)MathUtils.round(value, ((SliderOption)this.option).getIncrement());
        return value;
    }

    private float getPos() {
        float delta = ((SliderOption)this.option).getMax() - ((SliderOption)this.option).getMin();
        return (this.valueAnimation - ((SliderOption)this.option).getMin()) / delta;
    }
}
