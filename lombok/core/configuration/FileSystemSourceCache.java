package lombok.core.configuration;

import java.io.File;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationFileToSource;
import lombok.core.configuration.ConfigurationParser;
import lombok.core.configuration.ConfigurationSource;
import lombok.core.configuration.SingleConfigurationSource;
import lombok.core.debug.ProblemReporter;

public class FileSystemSourceCache {
    private static final long FULL_CACHE_CLEAR_INTERVAL = TimeUnit.MINUTES.toMillis(30L);
    private static final long RECHECK_FILESYSTEM = TimeUnit.SECONDS.toMillis(2L);
    private static final long NEVER_CHECKED = -1L;
    static final long MISSING = -88L;
    private final ConcurrentMap<ConfigurationFile, Content> fileCache = new ConcurrentHashMap<ConfigurationFile, Content>();
    private final ConcurrentMap<URI, ConfigurationFile> uriCache = new ConcurrentHashMap<URI, ConfigurationFile>();
    private volatile long lastCacheClear = System.currentTimeMillis();

    private void cacheClear() {
        long now = System.currentTimeMillis();
        long delta = now - this.lastCacheClear;
        if (delta > FULL_CACHE_CLEAR_INTERVAL) {
            this.lastCacheClear = now;
            this.fileCache.clear();
            this.uriCache.clear();
        }
    }

    public ConfigurationFileToSource fileToSource(final ConfigurationParser parser) {
        return new ConfigurationFileToSource(){

            @Override
            public ConfigurationSource parsed(ConfigurationFile fileLocation) {
                return FileSystemSourceCache.this.parseIfNeccesary(fileLocation, parser);
            }
        };
    }

    public ConfigurationFile forUri(URI javaFile) {
        if (javaFile == null) {
            return null;
        }
        this.cacheClear();
        ConfigurationFile result = (ConfigurationFile)this.uriCache.get(javaFile);
        if (result == null) {
            URI uri = javaFile.normalize();
            if (!uri.isAbsolute()) {
                uri = URI.create("file:" + uri.toString());
            }
            try {
                File directory;
                File file = new File(uri);
                if (!file.exists()) {
                    throw new IllegalArgumentException("File does not exist: " + uri);
                }
                File file2 = directory = file.isDirectory() ? file : file.getParentFile();
                if (directory != null) {
                    result = ConfigurationFile.forDirectory(directory);
                }
                this.uriCache.put(javaFile, result);
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (Exception e) {
                ProblemReporter.error("Can't find absolute path of file being compiled: " + javaFile, e);
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ConfigurationSource parseIfNeccesary(ConfigurationFile file, ConfigurationParser parser) {
        Content content;
        long now = System.currentTimeMillis();
        Content content2 = content = this.ensureContent(file);
        synchronized (content2) {
            if (content.lastChecked != -1L && now - content.lastChecked < RECHECK_FILESYSTEM) {
                return content.source;
            }
            content.lastChecked = now;
            long previouslyModified = content.lastModified;
            content.lastModified = file.getLastModifiedOrMissing();
            if (content.lastModified != previouslyModified) {
                content.source = content.lastModified == -88L ? null : SingleConfigurationSource.parse(file, parser);
            }
            return content.source;
        }
    }

    private Content ensureContent(ConfigurationFile context) {
        Content content = (Content)this.fileCache.get(context);
        if (content != null) {
            return content;
        }
        this.fileCache.putIfAbsent(context, Content.empty());
        return (Content)this.fileCache.get(context);
    }

    private static class Content {
        ConfigurationSource source;
        long lastModified;
        long lastChecked;

        private Content(ConfigurationSource source, long lastModified, long lastChecked) {
            this.source = source;
            this.lastModified = lastModified;
            this.lastChecked = lastChecked;
        }

        static Content empty() {
            return new Content(null, -88L, -1L);
        }
    }
}
