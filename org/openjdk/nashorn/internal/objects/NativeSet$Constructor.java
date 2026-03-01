package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeSet;
import org.openjdk.nashorn.internal.objects.NativeSet$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeSet$Constructor
extends ScriptFunction {
    NativeSet$Constructor() {
        super("Set", cfr_ldc_0(), null);
        NativeSet$Prototype nativeSet$Prototype = new NativeSet$Prototype();
        PrototypeObject.setConstructor(nativeSet$Prototype, this);
        this.setPrototype(nativeSet$Prototype);
        this.setArity(0);
        this.setDocumentationKey("Set");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeSet.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
