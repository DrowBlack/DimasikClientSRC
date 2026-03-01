package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.List;

public interface ShortList
extends List<Short>,
Comparable<List<? extends Short>>,
ShortCollection {
    @Override
    public ShortListIterator iterator();

    public ShortListIterator listIterator();

    public ShortListIterator listIterator(int var1);

    public ShortList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, short[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, short[] var2);

    public void addElements(int var1, short[] var2, int var3, int var4);

    @Override
    public boolean add(short var1);

    @Override
    public void add(int var1, short var2);

    @Override
    @Deprecated
    default public void add(int index, Short key) {
        this.add(index, (short)key);
    }

    public boolean addAll(int var1, ShortCollection var2);

    public boolean addAll(int var1, ShortList var2);

    public boolean addAll(ShortList var1);

    @Override
    public short set(int var1, short var2);

    public short getShort(int var1);

    public int indexOf(short var1);

    public int lastIndexOf(short var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return ShortCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Short get(int index) {
        return this.getShort(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Short)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Short)o);
    }

    @Override
    @Deprecated
    default public boolean add(Short k) {
        return this.add((short)k);
    }

    public short removeShort(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return ShortCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Short remove(int index) {
        return this.removeShort(index);
    }

    @Override
    @Deprecated
    default public Short set(int index, Short k) {
        return this.set(index, (short)k);
    }
}
