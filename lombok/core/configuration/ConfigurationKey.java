package lombok.core.configuration;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import lombok.core.configuration.ConfigurationDataType;

public abstract class ConfigurationKey<T> {
    private static final Pattern VALID_NAMES = Pattern.compile("[-_a-zA-Z][-.\\w]*(?<![-.])");
    private static final TreeMap<String, ConfigurationKey<?>> registeredKeys = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private static Map<String, ConfigurationKey<?>> copy;
    private final String keyName;
    private final String description;
    private final ConfigurationDataType type;
    private final boolean hidden;

    public ConfigurationKey(String keyName, String description) {
        this(keyName, description, false);
    }

    public ConfigurationKey(String keyName, String description, boolean hidden) {
        ConfigurationDataType type;
        this.keyName = ConfigurationKey.checkName(keyName);
        this.type = type = ConfigurationDataType.toDataType(this.getClass());
        this.description = description;
        this.hidden = hidden;
        ConfigurationKey.registerKey(keyName, this);
    }

    public final String getKeyName() {
        return this.keyName;
    }

    public final String getDescription() {
        return this.description;
    }

    public final ConfigurationDataType getType() {
        return this.type;
    }

    public final boolean isHidden() {
        return this.hidden;
    }

    public String toString() {
        return String.valueOf(this.keyName) + " (" + this.type + "): " + this.description;
    }

    private static String checkName(String keyName) {
        if (keyName == null) {
            throw new NullPointerException("keyName");
        }
        if (!VALID_NAMES.matcher(keyName).matches()) {
            throw new IllegalArgumentException("Invalid keyName: " + keyName);
        }
        return keyName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map<String, ConfigurationKey<?>> registeredKeys() {
        TreeMap<String, ConfigurationKey<?>> treeMap = registeredKeys;
        synchronized (treeMap) {
            if (copy == null) {
                copy = Collections.unmodifiableMap((Map)registeredKeys.clone());
            }
            return copy;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void registerKey(String keyName, ConfigurationKey<?> key) {
        TreeMap<String, ConfigurationKey<?>> treeMap = registeredKeys;
        synchronized (treeMap) {
            if (registeredKeys.containsKey(keyName)) {
                throw new IllegalArgumentException("Key '" + keyName + "' already registered");
            }
            registeredKeys.put(keyName, key);
            copy = null;
        }
    }
}
