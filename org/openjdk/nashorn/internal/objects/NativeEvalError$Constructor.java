package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeEvalError;
import org.openjdk.nashorn.internal.objects.NativeEvalError$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeEvalError$Constructor
extends ScriptFunction {
    NativeEvalError$Constructor() {
        super("EvalError", cfr_ldc_0(), null);
        NativeEvalError$Prototype nativeEvalError$Prototype = new NativeEvalError$Prototype();
        PrototypeObject.setConstructor(nativeEvalError$Prototype, this);
        this.setPrototype(nativeEvalError$Prototype);
        this.setDocumentationKey("Error");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeEvalError.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeEvalError;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
