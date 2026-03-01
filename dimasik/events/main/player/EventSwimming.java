package dimasik.events.main.player;

import dimasik.events.api.main.Event;
import lombok.Generated;

public class EventSwimming
implements Event {
    private float pitch;
    private float yaw;

    @Generated
    public float getPitch() {
        return this.pitch;
    }

    @Generated
    public float getYaw() {
        return this.yaw;
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
    public EventSwimming(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
