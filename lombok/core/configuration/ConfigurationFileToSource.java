package lombok.core.configuration;

import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationSource;

public interface ConfigurationFileToSource {
    public ConfigurationSource parsed(ConfigurationFile var1);
}
