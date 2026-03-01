package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeReferenceError;
import org.openjdk.nashorn.internal.objects.NativeReferenceError$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeReferenceError$Constructor
extends ScriptFunction {
    NativeReferenceError$Constructor() {
        super("ReferenceError", cfr_ldc_0(), null);
        NativeReferenceError$Prototype nativeReferenceError$Prototype = new NativeReferenceError$Prototype();
        PrototypeObject.setConstructor(nativeReferenceError$Prototype, this);
        this.setPrototype(nativeReferenceError$Prototype);
        this.setDocumentationKey("Error");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeReferenceError.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeReferenceError;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
