package dimasik.itemics.api.event.events.type;

import dimasik.itemics.api.event.events.type.ICancellable;

public class Cancellable
implements ICancellable {
    private boolean cancelled;

    @Override
    public final void cancel() {
        this.cancelled = true;
    }

    @Override
    public final boolean isCancelled() {
        return this.cancelled;
    }
}
