package com.jhlabs.math;

import com.jhlabs.math.CompoundFunction2D;
import com.jhlabs.math.Function2D;

public class TurbulenceFunction
extends CompoundFunction2D {
    private float octaves;

    public TurbulenceFunction(Function2D basis, float octaves) {
        super(basis);
        this.octaves = octaves;
    }

    public void setOctaves(float octaves) {
        this.octaves = octaves;
    }

    public float getOctaves() {
        return this.octaves;
    }

    @Override
    public float evaluate(float x, float y) {
        float t = 0.0f;
        float f = 1.0f;
        while (f <= this.octaves) {
            t += Math.abs(this.basis.evaluate(f * x, f * y)) / f;
            f *= 2.0f;
        }
        return t;
    }
}
