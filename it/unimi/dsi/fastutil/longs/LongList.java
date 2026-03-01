package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.util.List;

public interface LongList
extends List<Long>,
Comparable<List<? extends Long>>,
LongCollection {
    @Override
    public LongListIterator iterator();

    public LongListIterator listIterator();

    public LongListIterator listIterator(int var1);

    public LongList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, long[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, long[] var2);

    public void addElements(int var1, long[] var2, int var3, int var4);

    @Override
    public boolean add(long var1);

    @Override
    public void add(int var1, long var2);

    @Override
    @Deprecated
    default public void add(int index, Long key) {
        this.add(index, (long)key);
    }

    public boolean addAll(int var1, LongCollection var2);

    public boolean addAll(int var1, LongList var2);

    public boolean addAll(LongList var1);

    @Override
    public long set(int var1, long var2);

    public long getLong(int var1);

    public int indexOf(long var1);

    public int lastIndexOf(long var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return LongCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Long get(int index) {
        return this.getLong(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Long)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Long)o);
    }

    @Override
    @Deprecated
    default public boolean add(Long k) {
        return this.add((long)k);
    }

    public long removeLong(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return LongCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Long remove(int index) {
        return this.removeLong(index);
    }

    @Override
    @Deprecated
    default public Long set(int index, Long k) {
        return this.set(index, (long)k);
    }
}
