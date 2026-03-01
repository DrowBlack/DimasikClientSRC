package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Collection;
import java.util.Objects;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public interface LongCollection
extends Collection<Long>,
LongIterable {
    @Override
    public LongIterator iterator();

    @Override
    public boolean add(long var1);

    public boolean contains(long var1);

    public boolean rem(long var1);

    @Override
    @Deprecated
    default public boolean add(Long key) {
        return this.add((long)key);
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains((Long)key);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem((Long)key);
    }

    public long[] toLongArray();

    @Deprecated
    public long[] toLongArray(long[] var1);

    public long[] toArray(long[] var1);

    public boolean addAll(LongCollection var1);

    public boolean containsAll(LongCollection var1);

    public boolean removeAll(LongCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Long> filter) {
        return this.removeIf((long key) -> filter.test(key));
    }

    default public boolean removeIf(LongPredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        LongIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextLong())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    public boolean retainAll(LongCollection var1);
}
