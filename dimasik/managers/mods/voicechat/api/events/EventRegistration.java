package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.events.Event;
import java.util.function.Consumer;

public interface EventRegistration {
    public <T extends Event> void registerEvent(Class<T> var1, Consumer<T> var2, int var3);

    default public <T extends Event> void registerEvent(Class<T> eventClass, Consumer<T> onPacket) {
        this.registerEvent(eventClass, onPacket, 0);
    }
}
