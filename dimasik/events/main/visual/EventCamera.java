package dimasik.events.main.visual;

import dimasik.events.api.main.Event;
import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventCamera
extends EventCancellable
implements Event {
    public float yaw;
    public float pitch;
    public float partialTicks;

    public EventCamera() {
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
    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
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
    public float getPartialTicks() {
        return this.partialTicks;
    }

    @Generated
    public EventCamera(float yaw, float pitch, float partialTicks) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.partialTicks = partialTicks;
    }
}
