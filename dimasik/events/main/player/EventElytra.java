package dimasik.events.main.player;

import dimasik.events.api.main.Event;
import lombok.Generated;

public class EventElytra
implements Event {
    private float pitch;
    private float yaw;
    private float speed;
    private float ySpeed;
    private float visualPitch;

    public EventElytra(float visualPitch) {
        this.visualPitch = visualPitch;
    }

    public EventElytra(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public EventElytra(float pitch, float yaw, float speed, float ySpeed) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.speed = speed;
        this.ySpeed = ySpeed;
    }

    @Generated
    public float getPitch() {
        return this.pitch;
    }

    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Generated
    public float getSpeed() {
        return this.speed;
    }

    @Generated
    public float getYSpeed() {
        return this.ySpeed;
    }

    @Generated
    public float getVisualPitch() {
        return this.visualPitch;
    }

    @Generated
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Generated
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Generated
    public void setYSpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    @Generated
    public void setVisualPitch(float visualPitch) {
        this.visualPitch = visualPitch;
    }
}
