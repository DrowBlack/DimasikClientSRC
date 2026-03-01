package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Set;

public interface FloatSet
extends FloatCollection,
Set<Float> {
    @Override
    public FloatIterator iterator();

    public boolean remove(float var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return FloatCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Float o) {
        return FloatCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return FloatCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(float k) {
        return this.remove(k);
    }
}
