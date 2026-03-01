package com.mojang.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface MapLike<T> {
    @Nullable
    public T get(T var1);

    @Nullable
    public T get(String var1);

    public Stream<Pair<T, T>> entries();

    public static <T> MapLike<T> forMap(final Map<T, T> map, final DynamicOps<T> ops) {
        return new MapLike<T>(){

            @Override
            @Nullable
            public T get(T key) {
                return map.get(key);
            }

            @Override
            @Nullable
            public T get(String key) {
                return this.get(ops.createString(key));
            }

            @Override
            public Stream<Pair<T, T>> entries() {
                return map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue()));
            }

            public String toString() {
                return "MapLike[" + map + "]";
            }
        };
    }
}
