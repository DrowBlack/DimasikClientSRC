package com.mojang.serialization;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class Dynamic<T>
extends DynamicLike<T> {
    private final T value;

    public Dynamic(DynamicOps<T> ops) {
        this(ops, ops.empty());
    }

    public Dynamic(DynamicOps<T> ops, @Nullable T value) {
        super(ops);
        this.value = value == null ? ops.empty() : value;
    }

    public T getValue() {
        return this.value;
    }

    public Dynamic<T> map(Function<? super T, ? extends T> function) {
        return new Dynamic<T>(this.ops, function.apply(this.value));
    }

    public <U> Dynamic<U> castTyped(DynamicOps<U> ops) {
        if (!Objects.equals(this.ops, ops)) {
            throw new IllegalStateException("Dynamic type doesn't match");
        }
        return this;
    }

    public <U> U cast(DynamicOps<U> ops) {
        return this.castTyped(ops).getValue();
    }

    public OptionalDynamic<T> merge(Dynamic<?> value) {
        DataResult<Dynamic> merged = this.ops.mergeToList(this.value, value.cast(this.ops));
        return new OptionalDynamic(this.ops, merged.map((? super R m) -> new Dynamic<Object>(this.ops, m)));
    }

    public OptionalDynamic<T> merge(Dynamic<?> key, Dynamic<?> value) {
        DataResult<Dynamic> merged = this.ops.mergeToMap(this.value, key.cast(this.ops), value.cast(this.ops));
        return new OptionalDynamic(this.ops, merged.map((? super R m) -> new Dynamic<Object>(this.ops, m)));
    }

    public DataResult<Map<Dynamic<T>, Dynamic<T>>> getMapValues() {
        return this.ops.getMapValues(this.value).map((? super R map) -> {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            map.forEach(entry -> builder.put(new Dynamic(this.ops, entry.getFirst()), new Dynamic(this.ops, entry.getSecond())));
            return builder.build();
        });
    }

    public Dynamic<T> updateMapValues(Function<Pair<Dynamic<?>, Dynamic<?>>, Pair<Dynamic<?>, Dynamic<?>>> updater) {
        return DataFixUtils.orElse(this.getMapValues().map((? super R map) -> map.entrySet().stream().map((? super T e) -> {
            Pair pair = (Pair)updater.apply(Pair.of(e.getKey(), e.getValue()));
            return Pair.of(((Dynamic)pair.getFirst()).castTyped(this.ops), ((Dynamic)pair.getSecond()).castTyped(this.ops));
        }).collect(Pair.toMap())).map(this::createMap).result(), this);
    }

    @Override
    public DataResult<Number> asNumber() {
        return this.ops.getNumberValue(this.value);
    }

    @Override
    public DataResult<String> asString() {
        return this.ops.getStringValue(this.value);
    }

    @Override
    public DataResult<Stream<Dynamic<T>>> asStreamOpt() {
        return this.ops.getStream(this.value).map((? super R s) -> s.map((? super T e) -> new Dynamic<Object>(this.ops, e)));
    }

    @Override
    public DataResult<Stream<Pair<Dynamic<T>, Dynamic<T>>>> asMapOpt() {
        return this.ops.getMapValues(this.value).map((? super R s) -> s.map((? super T p) -> Pair.of(new Dynamic(this.ops, p.getFirst()), new Dynamic(this.ops, p.getSecond()))));
    }

    @Override
    public DataResult<ByteBuffer> asByteBufferOpt() {
        return this.ops.getByteBuffer(this.value);
    }

    @Override
    public DataResult<IntStream> asIntStreamOpt() {
        return this.ops.getIntStream(this.value);
    }

    @Override
    public DataResult<LongStream> asLongStreamOpt() {
        return this.ops.getLongStream(this.value);
    }

    @Override
    public OptionalDynamic<T> get(String key) {
        return new OptionalDynamic(this.ops, this.ops.getMap(this.value).flatMap(m -> {
            Object value = m.get(key);
            if (value == null) {
                return DataResult.error("key missing: " + key + " in " + this.value);
            }
            return DataResult.success(new Dynamic(this.ops, value));
        }));
    }

    @Override
    public DataResult<T> getGeneric(T key) {
        return this.ops.getGeneric(this.value, key);
    }

    public Dynamic<T> remove(String key) {
        return this.map(v -> this.ops.remove(v, key));
    }

    public Dynamic<T> set(String key, Dynamic<?> value) {
        return this.map(v -> this.ops.set(v, key, value.cast(this.ops)));
    }

    public Dynamic<T> update(String key, Function<Dynamic<?>, Dynamic<?>> function) {
        return this.map(v -> this.ops.update(v, key, value -> ((Dynamic)function.apply(new Dynamic<Object>(this.ops, value))).cast(this.ops)));
    }

    public Dynamic<T> updateGeneric(T key, Function<T, T> function) {
        return this.map(v -> this.ops.updateGeneric(v, key, function));
    }

    @Override
    public DataResult<T> getElement(String key) {
        return this.getElementGeneric(this.ops.createString(key));
    }

    @Override
    public DataResult<T> getElementGeneric(T key) {
        return this.ops.getGeneric(this.value, key);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Dynamic dynamic = (Dynamic)o;
        return Objects.equals(this.ops, dynamic.ops) && Objects.equals(this.value, dynamic.value);
    }

    public int hashCode() {
        return Objects.hash(this.ops, this.value);
    }

    public String toString() {
        return String.format("%s[%s]", this.ops, this.value);
    }

    public <R> Dynamic<R> convert(DynamicOps<R> outOps) {
        return new Dynamic<R>(outOps, Dynamic.convert(this.ops, outOps, this.value));
    }

    public <V> V into(Function<? super Dynamic<T>, ? extends V> action) {
        return action.apply(this);
    }

    @Override
    public <A> DataResult<Pair<A, T>> decode(Decoder<? extends A> decoder) {
        return decoder.decode(this.ops, this.value).map((? super R p) -> p.mapFirst(Function.identity()));
    }

    public static <S, T> T convert(DynamicOps<S> inOps, DynamicOps<T> outOps, S input) {
        if (Objects.equals(inOps, outOps)) {
            return (T)input;
        }
        return inOps.convertTo(outOps, input);
    }
}
