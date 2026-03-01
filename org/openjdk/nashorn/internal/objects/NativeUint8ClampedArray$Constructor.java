package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeUint8ClampedArray;
import org.openjdk.nashorn.internal.objects.NativeUint8ClampedArray$Prototype;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeUint8ClampedArray$Constructor
extends ScriptFunction {
    private static final PropertyMap $nasgenmap$;

    public int G$BYTES_PER_ELEMENT() {
        return NativeUint8ClampedArray.BYTES_PER_ELEMENT;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(1);
        arrayList.add(AccessorProperty.create("BYTES_PER_ELEMENT", 7, cfr_ldc_0(), null));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeUint8ClampedArray$Constructor() {
        super("Uint8ClampedArray", cfr_ldc_1(), $nasgenmap$, null);
        NativeUint8ClampedArray$Prototype nativeUint8ClampedArray$Prototype = new NativeUint8ClampedArray$Prototype();
        PrototypeObject.setConstructor(nativeUint8ClampedArray$Prototype, this);
        this.setPrototype(nativeUint8ClampedArray$Prototype);
        this.setArity(1);
        this.setDocumentationKey("Uint8ClampedArray");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeUint8ClampedArray$Constructor.class, "G$BYTES_PER_ELEMENT", MethodType.fromMethodDescriptorString("()I", null));
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
            return MethodHandles.lookup().findStatic(NativeUint8ClampedArray.class, "constructor", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;[Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeUint8ClampedArray;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
