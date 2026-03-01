package org.openjdk.nashorn.internal.runtime.arrays;

import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;

public abstract class IteratorAction<T> {
    protected final Object self;
    protected Object thisArg;
    protected final Object callbackfn;
    protected T result;
    protected long index;
    private final ArrayLikeIterator<Object> iter;

    public IteratorAction(Object self, Object callbackfn, Object thisArg, T initialResult) {
        this(self, callbackfn, thisArg, initialResult, ArrayLikeIterator.arrayLikeIterator(self));
    }

    public IteratorAction(Object self, Object callbackfn, Object thisArg, T initialResult, ArrayLikeIterator<Object> iter) {
        this.self = self;
        this.callbackfn = callbackfn;
        this.result = initialResult;
        this.iter = iter;
        this.thisArg = thisArg;
    }

    protected void applyLoopBegin(ArrayLikeIterator<Object> iterator) {
    }

    public final T apply() {
        boolean strict = Bootstrap.isStrictCallable(this.callbackfn);
        this.thisArg = this.thisArg == ScriptRuntime.UNDEFINED && !strict ? Context.getGlobal() : this.thisArg;
        this.applyLoopBegin(this.iter);
        boolean reverse = this.iter.isReverse();
        while (this.iter.hasNext()) {
            Object val = this.iter.next();
            this.index = this.iter.nextIndex() + (long)(reverse ? 1 : -1);
            try {
                if (this.forEach(val, this.index)) continue;
                return this.result;
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return this.result;
    }

    protected abstract boolean forEach(Object var1, double var2) throws Throwable;
}
