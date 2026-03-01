package dimasik.events.main.player;

import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public final class EventTeleport
extends EventCancellable {
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;

    @Generated
    public double getPosX() {
        return this.posX;
    }

    @Generated
    public double getPosY() {
        return this.posY;
    }

    @Generated
    public double getPosZ() {
        return this.posZ;
    }

    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Generated
    public float getPitch() {
        return this.pitch;
    }

    @Generated
    public void setPosX(double posX) {
        this.posX = posX;
    }

    @Generated
    public void setPosY(double posY) {
        this.posY = posY;
    }

    @Generated
    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Generated
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Generated
    public EventTeleport(double posX, double posY, double posZ, float yaw, float pitch) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Generated
    public EventTeleport() {
    }
}
