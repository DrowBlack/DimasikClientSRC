package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.util.Collections;
import org.openjdk.nashorn.internal.objects.AbstractIterator;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.LinkedMap;
import org.openjdk.nashorn.internal.objects.NativeMap;
import org.openjdk.nashorn.internal.objects.SetIterator;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Undefined;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;

public class NativeSet
extends ScriptObject {
    private final LinkedMap map = new LinkedMap();
    private static final Object FOREACH_INVOKER_KEY = new Object();
    private static PropertyMap $nasgenmap$;

    private NativeSet(ScriptObject proto, PropertyMap map) {
        super(proto, map);
    }

    public static Object construct(boolean isNew, Object self, Object arg) {
        if (!isNew) {
            throw ECMAErrors.typeError("constructor.requires.new", "Set");
        }
        Global global = Global.instance();
        NativeSet set = new NativeSet(global.getSetPrototype(), $nasgenmap$);
        NativeSet.populateSet(set.getJavaMap(), arg, global);
        return set;
    }

    public static Object add(Object self, Object value) {
        NativeSet.getNativeSet((Object)self).map.set(NativeMap.convertKey(value), null);
        return self;
    }

    public static boolean has(Object self, Object value) {
        return NativeSet.getNativeSet((Object)self).map.has(NativeMap.convertKey(value));
    }

    public static void clear(Object self) {
        NativeSet.getNativeSet((Object)self).map.clear();
    }

    public static boolean delete(Object self, Object value) {
        return NativeSet.getNativeSet((Object)self).map.delete(NativeMap.convertKey(value));
    }

    public static int size(Object self) {
        return NativeSet.getNativeSet((Object)self).map.size();
    }

    public static Object entries(Object self) {
        return new SetIterator(NativeSet.getNativeSet(self), AbstractIterator.IterationKind.KEY_VALUE, Global.instance());
    }

    public static Object keys(Object self) {
        return new SetIterator(NativeSet.getNativeSet(self), AbstractIterator.IterationKind.KEY, Global.instance());
    }

    public static Object values(Object self) {
        return new SetIterator(NativeSet.getNativeSet(self), AbstractIterator.IterationKind.VALUE, Global.instance());
    }

    public static Object getIterator(Object self) {
        return new SetIterator(NativeSet.getNativeSet(self), AbstractIterator.IterationKind.VALUE, Global.instance());
    }

    public static void forEach(Object self, Object callbackFn, Object thisArg) {
        LinkedMap.Node node;
        NativeSet set = NativeSet.getNativeSet(self);
        if (!Bootstrap.isCallable(callbackFn)) {
            throw ECMAErrors.typeError("not.a.function", ScriptRuntime.safeToString(callbackFn));
        }
        MethodHandle invoker = Global.instance().getDynamicInvoker(FOREACH_INVOKER_KEY, () -> Bootstrap.createDynamicCallInvoker(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class));
        LinkedMap.LinkedMapIterator iterator = set.getJavaMap().getIterator();
        while ((node = iterator.next()) != null) {
            try {
                Object object = invoker.invokeExact(callbackFn, thisArg, node.getKey(), node.getKey(), self);
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    @Override
    public String getClassName() {
        return "Set";
    }

    static void populateSet(LinkedMap map, Object arg, Global global) {
        if (arg != null && arg != Undefined.getUndefined()) {
            AbstractIterator.iterate(arg, global, value -> map.set(NativeMap.convertKey(value), null));
        }
    }

    LinkedMap getJavaMap() {
        return this.map;
    }

    private static NativeSet getNativeSet(Object self) {
        if (self instanceof NativeSet) {
            return (NativeSet)self;
        }
        throw ECMAErrors.typeError("not.a.set", ScriptRuntime.safeToString(self));
    }

    static {
        NativeSet.$clinit$();
    }

    public static void $clinit$() {
        $nasgenmap$ = PropertyMap.newMap(Collections.EMPTY_LIST);
    }
}
