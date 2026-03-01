package dimasik.events.main.movement;

import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;
import net.minecraft.client.Minecraft;

public final class EventStrafe
extends EventCancellable {
    private float forward;
    private float strafe;
    private float friction;
    private float yaw;

    public void setSpeed(double speed, double motionMultiplier) {
        this.setFriction((float)(this.getForward() != 0.0f && this.getStrafe() != 0.0f ? speed * (double)0.98f : speed));
        Minecraft.getInstance().player.setMotionWithMultiplication(motionMultiplier, motionMultiplier);
    }

    public void setSpeed(double speed) {
        this.setFriction((float)(this.getForward() != 0.0f && this.getStrafe() != 0.0f ? speed * (double)0.98f : speed));
        Minecraft.getInstance().player.setMotion(0.0, 0.0, Minecraft.getInstance().player.getMotion().y);
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
    public float getFriction() {
        return this.friction;
    }

    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Generated
    public void setForward(float forward) {
        this.forward = forward;
    }

    @Generated
    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    @Generated
    public void setFriction(float friction) {
        this.friction = friction;
    }

    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Generated
    public EventStrafe(float forward, float strafe, float friction, float yaw) {
        this.forward = forward;
        this.strafe = strafe;
        this.friction = friction;
        this.yaw = yaw;
    }
}
