package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import java.util.AbstractCollection;

public abstract class AbstractLongCollection
extends AbstractCollection<Long>
implements LongCollection {
    protected AbstractLongCollection() {
    }

    @Override
    public abstract LongIterator iterator();

    @Override
    public boolean add(long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(long k) {
        LongIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextLong()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(long k) {
        LongIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k != iterator.nextLong()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean add(Long key) {
        return LongCollection.super.add(key);
    }

    @Override
    @Deprecated
    public boolean contains(Object key) {
        return LongCollection.super.contains(key);
    }

    @Override
    @Deprecated
    public boolean remove(Object key) {
        return LongCollection.super.remove(key);
    }

    @Override
    public long[] toArray(long[] a) {
        if (a == null || a.length < this.size()) {
            a = new long[this.size()];
        }
        LongIterators.unwrap(this.iterator(), a);
        return a;
    }

    @Override
    public long[] toLongArray() {
        return this.toArray((long[])null);
    }

    @Override
    @Deprecated
    public long[] toLongArray(long[] a) {
        return this.toArray(a);
    }

    @Override
    public boolean addAll(LongCollection c) {
        boolean retVal = false;
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.add(i.nextLong())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean containsAll(LongCollection c) {
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            if (this.contains(i.nextLong())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean removeAll(LongCollection c) {
        boolean retVal = false;
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            if (!this.rem(i.nextLong())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean retainAll(LongCollection c) {
        boolean retVal = false;
        LongIterator i = this.iterator();
        while (i.hasNext()) {
            if (c.contains(i.nextLong())) continue;
            i.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        LongIterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            long k = i.nextLong();
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}
