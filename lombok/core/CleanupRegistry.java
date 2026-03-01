package lombok.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.core.CleanupTask;

public class CleanupRegistry {
    private final ConcurrentMap<CleanupKey, CleanupTask> tasks = new ConcurrentHashMap<CleanupKey, CleanupTask>();

    public void registerTask(String key, Object target, CleanupTask task) {
        CleanupKey ck = new CleanupKey(key, target);
        this.tasks.putIfAbsent(ck, task);
    }

    public void run() {
        for (CleanupTask task : this.tasks.values()) {
            task.cleanup();
        }
        this.tasks.clear();
    }

    private static final class CleanupKey {
        private final String key;
        private final Object target;

        CleanupKey(String key, Object target) {
            this.key = key;
            this.target = target;
        }

        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other == this) {
                return true;
            }
            if (!(other instanceof CleanupKey)) {
                return false;
            }
            CleanupKey o = (CleanupKey)other;
            if (!this.key.equals(o.key)) {
                return false;
            }
            return this.target == o.target;
        }

        public int hashCode() {
            return 109 * System.identityHashCode(this.target) + this.key.hashCode();
        }
    }
}
