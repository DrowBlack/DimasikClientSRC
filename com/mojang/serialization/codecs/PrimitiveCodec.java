package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public interface PrimitiveCodec<A>
extends Codec<A> {
    public <T> DataResult<A> read(DynamicOps<T> var1, T var2);

    public <T> T write(DynamicOps<T> var1, A var2);

    @Override
    default public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        return this.read(ops, input).map((? super R r) -> Pair.of(r, ops.empty()));
    }

    @Override
    default public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        return ops.mergeToPrimitive(prefix, this.write(ops, input));
    }
}
