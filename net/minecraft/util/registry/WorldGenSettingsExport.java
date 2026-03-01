package net.minecraft.util.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DelegatingDynamicOps;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public class WorldGenSettingsExport<T>
extends DelegatingDynamicOps<T> {
    private final DynamicRegistries dynamicRegistries;

    public static <T> WorldGenSettingsExport<T> create(DynamicOps<T> ops, DynamicRegistries dynamicRegistries) {
        return new WorldGenSettingsExport<T>(ops, dynamicRegistries);
    }

    private WorldGenSettingsExport(DynamicOps<T> ops, DynamicRegistries dynamicRegistries) {
        super(ops);
        this.dynamicRegistries = dynamicRegistries;
    }

    protected <E> DataResult<T> encode(E instance, T prefix, RegistryKey<? extends Registry<E>> registryKey, Codec<E> mapCodec) {
        MutableRegistry mutableregistry;
        Optional<RegistryKey<E>> optional1;
        Optional optional = this.dynamicRegistries.func_230521_a_(registryKey);
        if (optional.isPresent() && (optional1 = (mutableregistry = optional.get()).getOptionalKey(instance)).isPresent()) {
            RegistryKey<E> registrykey = optional1.get();
            return ResourceLocation.CODEC.encode(registrykey.getLocation(), this.ops, prefix);
        }
        return mapCodec.encode(instance, this, prefix);
    }
}
