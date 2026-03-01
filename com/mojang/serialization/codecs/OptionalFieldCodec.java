package com.mojang.serialization.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class OptionalFieldCodec<A>
extends MapCodec<Optional<A>> {
    private final String name;
    private final Codec<A> elementCodec;

    public OptionalFieldCodec(String name, Codec<A> elementCodec) {
        this.name = name;
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
        T value = input.get(this.name);
        if (value == null) {
            return DataResult.success(Optional.empty());
        }
        DataResult parsed = this.elementCodec.parse(ops, value);
        if (parsed.result().isPresent()) {
            return parsed.map(Optional::of);
        }
        return DataResult.success(Optional.empty());
    }

    @Override
    public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        if (input.isPresent()) {
            return prefix.add(this.name, this.elementCodec.encodeStart(ops, input.get()));
        }
        return prefix;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.of(ops.createString(this.name));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OptionalFieldCodec that = (OptionalFieldCodec)o;
        return Objects.equals(this.name, that.name) && Objects.equals(this.elementCodec, that.elementCodec);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.elementCodec);
    }

    public String toString() {
        return "OptionalFieldCodec[" + this.name + ": " + this.elementCodec + ']';
    }
}
