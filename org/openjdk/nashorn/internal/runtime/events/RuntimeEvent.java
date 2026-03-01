package org.openjdk.nashorn.internal.runtime.events;

import java.util.logging.Level;
import org.openjdk.nashorn.internal.runtime.options.Options;

public class RuntimeEvent<T> {
    public static final int RUNTIME_EVENT_QUEUE_SIZE = Options.getIntProperty("nashorn.runtime.event.queue.size", 1024);
    private final Level level;
    private final T value;

    public RuntimeEvent(Level level, T object) {
        this.level = level;
        this.value = object;
    }

    public final T getValue() {
        return this.value;
    }

    public String toString() {
        return "[" + this.level + "] " + (this.value == null ? "null" : this.getValueClass().getSimpleName()) + " value=" + this.value;
    }

    public final Class<?> getValueClass() {
        return this.value.getClass();
    }
}
