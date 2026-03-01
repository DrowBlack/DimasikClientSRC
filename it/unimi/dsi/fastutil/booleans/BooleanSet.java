package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.util.Set;

public interface BooleanSet
extends BooleanCollection,
Set<Boolean> {
    @Override
    public BooleanIterator iterator();

    public boolean remove(boolean var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return BooleanCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Boolean o) {
        return BooleanCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return BooleanCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(boolean k) {
        return this.remove(k);
    }
}
