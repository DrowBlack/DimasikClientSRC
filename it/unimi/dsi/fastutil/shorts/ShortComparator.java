package it.unimi.dsi.fastutil.shorts;

import java.util.Comparator;

@FunctionalInterface
public interface ShortComparator
extends Comparator<Short> {
    @Override
    public int compare(short var1, short var2);

    @Override
    @Deprecated
    default public int compare(Short ok1, Short ok2) {
        return this.compare((short)ok1, (short)ok2);
    }
}
