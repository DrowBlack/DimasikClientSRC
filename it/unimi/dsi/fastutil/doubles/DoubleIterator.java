package it.unimi.dsi.fastutil.doubles;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;

public interface DoubleIterator
extends PrimitiveIterator.OfDouble {
    @Override
    public double nextDouble();

    @Override
    @Deprecated
    default public Double next() {
        return this.nextDouble();
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Double> action) {
        this.forEachRemaining(action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextDouble();
        }
        return n - i - 1;
    }
}
