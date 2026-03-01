package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeFunction;
import org.openjdk.nashorn.internal.objects.NativeFunction$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeFunction$Constructor
extends ScriptFunction {
    NativeFunction$Constructor() {
        super("Function", cfr_ldc_0(), null);
        NativeFunction$Prototype nativeFunction$Prototype = new NativeFunction$Prototype();
        PrototypeObject.setConstructor(nativeFunction$Prototype, this);
        this.setPrototype(nativeFunction$Prototype);
        this.setArity(1);
        this.setDocumentationKey("Function");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeFunction.class, "function", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;[Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/runtime/ScriptFunction;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
