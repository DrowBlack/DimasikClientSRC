package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public interface ITag<T> {
    public static <T> Codec<ITag<T>> getTagCodec(Supplier<ITagCollection<T>> collectionSupplier) {
        return ResourceLocation.CODEC.flatXmap(tagId -> Optional.ofNullable(((ITagCollection)collectionSupplier.get()).get((ResourceLocation)tagId)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown tag: " + String.valueOf(tagId))), tag -> Optional.ofNullable(((ITagCollection)collectionSupplier.get()).getDirectIdFromTag(tag)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown tag: " + String.valueOf(tag))));
    }

    public boolean contains(T var1);

    public List<T> getAllElements();

    default public T getRandomElement(Random random) {
        List<T> list = this.getAllElements();
        return list.get(random.nextInt(list.size()));
    }

    public static <T> ITag<T> getTagOf(Set<T> elements) {
        return Tag.getTagFromContents(elements);
    }

    public static class TagEntry
    implements ITagEntry {
        private final ResourceLocation id;

        public TagEntry(ResourceLocation resourceLocationIn) {
            this.id = resourceLocationIn;
        }

        @Override
        public <T> boolean matches(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction, Consumer<T> elementConsumer) {
            ITag<T> itag = resourceTagFunction.apply(this.id);
            if (itag == null) {
                return false;
            }
            itag.getAllElements().forEach(elementConsumer);
            return true;
        }

        @Override
        public void addAdditionalData(JsonArray jsonArray) {
            jsonArray.add("#" + String.valueOf(this.id));
        }

        public String toString() {
            return "#" + String.valueOf(this.id);
        }
    }

    public static class Proxy {
        private final ITagEntry entry;
        private final String identifier;

        private Proxy(ITagEntry entry, String identifier) {
            this.entry = entry;
            this.identifier = identifier;
        }

        public ITagEntry getEntry() {
            return this.entry;
        }

        public String toString() {
            return this.entry.toString() + " (from " + this.identifier + ")";
        }
    }

    public static class OptionalTagEntry
    implements ITagEntry {
        private final ResourceLocation id;

        public OptionalTagEntry(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public <T> boolean matches(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction, Consumer<T> elementConsumer) {
            ITag<T> itag = resourceTagFunction.apply(this.id);
            if (itag != null) {
                itag.getAllElements().forEach(elementConsumer);
            }
            return true;
        }

        @Override
        public void addAdditionalData(JsonArray jsonArray) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("id", "#" + String.valueOf(this.id));
            jsonobject.addProperty("required", false);
            jsonArray.add(jsonobject);
        }

        public String toString() {
            return "#" + String.valueOf(this.id) + "?";
        }
    }

    public static class OptionalItemEntry
    implements ITagEntry {
        private final ResourceLocation id;

        public OptionalItemEntry(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public <T> boolean matches(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction, Consumer<T> elementConsumer) {
            T t = resourceElementFunction.apply(this.id);
            if (t != null) {
                elementConsumer.accept(t);
            }
            return true;
        }

        @Override
        public void addAdditionalData(JsonArray jsonArray) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("id", this.id.toString());
            jsonobject.addProperty("required", false);
            jsonArray.add(jsonobject);
        }

        public String toString() {
            return this.id.toString() + "?";
        }
    }

    public static class ItemEntry
    implements ITagEntry {
        private final ResourceLocation identifier;

        public ItemEntry(ResourceLocation identifier) {
            this.identifier = identifier;
        }

        @Override
        public <T> boolean matches(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction, Consumer<T> elementConsumer) {
            T t = resourceElementFunction.apply(this.identifier);
            if (t == null) {
                return false;
            }
            elementConsumer.accept(t);
            return true;
        }

        @Override
        public void addAdditionalData(JsonArray jsonArray) {
            jsonArray.add(this.identifier.toString());
        }

        public String toString() {
            return this.identifier.toString();
        }
    }

    public static interface ITagEntry {
        public <T> boolean matches(Function<ResourceLocation, ITag<T>> var1, Function<ResourceLocation, T> var2, Consumer<T> var3);

        public void addAdditionalData(JsonArray var1);
    }

    public static interface INamedTag<T>
    extends ITag<T> {
        public ResourceLocation getName();
    }

    public static class Builder {
        private final List<Proxy> proxyTags = Lists.newArrayList();

        public static Builder create() {
            return new Builder();
        }

        public Builder addProxyTag(Proxy proxyTag) {
            this.proxyTags.add(proxyTag);
            return this;
        }

        public Builder addTag(ITagEntry tagEntry, String identifier) {
            return this.addProxyTag(new Proxy(tagEntry, identifier));
        }

        public Builder addItemEntry(ResourceLocation registryName, String identifier) {
            return this.addTag(new ItemEntry(registryName), identifier);
        }

        public Builder addTagEntry(ResourceLocation tag, String identifier) {
            return this.addTag(new TagEntry(tag), identifier);
        }

        public <T> Optional<ITag<T>> build(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (Proxy itag$proxy : this.proxyTags) {
                if (itag$proxy.getEntry().matches(resourceTagFunction, resourceElementFunction, builder::add)) continue;
                return Optional.empty();
            }
            return Optional.of(ITag.getTagOf(builder.build()));
        }

        public Stream<Proxy> getProxyStream() {
            return this.proxyTags.stream();
        }

        public <T> Stream<Proxy> getProxyTags(Function<ResourceLocation, ITag<T>> resourceTagFunction, Function<ResourceLocation, T> resourceElementFunction) {
            return this.getProxyStream().filter(tagProxy -> !tagProxy.getEntry().matches(resourceTagFunction, resourceElementFunction, tagType -> {}));
        }

        public Builder deserialize(JsonObject json, String identifier) {
            JsonArray jsonarray = JSONUtils.getJsonArray(json, "values");
            ArrayList<ITagEntry> list = Lists.newArrayList();
            for (JsonElement jsonelement : jsonarray) {
                list.add(Builder.deserializeTagEntry(jsonelement));
            }
            if (JSONUtils.getBoolean(json, "replace", false)) {
                this.proxyTags.clear();
            }
            list.forEach(tagEntry -> this.proxyTags.add(new Proxy((ITagEntry)tagEntry, identifier)));
            return this;
        }

        private static ITagEntry deserializeTagEntry(JsonElement json) {
            boolean flag;
            String s;
            if (json.isJsonObject()) {
                JsonObject jsonobject = json.getAsJsonObject();
                s = JSONUtils.getString(jsonobject, "id");
                flag = JSONUtils.getBoolean(jsonobject, "required", true);
            } else {
                s = JSONUtils.getString(json, "id");
                flag = true;
            }
            if (s.startsWith("#")) {
                ResourceLocation resourcelocation1 = new ResourceLocation(s.substring(1));
                return flag ? new TagEntry(resourcelocation1) : new OptionalTagEntry(resourcelocation1);
            }
            ResourceLocation resourcelocation = new ResourceLocation(s);
            return flag ? new ItemEntry(resourcelocation) : new OptionalItemEntry(resourcelocation);
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            JsonArray jsonarray = new JsonArray();
            for (Proxy itag$proxy : this.proxyTags) {
                itag$proxy.getEntry().addAdditionalData(jsonarray);
            }
            jsonobject.addProperty("replace", false);
            jsonobject.add("values", jsonarray);
            return jsonobject;
        }
    }
}
