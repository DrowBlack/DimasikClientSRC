package net.minecraft.util.registry;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenSettingsExport;
import net.minecraft.util.registry.WorldSettingsImport;

public final class RegistryKeyCodec<E>
implements Codec<Supplier<E>> {
    private final RegistryKey<? extends Registry<E>> registryKey;
    private final Codec<E> registryCodec;
    private final boolean allowInlineDefinitions;

    public static <E> RegistryKeyCodec<E> create(RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec) {
        return RegistryKeyCodec.create(registryKey, codec, true);
    }

    public static <E> Codec<List<Supplier<E>>> getValueCodecs(RegistryKey<? extends Registry<E>> registryKey, Codec<E> registryKeyCodec) {
        return Codec.either(RegistryKeyCodec.create(registryKey, registryKeyCodec, false).listOf(), registryKeyCodec.xmap(value -> () -> value, Supplier::get).listOf()).xmap(either -> either.map(left -> left, right -> right), Either::left);
    }

    private static <E> RegistryKeyCodec<E> create(RegistryKey<? extends Registry<E>> registryKey, Codec<E> registryKeyCodec, boolean allowInlineDefinitions) {
        return new RegistryKeyCodec<E>(registryKey, registryKeyCodec, allowInlineDefinitions);
    }

    private RegistryKeyCodec(RegistryKey<? extends Registry<E>> registryKey, Codec<E> registryKeyCodec, boolean allowInlineDefinitions) {
        this.registryKey = registryKey;
        this.registryCodec = registryKeyCodec;
        this.allowInlineDefinitions = allowInlineDefinitions;
    }

    @Override
    public <T> DataResult<T> encode(Supplier<E> p_encode_1_, DynamicOps<T> p_encode_2_, T p_encode_3_) {
        return p_encode_2_ instanceof WorldGenSettingsExport ? ((WorldGenSettingsExport)p_encode_2_).encode(p_encode_1_.get(), p_encode_3_, this.registryKey, this.registryCodec) : this.registryCodec.encode(p_encode_1_.get(), p_encode_2_, p_encode_3_);
    }

    @Override
    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> p_decode_1_, T p_decode_2_) {
        return p_decode_1_ instanceof WorldSettingsImport ? ((WorldSettingsImport)p_decode_1_).decode(p_decode_2_, this.registryKey, this.registryCodec, this.allowInlineDefinitions) : this.registryCodec.decode(p_decode_1_, p_decode_2_).map((? super R elementPair) -> elementPair.mapFirst(element -> () -> element));
    }

    public String toString() {
        return "RegistryFileCodec[" + String.valueOf(this.registryKey) + " " + String.valueOf(this.registryCodec) + "]";
    }
}
