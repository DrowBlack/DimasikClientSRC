package com.mojang.serialization.codecs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Map;

public interface BaseMapCodec<K, V> {
    public Codec<K> keyCodec();

    public Codec<V> elementCodec();

    default public <T> DataResult<Map<K, V>> decode(DynamicOps<T> ops, MapLike<T> input) {
        ImmutableMap.Builder read = ImmutableMap.builder();
        ImmutableList.Builder failed = ImmutableList.builder();
        DataResult result = input.entries().reduce(DataResult.success(Unit.INSTANCE, Lifecycle.stable()), (r, pair) -> {
            DataResult k = this.keyCodec().parse(ops, pair.getFirst());
            DataResult v = this.elementCodec().parse(ops, pair.getSecond());
            DataResult<Pair> entry = k.apply2stable(Pair::of, v);
            entry.error().ifPresent(e -> failed.add(pair));
            return r.apply2stable((u, p) -> {
                read.put(p.getFirst(), p.getSecond());
                return u;
            }, entry);
        }, (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2));
        ImmutableMap elements = read.build();
        Object errors = ops.createMap(failed.build().stream());
        return result.map(unit -> elements).setPartial(elements).mapError(e -> e + " missed input: " + errors);
    }

    default public <T> RecordBuilder<T> encode(Map<K, V> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        for (Map.Entry<K, V> entry : input.entrySet()) {
            prefix.add(this.keyCodec().encodeStart(ops, entry.getKey()), this.elementCodec().encodeStart(ops, entry.getValue()));
        }
        return prefix;
    }
}
