package it.unimi.dsi.fastutil.floats;

import java.util.Comparator;

@FunctionalInterface
public interface FloatComparator
extends Comparator<Float> {
    @Override
    public int compare(float var1, float var2);

    @Override
    @Deprecated
    default public int compare(Float ok1, Float ok2) {
        return this.compare(ok1.floatValue(), ok2.floatValue());
    }
}
