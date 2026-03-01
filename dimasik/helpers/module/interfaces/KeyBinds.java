package dimasik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.visual.StencilHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.draggable.api.Component;
import dimasik.managers.module.Module;
import dimasik.managers.module.option.api.Option;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.utils.client.KeyUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

public class KeyBinds
extends Component {
    private final CheckboxOption hide = new CheckboxOption("Hide", true);
    private float widthModule = 0.0f;
    private float widthBind = 0.0f;
    private float width = 0.0f;
    private float key = 0.0f;
    private final Animation animation = new Animation();

    public KeyBinds() {
        super("KeyBinds", new Vector2f(10.0f, 46.0f), 114.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression(), this.hide);
    }

    @Override
    public void update(EventUpdate event) {
        boolean checkBox = false;
        for (Module module : Load.getInstance().getHooks().getModuleManagers().stream().filter(Module::hasBind).toList()) {
            module.getAnimation().update(module.isToggled());
        }
        for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
            for (Option<?> option : module.getSettingList()) {
                CheckboxOption checkboxOption;
                if (option instanceof CheckboxOption && (checkboxOption = (CheckboxOption)option).getKey() >= 0) {
                    checkboxOption.getAnimation().update((Boolean)checkboxOption.getValue());
                    if (!checkBox) {
                        checkBox = (Boolean)checkboxOption.getValue();
                    }
                }
                if (!(option instanceof MultiOption)) continue;
                MultiOption multiOption = (MultiOption)option;
                for (MultiOptionValue value : multiOption.getValues()) {
                    value.getAnim().update(value.isToggle() && value.getKey() >= 0);
                    if (value.getKey() < 0 || checkBox) continue;
                    checkBox = value.isToggle();
                }
            }
        }
        boolean show = (!Load.getInstance().getHooks().getModuleManagers().stream().filter(Module::isToggled).filter(Module::hasBind).toList().isEmpty() || checkBox || KeyBinds.mc.currentScreen instanceof ChatScreen) && Load.getInstance().getHooks().getModuleManagers().getInterfaces().getElements().getSelected("KeyBinds") || (Boolean)this.hide.getValue() == false;
        this.getShowAnimation().update(show);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        MatrixStack matrixStack = event.getMatrixStack();
        float height = 46.0f;
        float staticWidth = 192.0f;
        float keyWidth = 10.0f;
        matrixStack.push();
        for (Module module : Load.getInstance().getHooks().getModuleManagers().stream().filter(Module::hasBind).toList()) {
            if (!((double)module.getAnimation().getAnimationValue() > 0.1)) continue;
            height += 18.0f * module.getAnimation().getAnimationValue();
            staticWidth = Math.max(staticWidth, suisse_intl.getWidth(module.getName(), 13.0f) + suisse_intl.getWidth(KeyUtils.getKey(module.getCurrentKey()), 13.0f));
            keyWidth = Math.max(keyWidth, suisse_intl.getWidth(KeyUtils.getKey(module.getCurrentKey()), 13.0f));
        }
        for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
            for (Option<?> option : module.getSettingList()) {
                CheckboxOption checkboxOption;
                if (option instanceof CheckboxOption && (checkboxOption = (CheckboxOption)option).getKey() >= 0) {
                    height += 18.0f * checkboxOption.getAnimation().getAnimationValue();
                    staticWidth = Math.max(staticWidth, suisse_intl.getWidth(checkboxOption.getVisualName(), 13.0f) + suisse_intl.getWidth(KeyUtils.getKey(checkboxOption.getKey()), 13.0f));
                    keyWidth = Math.max(keyWidth, suisse_intl.getWidth(KeyUtils.getKey(checkboxOption.getKey()), 13.0f));
                }
                if (!(option instanceof MultiOption)) continue;
                MultiOption multiOption = (MultiOption)option;
                for (MultiOptionValue value : multiOption.getValues()) {
                    if (value.getKey() < 0) continue;
                    height += 18.0f * value.getAnim().getAnimationValue();
                    staticWidth = Math.max(staticWidth, suisse_intl.getWidth(value.getVisualName(), 13.0f) + suisse_intl.getWidth(KeyUtils.getKey(value.getKey()), 13.0f));
                    keyWidth = Math.max(keyWidth, suisse_intl.getWidth(KeyUtils.getKey(value.getKey()), 13.0f));
                }
            }
        }
        this.width = Animation.animate(this.width, staticWidth);
        this.key = Animation.animate(this.key, keyWidth);
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, KeyBinds.mc.getTimer().renderPartialTicks);
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.getShowAnimation().getAnimationValue());
        int glow = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(30.599999999999998 * (double)this.getShowAnimation().getAnimationValue()));
        int back2 = ColorHelpers.rgba(15, 15, 15, 61.199999999999996 * (double)this.getShowAnimation().getAnimationValue());
        int indicator = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue()));
        if ((double)this.getShowAnimation().getAnimationValue() > 0.1) {
            if (this.getDesign().getSelected("Transparent")) {
                float finalHeight = height;
                BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, finalHeight, 12.0f, back));
                this.blurSetting(KeyBinds.mc.getTimer().renderPartialTicks, 10.0f, ((Float)this.getCompression().getValue()).floatValue());
                VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, height, 12.0f, ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 40.800000000000004 * (double)this.getShowAnimation().getAnimationValue()));
            } else if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, height, 12.0f, back);
            }
            VisualHelpers.drawRoundedOutline(matrixStack, x, y, this.width, height, 12.0f, 2.0f, ColorHelpers.rgba(190, 190, 190, 15.299999999999999 * (double)this.getShowAnimation().getAnimationValue()));
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, height, 12.0f, -1);
            StencilHelpers.read(1);
            if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawGlow(matrixStack, x, y, this.width, 192.0f, 40.0f, glow);
            }
            VisualHelpers.drawRoundedRect(matrixStack, x + this.width - 30.0f, y, 26.0f, 3.0f, new Vector4f(0.0f, 4.0f, 4.0f, 0.0f), indicator);
            suisse_intl.drawText(matrixStack, "Keybinds", x + 12.0f, y + 15.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue())), 14.0f);
            dimasIcon.drawText(matrixStack, "B", x + this.width - 26.0f, y + 14.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue())), 12.0f);
            float i = 32.0f;
            for (Module module : Load.getInstance().getHooks().getModuleManagers().stream().filter(Module::hasBind).toList()) {
                module.getAnimation().animate(0.0f, 1.0f, 0.3f, EasingList.CIRC_OUT, KeyBinds.mc.getTimer().renderPartialTicks);
                if (!((double)module.getAnimation().getAnimationValue() > 0.1)) continue;
                dimasIcon.drawText(matrixStack, module.getCategory().getPath(), x + 12.0f, y + i + 10.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * module.getAnimation().getAnimationValue() * this.getShowAnimation().getAnimationValue())), 12.0f);
                suisse_intl.drawText(matrixStack, module.getName(), x + 29.0f, y + i + 10.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * module.getAnimation().getAnimationValue() * this.getShowAnimation().getAnimationValue())), 12.0f);
                suisse_intl.drawCenteredText(matrixStack, KeyUtils.getKey(module.getCurrentKey()), x + this.width - this.key / 2.0f - 12.0f, y + i + 10.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue() * module.getAnimation().getAnimationValue())), 12.0f);
                i += 18.0f;
            }
            for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
                for (Option<?> option : module.getSettingList()) {
                    CheckboxOption checkboxOption;
                    if (option instanceof CheckboxOption && (checkboxOption = (CheckboxOption)option).getKey() >= 0) {
                        checkboxOption.getAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, KeyBinds.mc.getTimer().renderPartialTicks);
                        if ((double)checkboxOption.getAnimation().getAnimationValue() > 0.1) {
                            if (checkboxOption.getModule() != null) {
                                dimasIcon.drawText(matrixStack, checkboxOption.getModule().getCategory().getPath(), x + 12.0f, y + i + 10.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * checkboxOption.getAnimation().getAnimationValue() * this.getShowAnimation().getAnimationValue())), 12.0f);
                            }
                            suisse_intl.drawText(matrixStack, checkboxOption.getVisualName(), x + 29.0f, y + i + 10.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * checkboxOption.getAnimation().getAnimationValue() * this.getShowAnimation().getAnimationValue())), 12.0f);
                            suisse_intl.drawCenteredText(matrixStack, KeyUtils.getKey(checkboxOption.getKey()), x + this.width - this.key / 2.0f - 12.0f, y + i + 10.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)((float)((int)(255.0f * this.getShowAnimation().getAnimationValue())) * checkboxOption.getAnimation().getAnimationValue())), 12.0f);
                            i += 18.0f;
                        }
                    }
                    if (!(option instanceof MultiOption)) continue;
                    MultiOption multiOption = (MultiOption)option;
                    for (MultiOptionValue value : multiOption.getValues()) {
                        value.getAnim().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, KeyBinds.mc.getTimer().renderPartialTicks);
                        if (!((double)value.getAnim().getAnimationValue() > 0.1)) continue;
                        if (multiOption.getModule() != null) {
                            dimasIcon.drawText(matrixStack, multiOption.getModule().getCategory().getPath(), x + 12.0f, y + i + 10.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * value.getAnim().getAnimationValue() * this.getShowAnimation().getAnimationValue())), 12.0f);
                        }
                        suisse_intl.drawText(matrixStack, value.getVisualName(), x + 29.0f, y + i + 10.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * value.getAnim().getAnimationValue() * this.getShowAnimation().getAnimationValue())), 12.0f);
                        suisse_intl.drawCenteredText(matrixStack, KeyUtils.getKey(value.getKey()), x + this.width - this.key / 2.0f - 12.0f, y + i + 10.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)((float)((int)(255.0f * this.getShowAnimation().getAnimationValue())) * value.getAnim().getAnimationValue())), 12.0f);
                        i += 18.0f;
                    }
                }
            }
            StencilHelpers.uninit();
            matrixStack.pop();
            this.getDraggableOption().setWidth(this.width);
            this.getDraggableOption().setHeight(height);
        }
    }
}
