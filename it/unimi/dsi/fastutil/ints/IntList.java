package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.List;

public interface IntList
extends List<Integer>,
Comparable<List<? extends Integer>>,
IntCollection {
    @Override
    public IntListIterator iterator();

    public IntListIterator listIterator();

    public IntListIterator listIterator(int var1);

    public IntList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, int[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, int[] var2);

    public void addElements(int var1, int[] var2, int var3, int var4);

    @Override
    public boolean add(int var1);

    @Override
    public void add(int var1, int var2);

    @Override
    @Deprecated
    default public void add(int index, Integer key) {
        this.add(index, (int)key);
    }

    public boolean addAll(int var1, IntCollection var2);

    public boolean addAll(int var1, IntList var2);

    public boolean addAll(IntList var1);

    @Override
    public int set(int var1, int var2);

    public int getInt(int var1);

    public int indexOf(int var1);

    public int lastIndexOf(int var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return IntCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Integer get(int index) {
        return this.getInt(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Integer)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Integer)o);
    }

    @Override
    @Deprecated
    default public boolean add(Integer k) {
        return this.add((int)k);
    }

    public int removeInt(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return IntCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Integer remove(int index) {
        return this.removeInt(index);
    }

    @Override
    @Deprecated
    default public Integer set(int index, Integer k) {
        return this.set(index, (int)k);
    }
}
