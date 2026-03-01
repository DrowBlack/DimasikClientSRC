package net.minecraft.tags;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public interface ITagCollection<T> {
    public Map<ResourceLocation, ITag<T>> getIDTagMap();

    @Nullable
    default public ITag<T> get(ResourceLocation resourceLocationIn) {
        return this.getIDTagMap().get(resourceLocationIn);
    }

    public ITag<T> getTagByID(ResourceLocation var1);

    @Nullable
    public ResourceLocation getDirectIdFromTag(ITag<T> var1);

    default public ResourceLocation getValidatedIdFromTag(ITag<T> tag) {
        ResourceLocation resourcelocation = this.getDirectIdFromTag(tag);
        if (resourcelocation == null) {
            throw new IllegalStateException("Unrecognized tag");
        }
        return resourcelocation;
    }

    default public Collection<ResourceLocation> getRegisteredTags() {
        return this.getIDTagMap().keySet();
    }

    default public Collection<ResourceLocation> getOwningTags(T itemIn) {
        ArrayList<ResourceLocation> list = Lists.newArrayList();
        for (Map.Entry<ResourceLocation, ITag<T>> entry : this.getIDTagMap().entrySet()) {
            if (!entry.getValue().contains(itemIn)) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    default public void writeTagCollectionToBuffer(PacketBuffer buffer, DefaultedRegistry<T> defaulted) {
        Map<ResourceLocation, ITag<T>> map = this.getIDTagMap();
        buffer.writeVarInt(map.size());
        for (Map.Entry<ResourceLocation, ITag<T>> entry : map.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            buffer.writeVarInt(entry.getValue().getAllElements().size());
            for (T t : entry.getValue().getAllElements()) {
                buffer.writeVarInt(defaulted.getId(t));
            }
        }
    }

    public static <T> ITagCollection<T> readTagCollectionFromBuffer(PacketBuffer buffer, Registry<T> registry) {
        HashMap<ResourceLocation, ITag<T>> map = Maps.newHashMap();
        int i = buffer.readVarInt();
        for (int j = 0; j < i; ++j) {
            ResourceLocation resourcelocation = buffer.readResourceLocation();
            int k = buffer.readVarInt();
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (int l = 0; l < k; ++l) {
                builder.add(registry.getByValue(buffer.readVarInt()));
            }
            map.put(resourcelocation, ITag.getTagOf(builder.build()));
        }
        return ITagCollection.getTagCollectionFromMap(map);
    }

    public static <T> ITagCollection<T> getEmptyTagCollection() {
        return ITagCollection.getTagCollectionFromMap(ImmutableBiMap.of());
    }

    public static <T> ITagCollection<T> getTagCollectionFromMap(Map<ResourceLocation, ITag<T>> idTagMap) {
        final ImmutableBiMap<ResourceLocation, ITag<T>> bimap = ImmutableBiMap.copyOf(idTagMap);
        return new ITagCollection<T>(){
            private final ITag<T> emptyTag = Tag.getEmptyTag();

            @Override
            public ITag<T> getTagByID(ResourceLocation id) {
                return bimap.getOrDefault(id, this.emptyTag);
            }

            @Override
            @Nullable
            public ResourceLocation getDirectIdFromTag(ITag<T> tag) {
                return tag instanceof ITag.INamedTag ? ((ITag.INamedTag)tag).getName() : (ResourceLocation)bimap.inverse().get(tag);
            }

            @Override
            public Map<ResourceLocation, ITag<T>> getIDTagMap() {
                return bimap;
            }
        };
    }
}
