package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.Config;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

public class CommentedPropertyConfig
implements Config {
    private static final Logger LOGGER = Logger.getLogger(CommentedPropertyConfig.class.getName());
    private static final ExecutorService SAVE_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("ConfigSaver");
        thread.setDaemon(true);
        return thread;
    });
    protected CommentedProperties properties;
    @Nullable
    protected Path path;

    protected CommentedPropertyConfig(CommentedProperties properties) {
        this.properties = properties;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String get(String key) {
        return this.properties.get(key);
    }

    public void set(String key, String value, String ... comments) {
        this.properties.set(key, value, comments);
    }

    public CommentedProperties getProperties() {
        return this.properties;
    }

    public void load() throws IOException {
        if (this.path == null) {
            return;
        }
        if (Files.exists(this.path, new LinkOption[0])) {
            try (InputStream inputStream = Files.newInputStream(this.path, new OpenOption[0]);){
                this.properties.load(inputStream);
            }
        }
    }

    public void reload() {
        this.properties.clear();
        try {
            this.load();
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to reload config", e);
        }
    }

    public synchronized void saveSync() {
        if (this.path == null) {
            return;
        }
        try {
            Files.createDirectories(this.path.getParent(), new FileAttribute[0]);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create parent directories of config", e);
        }
        try (OutputStream stream = Files.newOutputStream(this.path, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.TRUNCATE_EXISTING);){
            this.properties.save(stream);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save config", e);
        }
    }

    public void save() {
        if (this.path == null) {
            return;
        }
        SAVE_EXECUTOR_SERVICE.execute(this::saveSync);
    }

    @Override
    public Map<String, String> getEntries() {
        return Collections.unmodifiableMap(this.properties);
    }

    public static class Builder {
        @Nullable
        private Path path;
        private boolean strict = true;

        private Builder() {
        }

        public Builder path(Path path) {
            this.path = path;
            return this;
        }

        public Builder strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        public CommentedPropertyConfig build() {
            CommentedPropertyConfig config = new CommentedPropertyConfig(new CommentedProperties(this.strict));
            if (this.path != null) {
                config.path = this.path.toAbsolutePath();
            }
            config.reload();
            return config;
        }
    }
}
