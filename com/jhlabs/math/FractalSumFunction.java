package com.jhlabs.math;

import com.jhlabs.math.CompoundFunction2D;
import com.jhlabs.math.Function2D;

public class FractalSumFunction
extends CompoundFunction2D {
    private float octaves = 1.0f;

    public FractalSumFunction(Function2D basis) {
        super(basis);
    }

    @Override
    public float evaluate(float x, float y) {
        float t = 0.0f;
        float f = 1.0f;
        while (f <= this.octaves) {
            t += this.basis.evaluate(f * x, f * y) / f;
            f *= 2.0f;
        }
        return t;
    }
}
