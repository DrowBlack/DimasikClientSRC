package dimasik.managers.mods.voicechat.api.config;

import javax.annotation.Nullable;

public interface ConfigAccessor {
    public boolean hasKey(String var1);

    @Nullable
    public String getValue(String var1);

    default public String getString(String key, String def) {
        String value = this.getValue(key);
        if (value == null) {
            return def;
        }
        return value;
    }

    default public boolean getBoolean(String key, boolean def) {
        String value = this.getValue(key);
        if (value == null) {
            return def;
        }
        return Boolean.parseBoolean(value);
    }

    default public int getInt(String key, int def) {
        String value = this.getValue(key);
        if (value == null) {
            return def;
        }
        return Integer.parseInt(value);
    }

    default public double getDouble(String key, double def) {
        String value = this.getValue(key);
        if (value == null) {
            return def;
        }
        return Double.parseDouble(value);
    }
}
