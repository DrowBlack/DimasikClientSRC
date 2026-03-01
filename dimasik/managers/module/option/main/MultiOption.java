package dimasik.managers.module.option.main;

import dimasik.managers.module.Module;
import dimasik.managers.module.option.api.Option;
import dimasik.managers.module.option.main.MultiOptionValue;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import lombok.Generated;

public class MultiOption
extends Option<MultiOptionValue> {
    private final MultiOptionValue[] values;
    private Module module;

    public MultiOption(String settingName, MultiOptionValue ... values) {
        super(settingName, values[0]);
        this.values = values;
    }

    public boolean getSelected(String s) {
        for (MultiOptionValue value : this.values) {
            if (!value.getName().equals(s)) continue;
            return value.isToggle();
        }
        return false;
    }

    public boolean getIndex(int index) {
        for (MultiOptionValue value : this.values) {
            if (value != this.getValues()[index]) continue;
            return value.isToggle();
        }
        return false;
    }

    public String selected() {
        return Arrays.stream(this.values).filter(MultiOptionValue::isToggle).map(MultiOptionValue::getName).collect(Collectors.joining(", "));
    }

    public MultiOption visible(BooleanSupplier visible) {
        this.setVisible(visible);
        return this;
    }

    public MultiOption description(String settingDescription) {
        this.settingDescription = settingDescription;
        return this;
    }

    public MultiOption setModule(Module module) {
        this.module = module;
        return this;
    }

    @Generated
    public MultiOptionValue[] getValues() {
        return this.values;
    }

    @Generated
    public Module getModule() {
        return this.module;
    }
}
