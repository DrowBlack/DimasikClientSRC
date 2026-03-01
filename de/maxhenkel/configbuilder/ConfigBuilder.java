package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.ConfigBuilderImpl;
import de.maxhenkel.configbuilder.entry.BooleanConfigEntry;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import de.maxhenkel.configbuilder.entry.DoubleConfigEntry;
import de.maxhenkel.configbuilder.entry.EnumConfigEntry;
import de.maxhenkel.configbuilder.entry.FloatConfigEntry;
import de.maxhenkel.configbuilder.entry.IntegerConfigEntry;
import de.maxhenkel.configbuilder.entry.LongConfigEntry;
import de.maxhenkel.configbuilder.entry.StringConfigEntry;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConfigBuilder {
    public ConfigBuilder header(String ... var1);

    public BooleanConfigEntry booleanEntry(String var1, Boolean var2, String ... var3);

    public IntegerConfigEntry integerEntry(String var1, Integer var2, Integer var3, Integer var4, String ... var5);

    default public IntegerConfigEntry integerEntry(String key, Integer def, String ... comments) {
        return this.integerEntry(key, def, (Integer)null, (Integer)null, comments);
    }

    public LongConfigEntry longEntry(String var1, Long var2, Long var3, Long var4, String ... var5);

    default public LongConfigEntry longEntry(String key, Long def, String ... comments) {
        return this.longEntry(key, def, (Long)null, (Long)null, comments);
    }

    public DoubleConfigEntry doubleEntry(String var1, Double var2, Double var3, Double var4, String ... var5);

    default public DoubleConfigEntry doubleEntry(String key, Double def, String ... comments) {
        return this.doubleEntry(key, def, (Double)null, (Double)null, comments);
    }

    public FloatConfigEntry floatEntry(String var1, Float var2, Float var3, Float var4, String ... var5);

    default public FloatConfigEntry floatEntry(String key, Float def, String ... comments) {
        return this.floatEntry(key, def, (Float)null, (Float)null, comments);
    }

    public StringConfigEntry stringEntry(String var1, String var2, String ... var3);

    public <E extends Enum<E>> EnumConfigEntry<E> enumEntry(String var1, E var2, String ... var3);

    public <T> ConfigEntry<T> entry(String var1, T var2, String ... var3);

    public static <C> Builder<C> builder(@Nonnull Function<ConfigBuilder, C> builderConsumer) {
        return new Builder(builderConsumer);
    }

    public static class Builder<C> {
        @Nonnull
        private final Function<ConfigBuilder, C> builderConsumer;
        @Nullable
        private Path path;
        private final Map<Class<?>, ValueSerializer<?>> valueSerializers;
        private boolean removeUnused;
        private boolean strict;
        private boolean keepOrder;
        private boolean saveAfterBuild;
        private boolean saveSyncAfterBuild;

        private Builder(@Nonnull Function<ConfigBuilder, C> builderConsumer) {
            this.builderConsumer = builderConsumer;
            this.valueSerializers = new HashMap();
            this.removeUnused = true;
            this.strict = false;
            this.keepOrder = true;
            this.saveAfterBuild = true;
            this.saveSyncAfterBuild = false;
        }

        public Builder<C> path(Path path) {
            this.path = path;
            return this;
        }

        public <T> Builder<C> addValueSerializer(Class<T> type, ValueSerializer<T> serializer) {
            this.valueSerializers.put(type, serializer);
            return this;
        }

        public Builder<C> removeUnused(boolean removeUnused) {
            this.removeUnused = removeUnused;
            return this;
        }

        public Builder<C> strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        public Builder<C> keepOrder(boolean keepOrder) {
            this.keepOrder = keepOrder;
            return this;
        }

        public Builder<C> saveAfterBuild(boolean saveAfterBuild) {
            this.saveAfterBuild = saveAfterBuild;
            if (saveAfterBuild) {
                this.saveSyncAfterBuild = false;
            }
            return this;
        }

        public Builder<C> saveSyncAfterBuild(boolean saveSyncAfterBuild) {
            this.saveSyncAfterBuild = saveSyncAfterBuild;
            if (saveSyncAfterBuild) {
                this.saveAfterBuild = false;
            }
            return this;
        }

        public C build() {
            CommentedPropertyConfig cpc = CommentedPropertyConfig.builder().path(this.path).strict(this.strict).build();
            ConfigBuilderImpl builder = new ConfigBuilderImpl(cpc, this.valueSerializers);
            C config = this.builderConsumer.apply(builder);
            builder.freeze();
            if (this.removeUnused) {
                builder.removeUnused();
            }
            if (this.keepOrder) {
                builder.sortEntries();
            }
            if (this.saveAfterBuild) {
                builder.config.save();
            } else if (this.saveSyncAfterBuild) {
                builder.config.saveSync();
            }
            return config;
        }
    }
}
