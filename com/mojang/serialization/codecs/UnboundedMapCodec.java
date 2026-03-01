package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.BaseMapCodec;
import java.util.Map;
import java.util.Objects;

public final class UnboundedMapCodec<K, V>
implements BaseMapCodec<K, V>,
Codec<Map<K, V>> {
    private final Codec<K> keyCodec;
    private final Codec<V> elementCodec;

    public UnboundedMapCodec(Codec<K> keyCodec, Codec<V> elementCodec) {
        this.keyCodec = keyCodec;
        this.elementCodec = elementCodec;
    }

    @Override
    public Codec<K> keyCodec() {
        return this.keyCodec;
    }

    @Override
    public Codec<V> elementCodec() {
        return this.elementCodec;
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap((? super R map) -> this.decode(ops, (Object)map)).map((? super R r) -> Pair.of(r, input));
    }

    @Override
    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        return this.encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UnboundedMapCodec that = (UnboundedMapCodec)o;
        return Objects.equals(this.keyCodec, that.keyCodec) && Objects.equals(this.elementCodec, that.elementCodec);
    }

    public int hashCode() {
        return Objects.hash(this.keyCodec, this.elementCodec);
    }

    public String toString() {
        return "UnboundedMapCodec[" + this.keyCodec + " -> " + this.elementCodec + ']';
    }
}
