package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatIterable;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Collection;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

public interface FloatCollection
extends Collection<Float>,
FloatIterable {
    @Override
    public FloatIterator iterator();

    @Override
    public boolean add(float var1);

    public boolean contains(float var1);

    public boolean rem(float var1);

    @Override
    @Deprecated
    default public boolean add(Float key) {
        return this.add(key.floatValue());
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains(((Float)key).floatValue());
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem(((Float)key).floatValue());
    }

    public float[] toFloatArray();

    @Deprecated
    public float[] toFloatArray(float[] var1);

    public float[] toArray(float[] var1);

    public boolean addAll(FloatCollection var1);

    public boolean containsAll(FloatCollection var1);

    public boolean removeAll(FloatCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Float> filter) {
        return this.removeIf((double key) -> filter.test(Float.valueOf(SafeMath.safeDoubleToFloat(key))));
    }

    default public boolean removeIf(DoublePredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        FloatIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextFloat())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    public boolean retainAll(FloatCollection var1);
}
