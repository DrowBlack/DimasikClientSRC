package net.optifine.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;

public abstract class IntExpiringCache<T> {
    private final int intervalMs;
    private long timeCheckMs;
    private Int2ObjectOpenHashMap<Wrapper<T>> map = new Int2ObjectOpenHashMap();

    public IntExpiringCache(int intervalMs) {
        this.intervalMs = intervalMs;
    }

    public T get(int key) {
        Wrapper<T> wrapper1;
        long i = System.currentTimeMillis();
        if (!this.map.isEmpty() && i >= this.timeCheckMs) {
            this.timeCheckMs = i + (long)this.intervalMs;
            long j = i - (long)this.intervalMs;
            IntSet intset = this.map.keySet();
            IntIterator intiterator = intset.iterator();
            while (intiterator.hasNext()) {
                Wrapper<T> wrapper;
                int k = intiterator.nextInt();
                if (k == key || (wrapper = this.map.get(k)).getAccessTimeMs() > j) continue;
                intiterator.remove();
            }
        }
        if ((wrapper1 = this.map.get(key)) == null) {
            T t = this.make();
            wrapper1 = new Wrapper<T>(t);
            this.map.put(key, wrapper1);
        }
        wrapper1.setAccessTimeMs(i);
        return wrapper1.getValue();
    }

    protected abstract T make();

    public static class Wrapper<T> {
        private final T value;
        private long accessTimeMs;

        public Wrapper(T value) {
            this.value = value;
        }

        public T getValue() {
            return this.value;
        }

        public long getAccessTimeMs() {
            return this.accessTimeMs;
        }

        public void setAccessTimeMs(long accessTimeMs) {
            this.accessTimeMs = accessTimeMs;
        }
    }
}
