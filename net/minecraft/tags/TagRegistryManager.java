package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagRegistry;
import net.minecraft.util.ResourceLocation;

public class TagRegistryManager {
    private static final Map<ResourceLocation, TagRegistry<?>> idToRegistryMap = Maps.newHashMap();

    public static <T> TagRegistry<T> create(ResourceLocation id, Function<ITagCollectionSupplier, ITagCollection<T>> supplierToCollectionFunction) {
        TagRegistry<T> tagregistry = new TagRegistry<T>(supplierToCollectionFunction);
        TagRegistry<T> tagregistry1 = idToRegistryMap.putIfAbsent(id, tagregistry);
        if (tagregistry1 != null) {
            throw new IllegalStateException("Duplicate entry for static tag collection: " + String.valueOf(id));
        }
        return tagregistry;
    }

    public static void fetchTags(ITagCollectionSupplier supplier) {
        idToRegistryMap.values().forEach(registry -> registry.fetchTags(supplier));
    }

    public static void fetchTags() {
        idToRegistryMap.values().forEach(TagRegistry::fetchTags);
    }

    public static Multimap<ResourceLocation, ResourceLocation> validateTags(ITagCollectionSupplier supplier) {
        HashMultimap<ResourceLocation, ResourceLocation> multimap = HashMultimap.create();
        idToRegistryMap.forEach((id, registry) -> multimap.putAll((ResourceLocation)id, (Iterable<ResourceLocation>)registry.getTagIdsFromSupplier(supplier)));
        return multimap;
    }

    public static void checkHelperRegistrations() {
        TagRegistry[] atagregistry = new TagRegistry[]{BlockTags.collection, ItemTags.collection, FluidTags.collection, EntityTypeTags.tagCollection};
        boolean flag = Stream.of(atagregistry).anyMatch(registry -> !idToRegistryMap.containsValue(registry));
        if (flag) {
            throw new IllegalStateException("Missing helper registrations");
        }
    }
}
