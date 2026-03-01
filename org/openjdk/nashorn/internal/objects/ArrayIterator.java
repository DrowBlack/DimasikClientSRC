package org.openjdk.nashorn.internal.objects;

import java.util.Collections;
import org.openjdk.nashorn.internal.objects.AbstractIterator;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.IteratorResult;
import org.openjdk.nashorn.internal.objects.NativeArray;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Undefined;

public class ArrayIterator
extends AbstractIterator {
    private static PropertyMap $nasgenmap$;
    private ScriptObject iteratedObject;
    private long nextIndex = 0L;
    private final AbstractIterator.IterationKind iterationKind;
    private final Global global;

    private ArrayIterator(Object iteratedObject, AbstractIterator.IterationKind iterationKind, Global global) {
        super(global.getArrayIteratorPrototype(), $nasgenmap$);
        this.iteratedObject = iteratedObject instanceof ScriptObject ? (ScriptObject)iteratedObject : null;
        this.iterationKind = iterationKind;
        this.global = global;
    }

    static ArrayIterator newArrayValueIterator(Object iteratedObject) {
        return new ArrayIterator(Global.toObject(iteratedObject), AbstractIterator.IterationKind.VALUE, Global.instance());
    }

    static ArrayIterator newArrayKeyIterator(Object iteratedObject) {
        return new ArrayIterator(Global.toObject(iteratedObject), AbstractIterator.IterationKind.KEY, Global.instance());
    }

    static ArrayIterator newArrayKeyValueIterator(Object iteratedObject) {
        return new ArrayIterator(Global.toObject(iteratedObject), AbstractIterator.IterationKind.KEY_VALUE, Global.instance());
    }

    public static Object next(Object self, Object arg) {
        if (!(self instanceof ArrayIterator)) {
            throw ECMAErrors.typeError("not.a.array.iterator", ScriptRuntime.safeToString(self));
        }
        return ((ArrayIterator)self).next(arg);
    }

    @Override
    public String getClassName() {
        return "Array Iterator";
    }

    @Override
    protected IteratorResult next(Object arg) {
        long index = this.nextIndex++;
        if (this.iteratedObject == null || index >= JSType.toUint32(this.iteratedObject.getLength())) {
            this.iteratedObject = null;
            return this.makeResult(Undefined.getUndefined(), Boolean.TRUE, this.global);
        }
        if (this.iterationKind == AbstractIterator.IterationKind.KEY_VALUE) {
            NativeArray value = new NativeArray(new Object[]{JSType.toNarrowestNumber(index), this.iteratedObject.get(index)});
            return this.makeResult(value, Boolean.FALSE, this.global);
        }
        Number value = this.iterationKind == AbstractIterator.IterationKind.KEY ? JSType.toNarrowestNumber(index) : this.iteratedObject.get(index);
        return this.makeResult(value, Boolean.FALSE, this.global);
    }

    static {
        ArrayIterator.$clinit$();
    }

    public static void $clinit$() {
        $nasgenmap$ = PropertyMap.newMap(Collections.EMPTY_LIST);
    }
}
