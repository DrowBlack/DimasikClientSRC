package dimasik.managers.module.option.main;

import dimasik.helpers.animation.Animation;
import lombok.Generated;

public class MultiOptionValue {
    private final Animation animation = new Animation();
    private final Animation anim = new Animation();
    private final Animation bindAnimation = new Animation();
    private boolean bind;
    private int key = -1;
    private float nameWidth = 0.0f;
    private final String name;
    private String visualName;
    private String bindName;
    private boolean toggle;

    public MultiOptionValue(String name, boolean toggle) {
        this.name = name;
        this.toggle = toggle;
        this.visualName = name;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Animation getAnim() {
        return this.anim;
    }

    @Generated
    public Animation getBindAnimation() {
        return this.bindAnimation;
    }

    @Generated
    public boolean isBind() {
        return this.bind;
    }

    @Generated
    public int getKey() {
        return this.key;
    }

    @Generated
    public float getNameWidth() {
        return this.nameWidth;
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
    public String getBindName() {
        return this.bindName;
    }

    @Generated
    public boolean isToggle() {
        return this.toggle;
    }

    @Generated
    public void setBind(boolean bind) {
        this.bind = bind;
    }

    @Generated
    public void setKey(int key) {
        this.key = key;
    }

    @Generated
    public void setNameWidth(float nameWidth) {
        this.nameWidth = nameWidth;
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
    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }
}
