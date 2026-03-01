package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import java.util.List;

public interface FloatList
extends List<Float>,
Comparable<List<? extends Float>>,
FloatCollection {
    @Override
    public FloatListIterator iterator();

    public FloatListIterator listIterator();

    public FloatListIterator listIterator(int var1);

    public FloatList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, float[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, float[] var2);

    public void addElements(int var1, float[] var2, int var3, int var4);

    @Override
    public boolean add(float var1);

    @Override
    public void add(int var1, float var2);

    @Override
    @Deprecated
    default public void add(int index, Float key) {
        this.add(index, key.floatValue());
    }

    public boolean addAll(int var1, FloatCollection var2);

    public boolean addAll(int var1, FloatList var2);

    public boolean addAll(FloatList var1);

    @Override
    public float set(int var1, float var2);

    public float getFloat(int var1);

    public int indexOf(float var1);

    public int lastIndexOf(float var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return FloatCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Float get(int index) {
        return Float.valueOf(this.getFloat(index));
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf(((Float)o).floatValue());
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf(((Float)o).floatValue());
    }

    @Override
    @Deprecated
    default public boolean add(Float k) {
        return this.add(k.floatValue());
    }

    public float removeFloat(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return FloatCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Float remove(int index) {
        return Float.valueOf(this.removeFloat(index));
    }

    @Override
    @Deprecated
    default public Float set(int index, Float k) {
        return Float.valueOf(this.set(index, k.floatValue()));
    }
}
