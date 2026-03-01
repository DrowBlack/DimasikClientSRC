package dimasik.managers.module.option.main;

import dimasik.managers.module.option.api.Option;
import java.util.function.BooleanSupplier;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;

public class SliderOption
extends Option<Float> {
    private final float min;
    private final float max;
    private float increment = 1.0f;

    public SliderOption(String settingName, float value, float min, float max) {
        super(settingName, Float.valueOf(value));
        this.min = min;
        this.max = max;
    }

    public SliderOption(String settingName, float value, float max) {
        super(settingName, Float.valueOf(value));
        this.min = 0.0f;
        this.max = max;
    }

    public SliderOption increment(float increment) {
        this.increment = increment;
        return this;
    }

    @Override
    public void setValue(Float value) {
        super.setValue(Float.valueOf(MathHelper.clamp(value.floatValue(), this.min, this.max)));
    }

    public SliderOption visible(BooleanSupplier visible) {
        this.setVisible(visible);
        return this;
    }

    public SliderOption description(String settingDescription) {
        this.settingDescription = settingDescription;
        return this;
    }

    @Generated
    public float getMin() {
        return this.min;
    }

    @Generated
    public float getMax() {
        return this.max;
    }

    @Generated
    public float getIncrement() {
        return this.increment;
    }
}
