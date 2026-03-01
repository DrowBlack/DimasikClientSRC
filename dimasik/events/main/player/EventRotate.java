package dimasik.events.main.player;

import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventRotate
extends EventCancellable {
    private double yaw;
    private double pitch;

    @Generated
    public double getYaw() {
        return this.yaw;
    }

    @Generated
    public double getPitch() {
        return this.pitch;
    }

    @Generated
    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    @Generated
    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventRotate)) {
            return false;
        }
        EventRotate other = (EventRotate)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Double.compare(this.getYaw(), other.getYaw()) != 0) {
            return false;
        }
        return Double.compare(this.getPitch(), other.getPitch()) == 0;
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof EventRotate;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $yaw = Double.doubleToLongBits(this.getYaw());
        result = result * 59 + (int)($yaw >>> 32 ^ $yaw);
        long $pitch = Double.doubleToLongBits(this.getPitch());
        result = result * 59 + (int)($pitch >>> 32 ^ $pitch);
        return result;
    }

    @Generated
    public String toString() {
        return "EventRotate(yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ")";
    }

    @Generated
    public EventRotate(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
