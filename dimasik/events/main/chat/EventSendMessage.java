package dimasik.events.main.chat;

import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventSendMessage
extends EventCancellable {
    private final String message;

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public EventSendMessage(String message) {
        this.message = message;
    }
}
