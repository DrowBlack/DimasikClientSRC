package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeURIError;
import org.openjdk.nashorn.internal.objects.NativeURIError$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeURIError$Constructor
extends ScriptFunction {
    NativeURIError$Constructor() {
        super("URIError", cfr_ldc_0(), null);
        NativeURIError$Prototype nativeURIError$Prototype = new NativeURIError$Prototype();
        PrototypeObject.setConstructor(nativeURIError$Prototype, this);
        this.setPrototype(nativeURIError$Prototype);
        this.setDocumentationKey("Error");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeURIError.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeURIError;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
