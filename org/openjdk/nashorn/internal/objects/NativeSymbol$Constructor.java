package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeSymbol;
import org.openjdk.nashorn.internal.objects.NativeSymbol$Prototype;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeSymbol$Constructor
extends ScriptFunction {
    private Object _for;
    private Object keyFor;
    private static final PropertyMap $nasgenmap$;

    public Object G$iterator() {
        return NativeSymbol.iterator;
    }

    public Object G$_for() {
        return this._for;
    }

    public void S$_for(Object object) {
        this._for = object;
    }

    public Object G$keyFor() {
        return this.keyFor;
    }

    public void S$keyFor(Object object) {
        this.keyFor = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(3);
        arrayList.add(AccessorProperty.create("iterator", 7, cfr_ldc_0(), null));
        arrayList.add(AccessorProperty.create("for", 2, cfr_ldc_1(), cfr_ldc_2()));
        arrayList.add(AccessorProperty.create("keyFor", 2, cfr_ldc_3(), cfr_ldc_4()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeSymbol$Constructor() {
        super("Symbol", cfr_ldc_5(), $nasgenmap$, null);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("for", cfr_ldc_6());
        scriptFunction.setDocumentationKey("Symbol.for");
        this._for = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("keyFor", cfr_ldc_7());
        scriptFunction2.setDocumentationKey("Symbol.keyFor");
        this.keyFor = scriptFunction2;
        NativeSymbol$Prototype nativeSymbol$Prototype = new NativeSymbol$Prototype();
        PrototypeObject.setConstructor(nativeSymbol$Prototype, this);
        this.setPrototype(nativeSymbol$Prototype);
        this.setArity(1);
        this.setDocumentationKey("Symbol");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeSymbol$Constructor.class, "G$iterator", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_1() {
        try {
            return MethodHandles.lookup().findVirtual(NativeSymbol$Constructor.class, "G$_for", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_2() {
        try {
            return MethodHandles.lookup().findVirtual(NativeSymbol$Constructor.class, "S$_for", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_3() {
        try {
            return MethodHandles.lookup().findVirtual(NativeSymbol$Constructor.class, "G$keyFor", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_4() {
        try {
            return MethodHandles.lookup().findVirtual(NativeSymbol$Constructor.class, "S$keyFor", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_5() {
        try {
            return MethodHandles.lookup().findStatic(NativeSymbol.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_6() {
        try {
            return MethodHandles.lookup().findStatic(NativeSymbol.class, "_for", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_7() {
        try {
            return MethodHandles.lookup().findStatic(NativeSymbol.class, "keyFor", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
