package dimasik.events.main.input;

import dimasik.events.api.main.Event;
import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventMoveInput
extends EventCancellable
implements Event {
    private float forward;
    private float strafe;
    private boolean jump;
    private boolean sneak;

    @Generated
    public void setForward(float forward) {
        this.forward = forward;
    }

    @Generated
    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    @Generated
    public void setJump(boolean jump) {
        this.jump = jump;
    }

    @Generated
    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    @Generated
    public float getForward() {
        return this.forward;
    }

    @Generated
    public float getStrafe() {
        return this.strafe;
    }

    @Generated
    public boolean isJump() {
        return this.jump;
    }

    @Generated
    public boolean isSneak() {
        return this.sneak;
    }

    @Generated
    public EventMoveInput(float forward, float strafe, boolean jump, boolean sneak) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.sneak = sneak;
    }
}
