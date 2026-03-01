package dimasik.managers.module.option.main;

import dimasik.managers.module.option.api.Option;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.utils.math.MathUtils;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class SelectOption
extends Option<SelectOptionValue> {
    private final SelectOptionValue[] values;

    public SelectOption(String settingName, int selected, SelectOptionValue ... values) {
        super(settingName, values[MathUtils.clamp(selected, 0, values.length - 1)]);
        this.values = values;
    }

    public boolean getSelected(String s) {
        return ((SelectOptionValue)this.getValue()).getName().equals(s);
    }

    public boolean getIndex(int index) {
        return this.getValue() == this.getValues()[index];
    }

    public SelectOption visible(BooleanSupplier visible) {
        this.setVisible(visible);
        return this;
    }

    public SelectOption description(String settingDescription) {
        this.settingDescription = settingDescription;
        return this;
    }

    @Generated
    public SelectOptionValue[] getValues() {
        return this.values;
    }
}
