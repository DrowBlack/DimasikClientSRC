package org.openjdk.nashorn.internal.runtime.arrays;

import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.PropertyDescriptor;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayData;
import org.openjdk.nashorn.internal.runtime.arrays.SealedArrayFilter;

final class FrozenArrayFilter
extends SealedArrayFilter {
    FrozenArrayFilter(ArrayData underlying) {
        super(underlying);
    }

    @Override
    public ArrayData copy() {
        return this;
    }

    @Override
    public PropertyDescriptor getDescriptor(Global global, int index) {
        return global.newDataDescriptor(this.getObject(index), false, true, false);
    }

    @Override
    public ArrayData set(int index, int value, boolean strict) {
        if (strict) {
            throw ECMAErrors.typeError("cant.set.property", Integer.toString(index), "frozen array");
        }
        return this;
    }

    @Override
    public ArrayData set(int index, double value, boolean strict) {
        if (strict) {
            throw ECMAErrors.typeError("cant.set.property", Integer.toString(index), "frozen array");
        }
        return this;
    }

    @Override
    public ArrayData set(int index, Object value, boolean strict) {
        if (strict) {
            throw ECMAErrors.typeError("cant.set.property", Integer.toString(index), "frozen array");
        }
        return this;
    }

    @Override
    public ArrayData push(boolean strict, Object ... items) {
        return this;
    }

    @Override
    public Object pop() {
        int len = (int)this.underlying.length();
        return len == 0 ? ScriptRuntime.UNDEFINED : this.underlying.getObject(len - 1);
    }
}
