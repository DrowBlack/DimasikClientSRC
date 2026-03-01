package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeWeakSet;
import org.openjdk.nashorn.internal.objects.NativeWeakSet$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeWeakSet$Constructor
extends ScriptFunction {
    NativeWeakSet$Constructor() {
        super("WeakSet", cfr_ldc_0(), null);
        NativeWeakSet$Prototype nativeWeakSet$Prototype = new NativeWeakSet$Prototype();
        PrototypeObject.setConstructor(nativeWeakSet$Prototype, this);
        this.setPrototype(nativeWeakSet$Prototype);
        this.setArity(0);
        this.setDocumentationKey("WeakSet");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeWeakSet.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
