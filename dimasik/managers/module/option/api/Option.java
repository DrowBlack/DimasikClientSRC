package dimasik.managers.module.option.api;

import java.util.function.BooleanSupplier;
import lombok.Generated;

public abstract class Option<T> {
    private final String settingName;
    private String visualName;
    private String bindName;
    protected String settingDescription;
    private BooleanSupplier visible;
    private T value;

    public Option(String settingName, T value) {
        this.settingName = settingName;
        this.visualName = settingName;
        this.settingDescription = "Setting haven't description";
        this.value = value;
        this.visible = () -> true;
    }

    public abstract Option<T> visible(BooleanSupplier var1);

    public abstract Option<T> description(String var1);

    @Generated
    public String getSettingName() {
        return this.settingName;
    }

    @Generated
    public String getVisualName() {
        return this.visualName;
    }

    @Generated
    public String getBindName() {
        return this.bindName;
    }

    @Generated
    public String getSettingDescription() {
        return this.settingDescription;
    }

    @Generated
    public BooleanSupplier getVisible() {
        return this.visible;
    }

    @Generated
    public T getValue() {
        return this.value;
    }

    @Generated
    public void setVisualName(String visualName) {
        this.visualName = visualName;
    }

    @Generated
    public void setBindName(String bindName) {
        this.bindName = bindName;
    }

    @Generated
    public void setVisible(BooleanSupplier visible) {
        this.visible = visible;
    }

    @Generated
    public void setValue(T value) {
        this.value = value;
    }
}
