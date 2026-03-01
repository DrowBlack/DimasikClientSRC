package it.unimi.dsi.fastutil.longs;

import java.util.Comparator;

@FunctionalInterface
public interface LongComparator
extends Comparator<Long> {
    @Override
    public int compare(long var1, long var3);

    @Override
    @Deprecated
    default public int compare(Long ok1, Long ok2) {
        return this.compare((long)ok1, (long)ok2);
    }
}
