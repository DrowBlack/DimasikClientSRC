package dimasik.managers.module.option.main;

import dimasik.helpers.animation.Animation;
import dimasik.managers.module.Module;
import dimasik.managers.module.option.api.Option;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class CheckboxOption
extends Option<Boolean> {
    private final Animation animation = new Animation();
    private final Animation bindAnimation = new Animation();
    private int key = -1;
    private Module module;

    public CheckboxOption(String settingName, Boolean value) {
        super(settingName, value);
    }

    public CheckboxOption setModule(Module module) {
        this.module = module;
        return this;
    }

    public CheckboxOption visible(BooleanSupplier visible) {
        this.setVisible(visible);
        return this;
    }

    public CheckboxOption description(String settingDescription) {
        this.settingDescription = settingDescription;
        return this;
    }

    @Generated
    public void setKey(int key) {
        this.key = key;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Animation getBindAnimation() {
        return this.bindAnimation;
    }

    @Generated
    public int getKey() {
        return this.key;
    }

    @Generated
    public Module getModule() {
        return this.module;
    }
}
