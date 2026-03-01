package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeJavaImporter;
import org.openjdk.nashorn.internal.objects.NativeJavaImporter$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeJavaImporter$Constructor
extends ScriptFunction {
    NativeJavaImporter$Constructor() {
        super("JavaImporter", cfr_ldc_0(), null);
        NativeJavaImporter$Prototype nativeJavaImporter$Prototype = new NativeJavaImporter$Prototype();
        PrototypeObject.setConstructor(nativeJavaImporter$Prototype, this);
        this.setPrototype(nativeJavaImporter$Prototype);
        this.setArity(1);
        this.setDocumentationKey("JavaImporter");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeJavaImporter.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;[Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeJavaImporter;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
