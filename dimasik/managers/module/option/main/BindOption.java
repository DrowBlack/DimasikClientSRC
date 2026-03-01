package dimasik.managers.module.option.main;

import dimasik.managers.module.option.api.Option;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class BindOption
extends Option<Integer> {
    private int key;

    public BindOption(String settingName, int key) {
        super(settingName, key);
        this.key = key;
    }

    public BindOption visible(BooleanSupplier visible) {
        this.setVisible(visible);
        return this;
    }

    public BindOption description(String settingDescription) {
        this.settingDescription = settingDescription;
        return this;
    }

    @Generated
    public int getKey() {
        return this.key;
    }

    @Generated
    public void setKey(int key) {
        this.key = key;
    }
}
