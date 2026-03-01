package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;

public final class EitherCodec<F, S>
implements Codec<Either<F, S>> {
    private final Codec<F> first;
    private final Codec<S> second;

    public EitherCodec(Codec<F> first, Codec<S> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<Pair<Either<F, S>, T>> firstRead = this.first.decode(ops, input).map((? super R vo) -> vo.mapFirst(Either::left));
        if (firstRead.result().isPresent()) {
            return firstRead;
        }
        return this.second.decode(ops, input).map((? super R vo) -> vo.mapFirst(Either::right));
    }

    @Override
    public <T> DataResult<T> encode(Either<F, S> input, DynamicOps<T> ops, T prefix) {
        return input.map(value1 -> this.first.encode(value1, ops, prefix), value2 -> this.second.encode(value2, ops, prefix));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EitherCodec eitherCodec = (EitherCodec)o;
        return Objects.equals(this.first, eitherCodec.first) && Objects.equals(this.second, eitherCodec.second);
    }

    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    public String toString() {
        return "EitherCodec[" + this.first + ", " + this.second + ']';
    }
}
