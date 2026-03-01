package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.util.List;

public interface BooleanList
extends List<Boolean>,
Comparable<List<? extends Boolean>>,
BooleanCollection {
    @Override
    public BooleanListIterator iterator();

    public BooleanListIterator listIterator();

    public BooleanListIterator listIterator(int var1);

    public BooleanList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, boolean[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, boolean[] var2);

    public void addElements(int var1, boolean[] var2, int var3, int var4);

    @Override
    public boolean add(boolean var1);

    @Override
    public void add(int var1, boolean var2);

    @Override
    @Deprecated
    default public void add(int index, Boolean key) {
        this.add(index, (boolean)key);
    }

    public boolean addAll(int var1, BooleanCollection var2);

    public boolean addAll(int var1, BooleanList var2);

    public boolean addAll(BooleanList var1);

    @Override
    public boolean set(int var1, boolean var2);

    public boolean getBoolean(int var1);

    public int indexOf(boolean var1);

    public int lastIndexOf(boolean var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return BooleanCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Boolean get(int index) {
        return this.getBoolean(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Boolean)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Boolean)o);
    }

    @Override
    @Deprecated
    default public boolean add(Boolean k) {
        return this.add((boolean)k);
    }

    public boolean removeBoolean(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return BooleanCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Boolean remove(int index) {
        return this.removeBoolean(index);
    }

    @Override
    @Deprecated
    default public Boolean set(int index, Boolean k) {
        return this.set(index, (boolean)k);
    }
}
