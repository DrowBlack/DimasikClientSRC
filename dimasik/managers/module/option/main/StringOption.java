package dimasik.managers.module.option.main;

import dimasik.managers.module.option.api.Option;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class StringOption
extends Option<String> {
    private Type currentType = Type.Default;

    public StringOption(String settingName, String value) {
        super(settingName, value);
    }

    public StringOption visible(BooleanSupplier visible) {
        this.setVisible(visible);
        return this;
    }

    public StringOption description(String settingDescription) {
        this.settingDescription = settingDescription;
        return this;
    }

    public StringOption setType(Type type) {
        this.currentType = type;
        return this;
    }

    @Generated
    public Type getCurrentType() {
        return this.currentType;
    }

    public static enum Type {
        Default,
        OnlyString,
        OnlyNumber,
        OnlyAllowed;

    }
}
