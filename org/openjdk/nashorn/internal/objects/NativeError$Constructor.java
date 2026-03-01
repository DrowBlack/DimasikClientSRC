package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeError;
import org.openjdk.nashorn.internal.objects.NativeError$Prototype;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeError$Constructor
extends ScriptFunction {
    private Object captureStackTrace;
    private Object dumpStack;
    private static final PropertyMap $nasgenmap$;

    public Object G$captureStackTrace() {
        return this.captureStackTrace;
    }

    public void S$captureStackTrace(Object object) {
        this.captureStackTrace = object;
    }

    public Object G$dumpStack() {
        return this.dumpStack;
    }

    public void S$dumpStack(Object object) {
        this.dumpStack = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(2);
        arrayList.add(AccessorProperty.create("captureStackTrace", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("dumpStack", 2, cfr_ldc_2(), cfr_ldc_3()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeError$Constructor() {
        super("Error", cfr_ldc_4(), $nasgenmap$, null);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("captureStackTrace", cfr_ldc_5());
        scriptFunction.setDocumentationKey("Error.captureStackTrace");
        this.captureStackTrace = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("dumpStack", cfr_ldc_6());
        scriptFunction2.setDocumentationKey("Error.dumpStack");
        this.dumpStack = scriptFunction2;
        NativeError$Prototype nativeError$Prototype = new NativeError$Prototype();
        PrototypeObject.setConstructor(nativeError$Prototype, this);
        this.setPrototype(nativeError$Prototype);
        this.setDocumentationKey("Error");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeError$Constructor.class, "G$captureStackTrace", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeError$Constructor.class, "S$captureStackTrace", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeError$Constructor.class, "G$dumpStack", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeError$Constructor.class, "S$dumpStack", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeError.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeError;", null));
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
            return MethodHandles.lookup().findStatic(NativeError.class, "captureStackTrace", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeError.class, "dumpStack", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
