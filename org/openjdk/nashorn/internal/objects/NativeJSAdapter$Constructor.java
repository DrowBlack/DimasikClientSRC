package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeJSAdapter;
import org.openjdk.nashorn.internal.objects.NativeJSAdapter$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeJSAdapter$Constructor
extends ScriptFunction {
    NativeJSAdapter$Constructor() {
        super("JSAdapter", cfr_ldc_0(), null);
        NativeJSAdapter$Prototype nativeJSAdapter$Prototype = new NativeJSAdapter$Prototype();
        PrototypeObject.setConstructor(nativeJSAdapter$Prototype, this);
        this.setPrototype(nativeJSAdapter$Prototype);
        this.setDocumentationKey("JSAdapter");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeJSAdapter.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;[Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeJSAdapter;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
