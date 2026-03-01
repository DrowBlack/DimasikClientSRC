package dimasik.managers.draggable.api;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.visual.StencilHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.api.Option;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.DraggableOption;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.ui.screen.element.Element;
import dimasik.ui.screen.element.main.CheckboxElement;
import dimasik.ui.screen.element.main.SelectElement;
import dimasik.ui.screen.element.main.SliderElement;
import lombok.Generated;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

public abstract class Component
extends Module {
    private final Multimap<DraggableOption, Element<? extends Option<?>>> components = ArrayListMultimap.create();
    private final DraggableOption draggableOption;
    private final SelectOption design = new SelectOption("Design", 0, new SelectOptionValue("Standard"), new SelectOptionValue("Transparent"));
    private final SliderOption compression = new SliderOption("Compression", 1.0f, 1.0f, 8.0f).visible(() -> this.design.getSelected("Transparent")).increment(1.0f);
    private final Animation showAnimation = new Animation();

    public Component(String name, Vector2f value, float width, float height) {
        super(name, Category.RENDER);
        this.draggableOption = new DraggableOption(this.getName(), value, width, height);
        this.settings(this.draggableOption);
    }

    public abstract void update(EventUpdate var1);

    public abstract void render(EventRender2D.Pre var1);

    public void initSettings() {
        for (Option<?> option : this.draggableOption.getOptions()) {
            if (option instanceof CheckboxOption) {
                CheckboxOption checkboxOption = (CheckboxOption)option;
                this.components.put(this.draggableOption, new CheckboxElement(checkboxOption));
            }
            if (option instanceof SliderOption) {
                SliderOption sliderOption = (SliderOption)option;
                this.components.put(this.draggableOption, new SliderElement(sliderOption));
            }
            if (!(option instanceof SelectOption)) continue;
            SelectOption selectOption = (SelectOption)option;
            this.components.put(this.draggableOption, new SelectElement(selectOption));
        }
    }

    public void renderSettings(MatrixStack matrixStack, int mouseX, int mouseY, float partialsTicks) {
        GLHelpers.INSTANCE.rescale(1.0);
        float height = 0.0f;
        float width = 192.0f;
        float x = ((Vector2f)this.draggableOption.getValue()).x + this.draggableOption.getWidth();
        float y = ((Vector2f)this.draggableOption.getValue()).y;
        for (Element<Option<?>> component : this.components.get(this.draggableOption)) {
            component.setX(x);
            component.setY(y + height * component.getShow().getAnimationValue() + 10.0f);
            component.setWidth(192.0f);
            height += (component.getHeight() + 6.0f) * component.getShow().getAnimationValue();
        }
        height += 14.0f;
        int glow = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(30.599999999999998 * (double)this.getShowAnimation().getAnimationValue()));
        matrixStack.push();
        GLHelpers.INSTANCE.scaleAnimation(matrixStack, x, y, 0.0f, 0.0f, this.draggableOption.getClickAnimation().getAnimationValue());
        int back = ColorHelpers.rgba(15, 15, 15, 255);
        if (this.design.getSelected("Transparent")) {
            float finalHeight = height;
            BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, x, y, width, finalHeight, 12.0f, back));
            this.blurSetting(Component.mc.getTimer().renderPartialTicks, 10.0f, ((Float)this.getCompression().getValue()).floatValue());
            VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 40.800000000000004 * (double)this.getShowAnimation().getAnimationValue()));
        } else if (this.design.getSelected("Standard")) {
            VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, back);
        }
        VisualHelpers.drawRoundedOutline(matrixStack, x, y, width, height, 12.0f, 1.0f, ColorHelpers.rgba(190, 190, 190, 15.299999999999999));
        VisualHelpers.drawRoundedRect(matrixStack, x + width - 32.0f, y, 26.0f, 2.0f, new Vector4f(0.0f, 2.0f, 2.0f, 0.0f), ColorHelpers.getThemeColor(1));
        StencilHelpers.init();
        VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, -1);
        StencilHelpers.read(1);
        if (this.design.getSelected("Standard")) {
            VisualHelpers.drawGlow(matrixStack, x, y, width, 192.0f, 40.0f, glow);
        }
        for (Element<Option<?>> component : this.components.get(this.draggableOption)) {
            component.getShow().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, Component.mc.getTimer().renderPartialTicks);
            if (!((double)component.getShow().getAnimationValue() > 0.1)) continue;
            component.render(matrixStack, mouseX, mouseY, partialsTicks);
        }
        StencilHelpers.uninit();
        matrixStack.pop();
        GLHelpers.INSTANCE.rescaleMC();
    }

    public void updateSettings() {
        for (Element<Option<?>> component : this.components.get(this.draggableOption)) {
            component.getShow().update(component.getOption().getVisible().getAsBoolean());
            if (!((double)component.getShow().getAnimationValue() > 0.1)) continue;
            component.tick();
        }
    }

    public void clickSettings(double mouseX, double mouseY, int button) {
        for (Element<Option<?>> component : this.components.get(this.draggableOption)) {
            if (!component.getOption().getVisible().getAsBoolean() || !((double)component.getShow().getAnimationValue() > 0.9)) continue;
            component.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void releaseSettings(double mouseX, double mouseY, int button) {
        for (Element<Option<?>> component : this.components.get(this.draggableOption)) {
            if (!component.getOption().getVisible().getAsBoolean() || !((double)component.getShow().getAnimationValue() > 0.9)) continue;
            component.mouseReleased(mouseX, mouseY, button);
        }
    }

    public void translate() {
        for (Element<Option<?>> component : this.components.get(this.draggableOption)) {
            component.translate();
        }
    }

    @Override
    public JsonObject save() {
        JsonObject object = new JsonObject();
        JsonObject optionsObject = new JsonObject();
        for (Option<?> option : this.draggableOption.getOptions()) {
            if (option instanceof CheckboxOption) {
                CheckboxOption checkboxOption = (CheckboxOption)option;
                optionsObject.addProperty(checkboxOption.getSettingName(), (Boolean)checkboxOption.getValue());
            }
            if (option instanceof SliderOption) {
                SliderOption sliderOption = (SliderOption)option;
                optionsObject.addProperty(sliderOption.getSettingName(), (Number)sliderOption.getValue());
            }
            if (!(option instanceof SelectOption)) continue;
            SelectOption selectOption = (SelectOption)option;
            optionsObject.addProperty(selectOption.getSettingName(), ((SelectOptionValue)selectOption.getValue()).getName());
        }
        object.add("Options", optionsObject);
        return object;
    }

    @Override
    public void load(JsonObject object) {
        if (object != null) {
            for (Option<?> option : this.draggableOption.getOptions()) {
                JsonObject optionsObject = object.getAsJsonObject("Options");
                if (option == null || optionsObject == null || !optionsObject.has(option.getSettingName())) continue;
                if (option instanceof CheckboxOption) {
                    CheckboxOption checkboxOption = (CheckboxOption)option;
                    checkboxOption.setValue(optionsObject.get(checkboxOption.getSettingName()).getAsBoolean());
                }
                if (option instanceof SliderOption) {
                    SliderOption sliderOption = (SliderOption)option;
                    sliderOption.setValue(Float.valueOf(optionsObject.get(sliderOption.getSettingName()).getAsFloat()));
                }
                if (!(option instanceof SelectOption)) continue;
                SelectOption selectOption = (SelectOption)option;
                for (SelectOptionValue selectOptionValue : selectOption.getValues()) {
                    if (!selectOptionValue.getName().equals(optionsObject.get(selectOption.getSettingName()).getAsString())) continue;
                    selectOption.setValue(selectOptionValue);
                }
            }
        }
    }

    @Generated
    public Multimap<DraggableOption, Element<? extends Option<?>>> getComponents() {
        return this.components;
    }

    @Generated
    public DraggableOption getDraggableOption() {
        return this.draggableOption;
    }

    @Generated
    public SelectOption getDesign() {
        return this.design;
    }

    @Generated
    public SliderOption getCompression() {
        return this.compression;
    }

    @Generated
    public Animation getShowAnimation() {
        return this.showAnimation;
    }
}
