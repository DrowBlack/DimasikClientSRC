package dimasik.ui.screen.element.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.option.main.StringOption;
import dimasik.ui.screen.element.Element;
import dimasik.utils.client.StringUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;

public class StringElement
extends Element<StringOption> {
    private final Animation animation = new Animation();
    private boolean click = false;

    public StringElement(StringOption option) {
        super(20.0f, 26.0f, option);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.animation.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
        float animate = this.getModule() != null ? this.getModule().getToggleFade().getAnimationValue() : 1.0f;
        float show = this.getModule() != null ? this.getShow().getAnimationValue() : 1.0f;
        int outlineColor = ColorHelpers.rgba(190, 190, 190, (5.1000000000000005 + 5.1000000000000005 * (double)animate + 5.1000000000000005 * (double)this.animation.getAnimationValue()) * (double)show);
        int backColor = ColorHelpers.rgba(190, 190, 190, (2.5500000000000003 + 2.5500000000000003 * (double)animate + 5.1000000000000005 * (double)this.animation.getAnimationValue()) * (double)show);
        int textColor = ColorHelpers.rgba(255, 255, 255, (91.8 + 91.8 * (double)animate) * (double)show);
        int valueColor = ColorHelpers.rgba(255, 255, 255, (30.599999999999998 + 30.599999999999998 * (double)animate) * (double)show);
        int inputColor = ColorHelpers.rgba(255, 255, 255, (122.39999999999999 + 132.6 * (double)animate) * (double)show);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + 10.0f, this.y, this.width - 20.0f, this.height, 4.0f, backColor);
        VisualHelpers.drawRoundedOutline(matrixStack, this.x + 10.0f, this.y, this.width - 20.0f, this.height, 4.0f, 1.0f, outlineColor);
        if (!this.click) {
            suisse_intl.drawText(matrixStack, StringUtils.trim(((StringOption)this.option).getVisualName(), (this.width - 9.0f) / 2.0f, suisse_intl, 12.0f), this.x + 18.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, textColor, 12.0f);
            suisse_intl.drawText(matrixStack, StringUtils.trim((String)((StringOption)this.option).getValue(), (this.width - 9.0f) / 2.0f, suisse_intl, 12.0f), this.x + this.width - suisse_intl.getWidth(StringUtils.trim((String)((StringOption)this.option).getValue(), (this.width - 9.0f) / 2.0f, suisse_intl, 12.0f), 12.0f) - 18.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, valueColor, 12.0f);
        } else {
            suisse_intl.drawText(matrixStack, StringUtils.trim((String)((StringOption)this.option).getValue(), this.width - 28.0f, suisse_intl, 12.0f), this.x + 18.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, inputColor, 12.0f);
        }
    }

    @Override
    public void exit() {
        this.click = false;
    }

    @Override
    public void tick() {
        this.animation.update(this.click);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.click) {
            if (keyCode == 259 && !((String)((StringOption)this.option).getValue()).isEmpty()) {
                ((StringOption)this.option).setValue(((String)((StringOption)this.option).getValue()).substring(0, ((String)((StringOption)this.option).getValue()).length() - 1));
            }
            if (keyCode == 261) {
                ((StringOption)this.option).setValue("");
            }
            if (Screen.isPaste(keyCode)) {
                ((StringOption)this.option).setValue((String)((StringOption)this.option).getValue() + StringElement.mc.keyboardListener.getClipboardString());
            }
        }
        if (keyCode == 257) {
            this.click = false;
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        if (this.click && SharedConstants.isAllowedCharacter(codePoint) && ((StringOption)this.option).getCurrentType() == StringOption.Type.Default) {
            ((StringOption)this.option).setValue((String)((StringOption)this.option).getValue() + codePoint);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        this.click = ScreenHelpers.isHovered(mouseX, mouseY, this.x + 10.0f, this.y, this.width - 20.0f, this.height) ? !this.click : false;
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    @Override
    public void translate() {
        ((StringOption)this.option).setVisualName(this.getTranslation(((StringOption)this.option).getSettingName()));
    }
}
