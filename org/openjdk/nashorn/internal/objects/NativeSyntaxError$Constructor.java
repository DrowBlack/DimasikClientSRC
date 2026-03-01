package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeSyntaxError;
import org.openjdk.nashorn.internal.objects.NativeSyntaxError$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeSyntaxError$Constructor
extends ScriptFunction {
    NativeSyntaxError$Constructor() {
        super("SyntaxError", cfr_ldc_0(), null);
        NativeSyntaxError$Prototype nativeSyntaxError$Prototype = new NativeSyntaxError$Prototype();
        PrototypeObject.setConstructor(nativeSyntaxError$Prototype, this);
        this.setPrototype(nativeSyntaxError$Prototype);
        this.setDocumentationKey("Error");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeSyntaxError.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeSyntaxError;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
