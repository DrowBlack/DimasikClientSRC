package dimasik.managers.module.option.main;

import dimasik.managers.module.option.api.Option;
import java.util.function.BooleanSupplier;

public class ColorOption
extends Option<Integer> {
    public ColorOption(String settingName, Integer value) {
        super(settingName, value);
    }

    public ColorOption visible(BooleanSupplier visible) {
        this.setVisible(visible);
        return this;
    }

    public ColorOption description(String settingDescription) {
        this.settingDescription = settingDescription;
        return this;
    }
}
