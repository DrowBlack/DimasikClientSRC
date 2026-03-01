package dimasik.itemics.api.event.events;

import dimasik.itemics.api.event.events.type.Cancellable;

public final class TabCompleteEvent
extends Cancellable {
    public final String prefix;
    public String[] completions;

    public TabCompleteEvent(String prefix) {
        this.prefix = prefix;
        this.completions = null;
    }
}
