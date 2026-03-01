package com.mojang.serialization.codecs;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.RecordBuilder;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.mutable.MutableObject;

public final class CompoundListCodec<K, V>
implements Codec<List<Pair<K, V>>> {
    private final Codec<K> keyCodec;
    private final Codec<V> elementCodec;

    public CompoundListCodec(Codec<K> keyCodec, Codec<V> elementCodec) {
        this.keyCodec = keyCodec;
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<Pair<List<Pair<K, V>>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMapEntries(input).flatMap((? super R map) -> {
            ImmutableList.Builder read = ImmutableList.builder();
            ImmutableMap.Builder failed = ImmutableMap.builder();
            MutableObject<DataResult<Unit>> result = new MutableObject<DataResult<Unit>>(DataResult.success(Unit.INSTANCE, Lifecycle.experimental()));
            map.accept((key, value) -> {
                DataResult k = this.keyCodec.parse(ops, key);
                DataResult v = this.elementCodec.parse(ops, value);
                DataResult<Pair> readEntry = k.apply2stable(Pair::new, v);
                readEntry.error().ifPresent(e -> failed.put(key, value));
                result.setValue(((DataResult)result.getValue()).apply2stable((u, e) -> {
                    read.add(e);
                    return u;
                }, readEntry));
            });
            ImmutableCollection elements = read.build();
            Object errors = ops.createMap(failed.build());
            Pair pair = Pair.of(elements, errors);
            return result.getValue().map((? super R unit) -> pair).setPartial(pair);
        });
    }

    @Override
    public <T> DataResult<T> encode(List<Pair<K, V>> input, DynamicOps<T> ops, T prefix) {
        RecordBuilder<T> builder = ops.mapBuilder();
        for (Pair<K, V> pair : input) {
            builder.add(this.keyCodec.encodeStart(ops, pair.getFirst()), this.elementCodec.encodeStart(ops, pair.getSecond()));
        }
        return builder.build(prefix);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CompoundListCodec that = (CompoundListCodec)o;
        return Objects.equals(this.keyCodec, that.keyCodec) && Objects.equals(this.elementCodec, that.elementCodec);
    }

    public int hashCode() {
        return Objects.hash(this.keyCodec, this.elementCodec);
    }

    public String toString() {
        return "CompoundListCodec[" + this.keyCodec + " -> " + this.elementCodec + ']';
    }
}
