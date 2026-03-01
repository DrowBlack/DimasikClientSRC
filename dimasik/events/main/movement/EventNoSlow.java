package dimasik.events.main.movement;

import dimasik.events.api.main.Event;
import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventNoSlow
extends EventCancellable
implements Event {
    private float speed;

    @Generated
    public float getSpeed() {
        return this.speed;
    }

    @Generated
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Generated
    public EventNoSlow(float speed) {
        this.speed = speed;
    }
}
