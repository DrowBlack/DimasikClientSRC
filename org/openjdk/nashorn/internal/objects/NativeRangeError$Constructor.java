package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeRangeError;
import org.openjdk.nashorn.internal.objects.NativeRangeError$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeRangeError$Constructor
extends ScriptFunction {
    NativeRangeError$Constructor() {
        super("RangeError", cfr_ldc_0(), null);
        NativeRangeError$Prototype nativeRangeError$Prototype = new NativeRangeError$Prototype();
        PrototypeObject.setConstructor(nativeRangeError$Prototype, this);
        this.setPrototype(nativeRangeError$Prototype);
        this.setDocumentationKey("Error");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeRangeError.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeRangeError;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
