package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.util.Collection;

public interface BooleanCollection
extends Collection<Boolean>,
BooleanIterable {
    @Override
    public BooleanIterator iterator();

    @Override
    public boolean add(boolean var1);

    public boolean contains(boolean var1);

    public boolean rem(boolean var1);

    @Override
    @Deprecated
    default public boolean add(Boolean key) {
        return this.add((boolean)key);
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains((Boolean)key);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem((Boolean)key);
    }

    public boolean[] toBooleanArray();

    @Deprecated
    public boolean[] toBooleanArray(boolean[] var1);

    public boolean[] toArray(boolean[] var1);

    public boolean addAll(BooleanCollection var1);

    public boolean containsAll(BooleanCollection var1);

    public boolean removeAll(BooleanCollection var1);

    public boolean retainAll(BooleanCollection var1);
}
