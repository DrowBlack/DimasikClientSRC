package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeTypeError;
import org.openjdk.nashorn.internal.objects.NativeTypeError$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeTypeError$Constructor
extends ScriptFunction {
    NativeTypeError$Constructor() {
        super("TypeError", cfr_ldc_0(), null);
        NativeTypeError$Prototype nativeTypeError$Prototype = new NativeTypeError$Prototype();
        PrototypeObject.setConstructor(nativeTypeError$Prototype, this);
        this.setPrototype(nativeTypeError$Prototype);
        this.setDocumentationKey("Error");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeTypeError.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeTypeError;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
