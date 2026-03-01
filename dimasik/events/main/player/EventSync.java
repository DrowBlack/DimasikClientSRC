package dimasik.events.main.player;

import dimasik.events.api.main.Event;
import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventSync
extends EventCancellable
implements Event {
    private float yaw;
    private float pitch;
    private double posX;
    private double posY;
    private double posZ;
    private boolean onGround;
    private boolean sprint;

    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Generated
    public void setPitch(float pitch) {
        this.pitch = pitch;
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
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Generated
    public void setSprint(boolean sprint) {
        this.sprint = sprint;
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
    public boolean isOnGround() {
        return this.onGround;
    }

    @Generated
    public boolean isSprint() {
        return this.sprint;
    }

    @Generated
    public EventSync(float yaw, float pitch, double posX, double posY, double posZ, boolean onGround, boolean sprint) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.onGround = onGround;
        this.sprint = sprint;
    }
}
