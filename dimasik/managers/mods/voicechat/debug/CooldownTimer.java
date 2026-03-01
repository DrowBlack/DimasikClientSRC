package dimasik.managers.mods.voicechat.debug;

import java.util.concurrent.ConcurrentHashMap;

public class CooldownTimer {
    private static ConcurrentHashMap<String, Long> cooldowns = new ConcurrentHashMap();

    public static void run(String id, long time, Runnable runnable) {
        if (System.currentTimeMillis() - cooldowns.getOrDefault(id, 0L) > time) {
            cooldowns.put(id, System.currentTimeMillis());
            runnable.run();
        }
    }

    public static void run(String id, Runnable runnable) {
        CooldownTimer.run(id, 10000L, runnable);
    }
}
