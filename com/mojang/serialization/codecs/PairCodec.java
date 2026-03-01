package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;

public final class PairCodec<F, S>
implements Codec<Pair<F, S>> {
    private final Codec<F> first;
    private final Codec<S> second;

    public PairCodec(Codec<F> first, Codec<S> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public <T> DataResult<Pair<Pair<F, S>, T>> decode(DynamicOps<T> ops, T input) {
        return this.first.decode(ops, input).flatMap((? super R p1) -> this.second.decode(ops, p1.getSecond()).map((? super R p2) -> Pair.of(Pair.of(p1.getFirst(), p2.getFirst()), p2.getSecond())));
    }

    @Override
    public <T> DataResult<T> encode(Pair<F, S> value, DynamicOps<T> ops, T rest) {
        return this.second.encode(value.getSecond(), ops, rest).flatMap((? super R f) -> this.first.encode(value.getFirst(), ops, f));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PairCodec pairCodec = (PairCodec)o;
        return Objects.equals(this.first, pairCodec.first) && Objects.equals(this.second, pairCodec.second);
    }

    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    public String toString() {
        return "PairCodec[" + this.first + ", " + this.second + ']';
    }
}
