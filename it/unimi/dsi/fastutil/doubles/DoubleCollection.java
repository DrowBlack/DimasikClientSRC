package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.Collection;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

public interface DoubleCollection
extends Collection<Double>,
DoubleIterable {
    @Override
    public DoubleIterator iterator();

    @Override
    public boolean add(double var1);

    public boolean contains(double var1);

    public boolean rem(double var1);

    @Override
    @Deprecated
    default public boolean add(Double key) {
        return this.add((double)key);
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains((Double)key);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem((Double)key);
    }

    public double[] toDoubleArray();

    @Deprecated
    public double[] toDoubleArray(double[] var1);

    public double[] toArray(double[] var1);

    public boolean addAll(DoubleCollection var1);

    public boolean containsAll(DoubleCollection var1);

    public boolean removeAll(DoubleCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Double> filter) {
        return this.removeIf((double key) -> filter.test(key));
    }

    default public boolean removeIf(DoublePredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        DoubleIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextDouble())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    public boolean retainAll(DoubleCollection var1);
}
