package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.LinkRequest;
import org.openjdk.nashorn.internal.WeakValueCache;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Symbol;
import org.openjdk.nashorn.internal.runtime.Undefined;
import org.openjdk.nashorn.internal.runtime.linker.PrimitiveLookup;

public final class NativeSymbol
extends ScriptObject {
    private final Symbol symbol;
    static final MethodHandle WRAPFILTER = NativeSymbol.findOwnMH("wrapFilter", Lookup.MH.type(NativeSymbol.class, Object.class));
    private static final MethodHandle PROTOFILTER = NativeSymbol.findOwnMH("protoFilter", Lookup.MH.type(Object.class, Object.class));
    private static PropertyMap $nasgenmap$;
    private static final WeakValueCache<String, Symbol> globalSymbolRegistry;
    public static final Symbol iterator;

    NativeSymbol(Symbol symbol) {
        this(symbol, Global.instance());
    }

    NativeSymbol(Symbol symbol, Global global) {
        this(symbol, global.getSymbolPrototype(), $nasgenmap$);
    }

    private NativeSymbol(Symbol symbol, ScriptObject prototype, PropertyMap map) {
        super(prototype, map);
        this.symbol = symbol;
    }

    private static Symbol getSymbolValue(Object self) {
        if (self instanceof Symbol) {
            return (Symbol)self;
        }
        if (self instanceof NativeSymbol) {
            return ((NativeSymbol)self).symbol;
        }
        throw ECMAErrors.typeError("not.a.symbol", ScriptRuntime.safeToString(self));
    }

    public static GuardedInvocation lookupPrimitive(LinkRequest request, Object receiver) {
        return PrimitiveLookup.lookupPrimitive(request, Symbol.class, (ScriptObject)new NativeSymbol((Symbol)receiver), WRAPFILTER, PROTOFILTER);
    }

    @Override
    public Object getDefaultValue(Class<?> typeHint) {
        return this.symbol;
    }

    public static String toString(Object self) {
        return NativeSymbol.getSymbolValue(self).toString();
    }

    public static Object valueOf(Object self) {
        return NativeSymbol.getSymbolValue(self);
    }

    public static Object constructor(boolean newObj, Object self, Object ... args) {
        if (newObj) {
            throw ECMAErrors.typeError("symbol.as.constructor", new String[0]);
        }
        String description = args.length > 0 && args[0] != Undefined.getUndefined() ? JSType.toString(args[0]) : "";
        return new Symbol(description);
    }

    public static synchronized Object _for(Object self, Object arg) {
        String name = JSType.toString(arg);
        return globalSymbolRegistry.getOrCreate(name, Symbol::new);
    }

    public static synchronized Object keyFor(Object self, Object arg) {
        if (!(arg instanceof Symbol)) {
            throw ECMAErrors.typeError("not.a.symbol", ScriptRuntime.safeToString(arg));
        }
        String name = ((Symbol)arg).getName();
        return globalSymbolRegistry.get(name) == arg ? name : Undefined.getUndefined();
    }

    private static NativeSymbol wrapFilter(Object receiver) {
        return new NativeSymbol((Symbol)receiver);
    }

    private static Object protoFilter(Object object) {
        return Global.instance().getSymbolPrototype();
    }

    private static MethodHandle findOwnMH(String name, MethodType type) {
        return Lookup.MH.findStatic(MethodHandles.lookup(), NativeSymbol.class, name, type);
    }

    static {
        globalSymbolRegistry = new WeakValueCache();
        iterator = new Symbol("Symbol.iterator");
        NativeSymbol.$clinit$();
    }

    public static void $clinit$() {
        $nasgenmap$ = PropertyMap.newMap(Collections.EMPTY_LIST);
    }
}
