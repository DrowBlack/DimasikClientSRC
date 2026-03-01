package net.minecraft.util.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.WorldSettingsImport;

public final class SimpleRegistryCodec<E>
implements Codec<SimpleRegistry<E>> {
    private final Codec<SimpleRegistry<E>> registryCodec;
    private final RegistryKey<? extends Registry<E>> registryKey;
    private final Codec<E> rawCodec;

    public static <E> SimpleRegistryCodec<E> create(RegistryKey<? extends Registry<E>> registryKey, Lifecycle lifecycle, Codec<E> rawCodec) {
        return new SimpleRegistryCodec<E>(registryKey, lifecycle, rawCodec);
    }

    private SimpleRegistryCodec(RegistryKey<? extends Registry<E>> registryKey, Lifecycle lifecycle, Codec<E> rawCodec) {
        this.registryCodec = SimpleRegistry.getUnboundedRegistryCodec(registryKey, lifecycle, rawCodec);
        this.registryKey = registryKey;
        this.rawCodec = rawCodec;
    }

    @Override
    public <T> DataResult<T> encode(SimpleRegistry<E> p_encode_1_, DynamicOps<T> p_encode_2_, T p_encode_3_) {
        return this.registryCodec.encode(p_encode_1_, p_encode_2_, p_encode_3_);
    }

    @Override
    public <T> DataResult<Pair<SimpleRegistry<E>, T>> decode(DynamicOps<T> p_decode_1_, T p_decode_2_) {
        DataResult<Pair<SimpleRegistry<E>, T>> dataresult = this.registryCodec.decode(p_decode_1_, p_decode_2_);
        return p_decode_1_ instanceof WorldSettingsImport ? dataresult.flatMap((? super R registryPair) -> ((WorldSettingsImport)p_decode_1_).decode((SimpleRegistry)registryPair.getFirst(), this.registryKey, this.rawCodec).map((? super R registry) -> Pair.of(registry, registryPair.getSecond()))) : dataresult;
    }

    public String toString() {
        return "RegistryDataPackCodec[" + String.valueOf(this.registryCodec) + " " + String.valueOf(this.registryKey) + " " + String.valueOf(this.rawCodec) + "]";
    }
}
