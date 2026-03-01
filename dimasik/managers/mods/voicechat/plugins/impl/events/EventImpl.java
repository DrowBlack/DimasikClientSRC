package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.Event;

public class EventImpl
implements Event {
    private boolean cancelled;

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean cancel() {
        if (!this.isCancellable()) {
            return false;
        }
        this.cancelled = true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
