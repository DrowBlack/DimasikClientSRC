package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.openjdk.nashorn.internal.objects.NativeDataView;
import org.openjdk.nashorn.internal.objects.NativeDataView$Prototype;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.Specialization;

final class NativeDataView$Constructor
extends ScriptFunction {
    NativeDataView$Constructor() {
        super("DataView", cfr_ldc_0(), new Specialization[]{new Specialization(cfr_ldc_1(), false, false), new Specialization(cfr_ldc_2(), false, false)});
        NativeDataView$Prototype nativeDataView$Prototype = new NativeDataView$Prototype();
        PrototypeObject.setConstructor(nativeDataView$Prototype, this);
        this.setPrototype(nativeDataView$Prototype);
        this.setArity(1);
        this.setDocumentationKey("DataView");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeDataView.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;[Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeDataView;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_1() {
        try {
            return MethodHandles.lookup().findStatic(NativeDataView.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;I)Lorg/openjdk/nashorn/internal/objects/NativeDataView;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_2() {
        try {
            return MethodHandles.lookup().findStatic(NativeDataView.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;II)Lorg/openjdk/nashorn/internal/objects/NativeDataView;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
