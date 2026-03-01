package lombok.core.configuration;

import java.net.URI;
import lombok.core.configuration.ConfigurationResolver;

public interface ConfigurationResolverFactory {
    public ConfigurationResolver createResolver(URI var1);
}
