package dimasik.managers.module.option.main;

import dimasik.helpers.animation.Animation;
import java.util.List;
import lombok.Generated;

public class SelectOptionValue {
    private final Animation animation = new Animation();
    private final Animation openAnimation = new Animation();
    public List<SelectOptionValue> options;
    public int index;
    private final String name;
    private String visualName;

    public SelectOptionValue(String name) {
        this.name = name;
        this.visualName = name;
    }

    public boolean getValue() {
        if (this.index >= 0 && this.index < this.options.size()) {
            return this.options.get(this.index).getValue();
        }
        return false;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Animation getOpenAnimation() {
        return this.openAnimation;
    }

    @Generated
    public List<SelectOptionValue> getOptions() {
        return this.options;
    }

    @Generated
    public int getIndex() {
        return this.index;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getVisualName() {
        return this.visualName;
    }

    @Generated
    public void setVisualName(String visualName) {
        this.visualName = visualName;
    }
}
