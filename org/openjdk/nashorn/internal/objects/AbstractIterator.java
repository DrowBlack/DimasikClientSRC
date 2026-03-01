package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.util.Collections;
import java.util.function.Consumer;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.IteratorResult;
import org.openjdk.nashorn.internal.objects.NativeSymbol;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.linker.InvokeByName;

public abstract class AbstractIterator
extends ScriptObject {
    private static PropertyMap $nasgenmap$;
    private static final Object ITERATOR_INVOKER_KEY;
    private static final Object NEXT_INVOKER_KEY;
    private static final Object DONE_INVOKER_KEY;
    private static final Object VALUE_INVOKER_KEY;

    protected AbstractIterator(ScriptObject prototype, PropertyMap map) {
        super(prototype, map);
    }

    public static Object getIterator(Object self) {
        return self;
    }

    @Override
    public String getClassName() {
        return "Iterator";
    }

    protected abstract IteratorResult next(Object var1);

    protected IteratorResult makeResult(Object value, Boolean done, Global global) {
        return new IteratorResult(value, done, global);
    }

    static MethodHandle getIteratorInvoker(Global global) {
        return global.getDynamicInvoker(ITERATOR_INVOKER_KEY, () -> Bootstrap.createDynamicCallInvoker(Object.class, Object.class, Object.class));
    }

    public static InvokeByName getNextInvoker(Global global) {
        return global.getInvokeByName(NEXT_INVOKER_KEY, () -> new InvokeByName("next", Object.class, Object.class, Object.class));
    }

    public static MethodHandle getDoneInvoker(Global global) {
        return global.getDynamicInvoker(DONE_INVOKER_KEY, () -> Bootstrap.createDynamicInvoker("done", 0, Object.class, Object.class));
    }

    public static MethodHandle getValueInvoker(Global global) {
        return global.getDynamicInvoker(VALUE_INVOKER_KEY, () -> Bootstrap.createDynamicInvoker("value", 0, Object.class, Object.class));
    }

    public static Object getIterator(Object iterable, Global global) {
        Object object = Global.toObject(iterable);
        if (object instanceof ScriptObject) {
            Object getter = ((ScriptObject)object).get(NativeSymbol.iterator);
            if (Bootstrap.isCallable(getter)) {
                try {
                    MethodHandle invoker = AbstractIterator.getIteratorInvoker(global);
                    Object value = invoker.invokeExact(getter, iterable);
                    if (JSType.isPrimitive(value)) {
                        throw ECMAErrors.typeError("not.an.object", ScriptRuntime.safeToString(value));
                    }
                    return value;
                }
                catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
            throw ECMAErrors.typeError("not.a.function", ScriptRuntime.safeToString(getter));
        }
        throw ECMAErrors.typeError("cannot.get.iterator", ScriptRuntime.safeToString(iterable));
    }

    public static void iterate(Object iterable, Global global, Consumer<Object> consumer) {
        Object iterator = AbstractIterator.getIterator(Global.toObject(iterable), global);
        InvokeByName nextInvoker = AbstractIterator.getNextInvoker(global);
        MethodHandle doneInvoker = AbstractIterator.getDoneInvoker(global);
        MethodHandle valueInvoker = AbstractIterator.getValueInvoker(global);
        try {
            Object done;
            Object result;
            Object next;
            while (Bootstrap.isCallable(next = nextInvoker.getGetter().invokeExact(iterator)) && (result = nextInvoker.getInvoker().invokeExact(next, iterator, null)) instanceof ScriptObject && !JSType.toBoolean(done = doneInvoker.invokeExact(result))) {
                consumer.accept(valueInvoker.invokeExact(result));
            }
        }
        catch (RuntimeException r) {
            throw r;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    static {
        ITERATOR_INVOKER_KEY = new Object();
        NEXT_INVOKER_KEY = new Object();
        DONE_INVOKER_KEY = new Object();
        VALUE_INVOKER_KEY = new Object();
        AbstractIterator.$clinit$();
    }

    public static void $clinit$() {
        $nasgenmap$ = PropertyMap.newMap(Collections.EMPTY_LIST);
    }

    static enum IterationKind {
        KEY,
        VALUE,
        KEY_VALUE;

    }
}
