package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeMap;
import org.openjdk.nashorn.internal.objects.NativeMap$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeMap$Constructor
extends ScriptFunction {
    NativeMap$Constructor() {
        super("Map", cfr_ldc_0(), null);
        NativeMap$Prototype nativeMap$Prototype = new NativeMap$Prototype();
        PrototypeObject.setConstructor(nativeMap$Prototype, this);
        this.setPrototype(nativeMap$Prototype);
        this.setArity(0);
        this.setDocumentationKey("Map");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeMap.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
