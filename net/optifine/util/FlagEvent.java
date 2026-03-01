package net.optifine.util;

import java.util.HashSet;
import java.util.Set;

public class FlagEvent {
    private static Set<String> setEvents = new HashSet<String>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void set(String name) {
        Set<String> set = setEvents;
        synchronized (set) {
            setEvents.add(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean clear(String name) {
        Set<String> set = setEvents;
        synchronized (set) {
            return setEvents.remove(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isActive(String name) {
        Set<String> set = setEvents;
        synchronized (set) {
            return setEvents.contains(name);
        }
    }

    public static boolean isActiveClear(String name) {
        return FlagEvent.clear(name);
    }
}
