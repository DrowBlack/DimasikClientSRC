package dimasik.events.main.render;

import dimasik.events.api.main.Event;
import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventNameRender
extends EventCancellable
implements Event {
    private final Type type;

    @Generated
    public Type getType() {
        return this.type;
    }

    @Generated
    public EventNameRender(Type type) {
        this.type = type;
    }

    public static enum Type {
        PlayerName;

    }
}
