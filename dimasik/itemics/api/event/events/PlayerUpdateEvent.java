package dimasik.itemics.api.event.events;

import dimasik.itemics.api.event.events.type.EventState;

public final class PlayerUpdateEvent {
    private final EventState state;

    public PlayerUpdateEvent(EventState state) {
        this.state = state;
    }

    public final EventState getState() {
        return this.state;
    }
}
