package lombok.core;

import java.net.URI;
import java.util.Collections;
import lombok.core.AST;
import lombok.core.configuration.BubblingConfigurationResolver;
import lombok.core.configuration.ConfigurationFileToSource;
import lombok.core.configuration.ConfigurationKey;
import lombok.core.configuration.ConfigurationParser;
import lombok.core.configuration.ConfigurationProblemReporter;
import lombok.core.configuration.ConfigurationResolver;
import lombok.core.configuration.ConfigurationResolverFactory;
import lombok.core.configuration.FileSystemSourceCache;

public class LombokConfiguration {
    private static final ConfigurationResolver NULL_RESOLVER = new ConfigurationResolver(){

        @Override
        public <T> T resolve(ConfigurationKey<T> key) {
            if (key.getType().isList()) {
                return (T)Collections.emptyList();
            }
            return null;
        }
    };
    private static FileSystemSourceCache cache = new FileSystemSourceCache();
    private static ConfigurationResolverFactory configurationResolverFactory = System.getProperty("lombok.disableConfig") != null ? new ConfigurationResolverFactory(){

        @Override
        public ConfigurationResolver createResolver(URI sourceLocation) {
            return NULL_RESOLVER;
        }
    } : LombokConfiguration.createFileSystemBubblingResolverFactory();

    private LombokConfiguration() {
    }

    public static void overrideConfigurationResolverFactory(ConfigurationResolverFactory crf) {
        configurationResolverFactory = crf == null ? LombokConfiguration.createFileSystemBubblingResolverFactory() : crf;
    }

    static <T> T read(ConfigurationKey<T> key, AST<?, ?, ?> ast) {
        return LombokConfiguration.read(key, ast.getAbsoluteFileLocation());
    }

    public static <T> T read(ConfigurationKey<T> key, URI sourceLocation) {
        return configurationResolverFactory.createResolver(sourceLocation).resolve(key);
    }

    private static ConfigurationResolverFactory createFileSystemBubblingResolverFactory() {
        final ConfigurationFileToSource fileToSource = cache.fileToSource(new ConfigurationParser(ConfigurationProblemReporter.CONSOLE));
        return new ConfigurationResolverFactory(){

            @Override
            public ConfigurationResolver createResolver(URI sourceLocation) {
                return new BubblingConfigurationResolver(cache.forUri(sourceLocation), fileToSource);
            }
        };
    }
}
