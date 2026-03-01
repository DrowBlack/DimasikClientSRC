package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeFloat64Array;
import org.openjdk.nashorn.internal.objects.NativeFloat64Array$Prototype;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeFloat64Array$Constructor
extends ScriptFunction {
    private static final PropertyMap $nasgenmap$;

    public int G$BYTES_PER_ELEMENT() {
        return NativeFloat64Array.BYTES_PER_ELEMENT;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(1);
        arrayList.add(AccessorProperty.create("BYTES_PER_ELEMENT", 7, cfr_ldc_0(), null));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeFloat64Array$Constructor() {
        super("Float64Array", cfr_ldc_1(), $nasgenmap$, null);
        NativeFloat64Array$Prototype nativeFloat64Array$Prototype = new NativeFloat64Array$Prototype();
        PrototypeObject.setConstructor(nativeFloat64Array$Prototype, this);
        this.setPrototype(nativeFloat64Array$Prototype);
        this.setArity(1);
        this.setDocumentationKey("Float64Array");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeFloat64Array$Constructor.class, "G$BYTES_PER_ELEMENT", MethodType.fromMethodDescriptorString("()I", null));
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
            return MethodHandles.lookup().findStatic(NativeFloat64Array.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;[Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeFloat64Array;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
