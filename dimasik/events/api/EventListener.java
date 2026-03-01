package dimasik.events.api;

import dimasik.events.api.main.Event;

public interface EventListener<T extends Event> {
    public void onEvent(T var1);

    default public int getPriority() {
        return 0;
    }
}
