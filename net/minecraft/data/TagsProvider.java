package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TagsProvider<T>
implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final Registry<T> registry;
    private final Map<ResourceLocation, ITag.Builder> tagToBuilder = Maps.newLinkedHashMap();

    protected TagsProvider(DataGenerator generatorIn, Registry<T> registryIn) {
        this.generator = generatorIn;
        this.registry = registryIn;
    }

    protected abstract void registerTags();

    @Override
    public void act(DirectoryCache cache) {
        this.tagToBuilder.clear();
        this.registerTags();
        Tag itag = Tag.getEmptyTag();
        Function<ResourceLocation, ITag> function = key -> this.tagToBuilder.containsKey(key) ? itag : null;
        Function<ResourceLocation, Object> function1 = key -> this.registry.getOptional((ResourceLocation)key).orElse(null);
        this.tagToBuilder.forEach((tagName, builder) -> {
            List list = builder.getProxyTags(function, function1).collect(Collectors.toList());
            if (!list.isEmpty()) {
                throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", tagName, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
            }
            JsonObject jsonobject = builder.serialize();
            Path path = this.makePath((ResourceLocation)tagName);
            try {
                String s = GSON.toJson(jsonobject);
                String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
                if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path, new LinkOption[0])) {
                    Files.createDirectories(path.getParent(), new FileAttribute[0]);
                    try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                        bufferedwriter.write(s);
                    }
                }
                cache.recordHash(path, s1);
            }
            catch (IOException ioexception) {
                LOGGER.error("Couldn't save tags to {}", (Object)path, (Object)ioexception);
            }
        });
    }

    protected abstract Path makePath(ResourceLocation var1);

    protected Builder<T> getOrCreateBuilder(ITag.INamedTag<T> tag) {
        ITag.Builder itag$builder = this.createBuilderIfAbsent(tag);
        return new Builder<T>(itag$builder, this.registry, "vanilla");
    }

    protected ITag.Builder createBuilderIfAbsent(ITag.INamedTag<T> tag) {
        return this.tagToBuilder.computeIfAbsent(tag.getName(), key -> new ITag.Builder());
    }

    public static class Builder<T> {
        private final ITag.Builder builder;
        private final Registry<T> registry;
        private final String id;

        private Builder(ITag.Builder builder, Registry<T> registry, String id) {
            this.builder = builder;
            this.registry = registry;
            this.id = id;
        }

        public Builder<T> addItemEntry(T item) {
            this.builder.addItemEntry(this.registry.getKey(item), this.id);
            return this;
        }

        public Builder<T> addTag(ITag.INamedTag<T> tag) {
            this.builder.addTagEntry(tag.getName(), this.id);
            return this;
        }

        @SafeVarargs
        public final Builder<T> add(T ... toAdd) {
            Stream.of(toAdd).map(this.registry::getKey).forEach(key -> this.builder.addItemEntry((ResourceLocation)key, this.id));
            return this;
        }
    }
}
