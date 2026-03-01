package dimasik.ui.screen.component.main;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.visual.SmartScissorHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.option.api.Option;
import dimasik.managers.module.option.main.BindOption;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.ColorOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.managers.module.option.main.StringOption;
import dimasik.ui.screen.UIScreen;
import dimasik.ui.screen.component.Component;
import dimasik.ui.screen.element.Element;
import dimasik.ui.screen.element.main.BindElement;
import dimasik.ui.screen.element.main.CheckboxElement;
import dimasik.ui.screen.element.main.ColorElement;
import dimasik.ui.screen.element.main.MultiSelectElement;
import dimasik.ui.screen.element.main.SelectElement;
import dimasik.ui.screen.element.main.SliderElement;
import dimasik.ui.screen.element.main.StringElement;
import lombok.Generated;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector4f;

public class ModuleComponent
extends Component {
    private final Multimap<Module, Element<? extends Option<?>>> components = ArrayListMultimap.create();
    private final Module module;
    public boolean bind = false;
    public boolean open = false;
    private float animHeight = 0.0f;
    private final Animation animation = new Animation();

    public ModuleComponent(float x, float y, float width, float height, UIScreen parent, Module module) {
        super(x, y, width, height, parent);
        this.module = module;
        Load.getInstance().getHooks().getModuleManagers().forEach(modules -> modules.getSettingList().forEach(option -> {
            if (option instanceof CheckboxOption) {
                CheckboxOption checkboxOption = (CheckboxOption)option;
                this.components.put((Module)modules, new CheckboxElement(checkboxOption.setModule((Module)modules)));
            }
            if (option instanceof SelectOption) {
                SelectOption selectOption = (SelectOption)option;
                this.components.put((Module)modules, new SelectElement(selectOption).setModule((Module)modules));
            }
            if (option instanceof MultiOption) {
                MultiOption multiOption = (MultiOption)option;
                this.components.put((Module)modules, new MultiSelectElement(multiOption.setModule((Module)modules)));
            }
            if (option instanceof SliderOption) {
                SliderOption sliderOption = (SliderOption)option;
                this.components.put((Module)modules, new SliderElement(sliderOption).setModule((Module)modules));
            }
            if (option instanceof BindOption) {
                BindOption bindOption = (BindOption)option;
                this.components.put((Module)modules, new BindElement(bindOption).setModule((Module)modules));
            }
            if (option instanceof StringOption) {
                StringOption stringOption = (StringOption)option;
                this.components.put((Module)modules, new StringElement(stringOption).setModule((Module)modules));
            }
            if (option instanceof ColorOption) {
                ColorOption colorOption = (ColorOption)option;
                this.components.put((Module)modules, new ColorElement(colorOption).setModule((Module)modules));
            }
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        String bind = "";
        if (this.bind) {
            bind = "...";
        }
        this.cursorLogic(mouseX, mouseY, this.x, this.y, this.width, this.height);
        this.module.getToggleFade().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, ModuleComponent.mc.getTimer().renderPartialTicks);
        float y = this.getY() + this.height + 5.0f;
        int back = ColorHelpers.rgba(190, 190, 190, 5.1000000000000005 + 10.200000000000001 * (double)this.module.getToggleFade().getAnimationValue());
        int outline = ColorHelpers.rgba(190, 190, 190, 15.299999999999999 + 10.200000000000001 * (double)this.module.getToggleFade().getAnimationValue());
        int indicator = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 255.0f * this.module.getToggleFade().getAnimationValue());
        int settings = ColorHelpers.rgba(255, 255, 255, 30.599999999999998);
        int settingsBack = ColorHelpers.rgba(190, 190, 190, 2.5500000000000003 + 7.6499999999999995 * (double)this.module.getToggleFade().getAnimationValue());
        int glow = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 30.599999999999998 * (double)this.module.getToggleFade().getAnimationValue());
        Vector4f round = new Vector4f(6.0f, 6.0f, 6.0f, 6.0f);
        if (this.animation.getAnimationValue() > 0.0f) {
            round = new Vector4f(0.0f, 6.0f, 0.0f, 6.0f);
        }
        VisualHelpers.drawRoundedRect(matrixStack, this.x, this.getY(), this.width, this.getHeight(), round, back, glow, back, back);
        VisualHelpers.drawRoundedOutline(matrixStack, this.x, this.getY(), this.width, this.animHeight, 6.0f, 1.3f, outline);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width - 27.0f, this.getY(), 24.0f, 2.0f, new Vector4f(0.0f, 2.0f, 2.0f, 0.0f), indicator);
        if (!this.module.getSettingList().isEmpty()) {
            VisualHelpers.drawRoundedTexture(matrixStack, new ResourceLocation("main/textures/images/settings.png"), this.x + this.width - 26.0f, y - this.height + 2.0f, 16.0f, 16.0f, 0.0f, 0.12f + 0.36f * this.module.getToggleFade().getAnimationValue());
        }
        VisualHelpers.drawRoundedRect(matrixStack, this.x, this.getY() + this.getHeight() - 1.0f, this.width, this.animHeight - this.getHeight(), new Vector4f(6.0f, 0.0f, 6.0f, 0.0f), settingsBack);
        int interpolateColor = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 122.39999999999999), ColorHelpers.rgba(255, 255, 255, 255), this.module.getToggleFade().getAnimationValue());
        suisse_intl.drawText(matrixStack, this.module.getName() + bind, this.x + 10.0f, this.getY() + this.height / 2.0f - sf_medium.getHeight(14.0f) / 2.0f, interpolateColor, 14.0f);
        this.animation.animate(0.0f, 1.0f, 0.25f, EasingList.CIRC_OUT, ModuleComponent.mc.getTimer().renderPartialTicks);
        matrixStack.push();
        if (Load.getInstance().getUiScreen().getAnimation().getAnimationValue() == 1.0f && Load.getInstance().getUiScreen().getAnimation().getValue() == 1.0f) {
            SmartScissorHelpers.enable(this.x - 100.0f, this.getY() + this.getHeight(), this.width + 200.0f, this.animHeight + 100.0f);
        }
        GLHelpers.INSTANCE.scaleAnimationHeight(matrixStack, this.x, this.getY(), this.width, this.height, this.animation.getAnimationValue());
        for (Element<Option<?>> component : this.components.get(this.module)) {
            component.getShow().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, ModuleComponent.mc.getTimer().renderPartialTicks);
            if (!((double)component.getShow().getAnimationValue() > 0.1)) continue;
            component.setX(this.getX());
            component.setY(y -= component.getHeight() * (1.0f - component.getShow().getAnimationValue()));
            component.setWidth(this.getWidth());
            if (this.animation.getAnimationValue() > 0.05f) {
                component.render(matrixStack, mouseX, mouseY, partialTicks);
            }
            y += component.getHeight() + 5.0f;
            this.height += (component.getHeight() + 6.0f) * component.getShow().getAnimationValue() * this.animation.getAnimationValue();
        }
        if (Load.getInstance().getUiScreen().getAnimation().getAnimationValue() == 1.0f && Load.getInstance().getUiScreen().getAnimation().getValue() == 1.0f) {
            SmartScissorHelpers.disable();
        }
        matrixStack.pop();
        if (this.animation.getAnimationValue() > 0.05f) {
            this.height += 5.0f;
        }
        this.animHeight = this.height;
    }

    @Override
    public void exit() {
        for (Element<Option<?>> component : this.components.get(this.module)) {
            if (!component.getOption().getVisible().getAsBoolean()) continue;
            component.exit();
        }
    }

    @Override
    public void tick() {
        this.animation.update(this.open);
        this.module.getToggleFade().update(this.module.isToggled());
        for (Element<Option<?>> component : this.components.get(this.module)) {
            component.getShow().update(component.getOption().getVisible().getAsBoolean());
            if (!((double)component.getShow().getAnimationValue() > 0.1)) continue;
            component.tick();
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.bind) {
            if (keyCode == 261 || keyCode == 256) {
                this.module.setCurrentKey(-1);
            } else {
                this.module.setCurrentKey(keyCode);
            }
            this.bind = false;
        }
        for (Element<Option<?>> component : this.components.get(this.module)) {
            if (!component.getOption().getVisible().getAsBoolean()) continue;
            component.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (Element<Option<?>> component : this.components.get(this.module)) {
            if (!component.getOption().getVisible().getAsBoolean()) continue;
            component.charTyped(codePoint, modifiers);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (this.bind) {
            this.module.setCurrentKey(button);
            this.bind = false;
        }
        if (this.isSomeElementHovered()) {
            if (button == 0) {
                this.module.toggle();
            }
            if (button == 1 && this.components.get(this.module).stream().count() >= 1L) {
                boolean bl = this.open = !this.open;
            }
            if (button == 2) {
                this.bind = true;
            }
        }
        if (this.open) {
            for (Element<Option<?>> component : this.components.get(this.module)) {
                if (!component.getOption().getVisible().getAsBoolean() || !((double)component.getShow().getAnimationValue() > 0.9)) continue;
                component.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (this.open) {
            for (Element<Option<?>> component : this.components.get(this.module)) {
                if (!component.getOption().getVisible().getAsBoolean() || !((double)component.getShow().getAnimationValue() > 0.9)) continue;
                component.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
    }

    @Override
    public void translate() {
        for (Element<Option<?>> component : this.components.get(this.module)) {
            component.translate();
        }
    }

    @Generated
    public Multimap<Module, Element<? extends Option<?>>> getComponents() {
        return this.components;
    }

    @Generated
    public Module getModule() {
        return this.module;
    }

    @Generated
    public boolean isBind() {
        return this.bind;
    }

    @Generated
    public boolean isOpen() {
        return this.open;
    }

    @Generated
    public float getAnimHeight() {
        return this.animHeight;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }
}
