package dimasik.helpers.animation;

import dimasik.helpers.animation.Easing;
import dimasik.helpers.animation.GhostAnim;
import lombok.Generated;

public class InfinityAnimation {
    private float output;
    private float endpoint;
    private Easing easing = Easing.LINEAR;
    private GhostAnim animation = new GhostAnim().setSize(0.0f).setSpeed(0).setForward(false).setEasing(this.easing);

    public float animate(float destination, int ms) {
        ms = Math.max(1, ms);
        this.output = this.endpoint - this.animation.get();
        this.endpoint = destination;
        if (this.output != this.endpoint - destination) {
            this.animation = new GhostAnim().setSize(this.endpoint - this.output).setSpeed(ms).setForward(false).setEasing(this.easing);
        }
        return this.output;
    }

    public boolean finished() {
        return this.output == this.endpoint || this.animation.finished() || this.animation.finished(false);
    }

    public float get() {
        this.output = this.endpoint - this.animation.get();
        return this.output;
    }

    public InfinityAnimation easing(Easing easing) {
        this.easing = easing;
        return this;
    }

    @Generated
    public GhostAnim getAnimation() {
        return this.animation;
    }
}
