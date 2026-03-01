package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeWeakMap;
import org.openjdk.nashorn.internal.objects.NativeWeakMap$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeWeakMap$Constructor
extends ScriptFunction {
    NativeWeakMap$Constructor() {
        super("WeakMap", cfr_ldc_0(), null);
        NativeWeakMap$Prototype nativeWeakMap$Prototype = new NativeWeakMap$Prototype();
        PrototypeObject.setConstructor(nativeWeakMap$Prototype, this);
        this.setPrototype(nativeWeakMap$Prototype);
        this.setArity(0);
        this.setDocumentationKey("WeakMap");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeWeakMap.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
