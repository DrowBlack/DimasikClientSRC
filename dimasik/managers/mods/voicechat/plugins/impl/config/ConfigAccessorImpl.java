package dimasik.managers.mods.voicechat.plugins.impl.config;

import de.maxhenkel.configbuilder.Config;
import dimasik.managers.mods.voicechat.api.config.ConfigAccessor;
import javax.annotation.Nullable;

public class ConfigAccessorImpl
implements ConfigAccessor {
    private Config config;

    public ConfigAccessorImpl(Config config) {
        this.config = config;
    }

    @Override
    public boolean hasKey(String key) {
        return this.config.getEntries().containsKey(key);
    }

    @Override
    @Nullable
    public String getValue(String key) {
        return this.config.getEntries().get(key);
    }
}
