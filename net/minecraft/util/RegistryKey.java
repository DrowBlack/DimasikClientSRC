package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class RegistryKey<T> {
    private static final Map<String, RegistryKey<?>> UNIVERSAL_KEY_MAP = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private final ResourceLocation parent;
    private final ResourceLocation location;

    public static <T> RegistryKey<T> getOrCreateKey(RegistryKey<? extends Registry<T>> parent, ResourceLocation location) {
        return RegistryKey.getOrCreateKey(parent.location, location);
    }

    public static <T> RegistryKey<Registry<T>> getOrCreateRootKey(ResourceLocation location) {
        return RegistryKey.getOrCreateKey(Registry.ROOT, location);
    }

    private static <T> RegistryKey<T> getOrCreateKey(ResourceLocation parent, ResourceLocation location) {
        String s = (String.valueOf(parent) + ":" + String.valueOf(location)).intern();
        return UNIVERSAL_KEY_MAP.computeIfAbsent(s, concatKey -> new RegistryKey(parent, location));
    }

    private RegistryKey(ResourceLocation parent, ResourceLocation location) {
        this.parent = parent;
        this.location = location;
    }

    public String toString() {
        return "ResourceKey[" + String.valueOf(this.parent) + " / " + String.valueOf(this.location) + "]";
    }

    public boolean isParent(RegistryKey<? extends Registry<?>> key) {
        return this.parent.equals(key.getLocation());
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public static <T> Function<ResourceLocation, RegistryKey<T>> getKeyCreator(RegistryKey<? extends Registry<T>> parent) {
        return location -> RegistryKey.getOrCreateKey(parent, location);
    }
}
