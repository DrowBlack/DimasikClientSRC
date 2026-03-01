package dimasik.events.main.input;

import dimasik.events.api.main.Event;
import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventInput
extends EventCancellable
implements Event {
    private int key;

    @Generated
    public int getKey() {
        return this.key;
    }

    @Generated
    public void setKey(int key) {
        this.key = key;
    }

    @Generated
    public EventInput(int key) {
        this.key = key;
    }
}
