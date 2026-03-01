package lombok.core.configuration;

import lombok.core.configuration.ConfigurationKey;

public interface ConfigurationResolver {
    public <T> T resolve(ConfigurationKey<T> var1);
}
