package com.mojang.serialization;

import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.stream.Stream;

public final class KeyCompressor<T> {
    private final Int2ObjectMap<T> decompress = new Int2ObjectArrayMap<T>();
    private final Object2IntMap<T> compress = new Object2IntArrayMap<T>();
    private final Object2IntMap<String> compressString = new Object2IntArrayMap<String>();
    private final int size;
    private final DynamicOps<T> ops;

    public KeyCompressor(DynamicOps<T> ops, Stream<T> keyStream) {
        this.ops = ops;
        this.compressString.defaultReturnValue(-1);
        keyStream.forEach(key -> {
            if (this.compress.containsKey(key)) {
                return;
            }
            int next = this.compress.size();
            this.compress.put((T)key, next);
            ops.getStringValue(key).result().ifPresent(k -> this.compressString.put((String)k, next));
            this.decompress.put(next, (T)key);
        });
        this.size = this.compress.size();
    }

    public T decompress(int key) {
        return (T)this.decompress.get(key);
    }

    public int compress(String key) {
        int id = this.compressString.getInt(key);
        return id == -1 ? this.compress(this.ops.createString(key)) : id;
    }

    public int compress(T key) {
        return (Integer)this.compress.get(key);
    }

    public int size() {
        return this.size;
    }
}
