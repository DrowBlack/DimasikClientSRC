package dimasik.events.main.misc;

import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventPush
extends EventCancellable {
    private final PushType pushType;

    @Generated
    public EventPush(PushType pushType) {
        this.pushType = pushType;
    }

    @Generated
    public PushType getPushType() {
        return this.pushType;
    }

    public static enum PushType {
        Blocks,
        Entities,
        Water;

    }
}
