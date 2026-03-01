package net.minecraft.util.registry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.stream.Stream;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldSettingsImport;

public final class RegistryLookupCodec<E>
extends MapCodec<Registry<E>> {
    private final RegistryKey<? extends Registry<E>> registryKey;

    public static <E> RegistryLookupCodec<E> getLookUpCodec(RegistryKey<? extends Registry<E>> registryKey) {
        return new RegistryLookupCodec<E>(registryKey);
    }

    private RegistryLookupCodec(RegistryKey<? extends Registry<E>> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public <T> RecordBuilder<T> encode(Registry<E> p_encode_1_, DynamicOps<T> p_encode_2_, RecordBuilder<T> p_encode_3_) {
        return p_encode_3_;
    }

    @Override
    public <T> DataResult<Registry<E>> decode(DynamicOps<T> p_decode_1_, MapLike<T> p_decode_2_) {
        return p_decode_1_ instanceof WorldSettingsImport ? ((WorldSettingsImport)p_decode_1_).getRegistryByKey(this.registryKey) : DataResult.error("Not a registry ops");
    }

    public String toString() {
        return "RegistryLookupCodec[" + String.valueOf(this.registryKey) + "]";
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> p_keys_1_) {
        return Stream.empty();
    }
}
