package dimasik.ui.screen.element.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.ui.screen.element.Element;
import dimasik.utils.client.KeyUtils;
import dimasik.utils.client.StringUtils;
import net.minecraft.util.ResourceLocation;

public class CheckboxElement
extends Element<CheckboxOption> {
    private final Animation animation = new Animation();
    private boolean bind;

    public CheckboxElement(CheckboxOption option) {
        super(20.0f, 20.0f, option);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.animation.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
        float animate = ((CheckboxOption)this.option).getModule() != null ? ((CheckboxOption)this.option).getModule().getToggleFade().getAnimationValue() : 1.0f;
        float show = this.getModule() != null ? this.getShow().getAnimationValue() : 1.0f;
        int textColor = ColorHelpers.rgba(255, 255, 255, (122.39999999999999 + 132.6 * (double)animate) * (double)show * (double)(1.0f - ((CheckboxOption)this.option).getBindAnimation().getAnimationValue()));
        int bindColor = ColorHelpers.rgba(255, 255, 255, (122.39999999999999 + 132.6 * (double)animate) * (double)show * (double)((CheckboxOption)this.option).getBindAnimation().getAnimationValue());
        int boxColor = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 30.599999999999998), ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 61.199999999999996), this.animation.getAnimationValue() * animate * show);
        int inBoxColor = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 15.299999999999999 * (double)this.animation.getAnimationValue() * (double)show), ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 30.599999999999998 * (double)this.animation.getAnimationValue() * (double)show), animate * show);
        int checkColor = ColorHelpers.rgba(255, 255, 255, 122.39999999999999 * (double)this.animation.getAnimationValue() + 132.6 * (double)animate * (double)this.animation.getAnimationValue() * (double)show);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width - 22.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f + 0.5f, 12.0f, 12.0f, 3.0f, inBoxColor);
        VisualHelpers.drawRoundedOutline(matrixStack, this.x + this.width - 22.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f + 0.5f, 12.0f, 12.0f, 3.0f, 1.0f, boxColor);
        VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/checkmark.png"), this.x + this.width - 19.75f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f + 3.0f, 8.0f, 8.0f, checkColor);
        String bindValue = ": " + KeyUtils.getKey(((CheckboxOption)this.option).getKey());
        ((CheckboxOption)this.option).getBindAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, CheckboxElement.mc.getTimer().renderPartialTicks);
        suisse_intl.drawText(matrixStack, StringUtils.trim(((CheckboxOption)this.option).getVisualName(), this.width - 22.0f, suisse_intl, 12.0f), this.x + 10.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, textColor, 12.0f);
        suisse_intl.drawText(matrixStack, StringUtils.trim(((CheckboxOption)this.option).getBindName() + bindValue, this.width - 22.0f, suisse_intl, 12.0f), this.x + 10.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, bindColor, 12.0f);
    }

    @Override
    public void exit() {
    }

    @Override
    public void tick() {
        ((CheckboxOption)this.option).getBindAnimation().update(this.bind);
        this.animation.update((Boolean)((CheckboxOption)this.option).getValue());
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.bind) {
            if (keyCode == 261) {
                ((CheckboxOption)this.option).setKey(-1);
                this.bind = false;
                return;
            }
            ((CheckboxOption)this.option).setKey(keyCode);
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
            ((CheckboxOption)this.option).setKey(button);
            this.bind = false;
        }
        if (ScreenHelpers.isHovered((int)mouseX, (int)mouseY, this.x + this.width - this.height - 5.0f, this.y, this.height, this.height)) {
            if (button == 0) {
                ((CheckboxOption)this.option).setValue((Boolean)((CheckboxOption)this.option).getValue() == false);
            }
            if (button == 2) {
                this.bind = !this.bind;
                Load.getInstance().getUiScreen().setSearching(false);
            }
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
        ((CheckboxOption)this.option).setVisualName(this.getTranslation(((CheckboxOption)this.option).getSettingName()));
        ((CheckboxOption)this.option).setBindName(this.getTranslation("Bind"));
    }
}
