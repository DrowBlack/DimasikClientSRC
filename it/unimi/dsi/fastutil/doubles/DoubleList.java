package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;

public interface DoubleList
extends List<Double>,
Comparable<List<? extends Double>>,
DoubleCollection {
    @Override
    public DoubleListIterator iterator();

    public DoubleListIterator listIterator();

    public DoubleListIterator listIterator(int var1);

    public DoubleList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, double[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, double[] var2);

    public void addElements(int var1, double[] var2, int var3, int var4);

    @Override
    public boolean add(double var1);

    @Override
    public void add(int var1, double var2);

    @Override
    @Deprecated
    default public void add(int index, Double key) {
        this.add(index, (double)key);
    }

    public boolean addAll(int var1, DoubleCollection var2);

    public boolean addAll(int var1, DoubleList var2);

    public boolean addAll(DoubleList var1);

    @Override
    public double set(int var1, double var2);

    public double getDouble(int var1);

    public int indexOf(double var1);

    public int lastIndexOf(double var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return DoubleCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Double get(int index) {
        return this.getDouble(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Double)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Double)o);
    }

    @Override
    @Deprecated
    default public boolean add(Double k) {
        return this.add((double)k);
    }

    public double removeDouble(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return DoubleCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Double remove(int index) {
        return this.removeDouble(index);
    }

    @Override
    @Deprecated
    default public Double set(int index, Double k) {
        return this.set(index, (double)k);
    }
}
