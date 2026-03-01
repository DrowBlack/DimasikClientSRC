package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeSymbol;
import org.openjdk.nashorn.internal.objects.NativeUint32Array;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeUint32Array$Prototype
extends PrototypeObject {
    private Object set;
    private Object subarray;
    private Object getIterator;
    private static final PropertyMap $nasgenmap$;

    public Object G$set() {
        return this.set;
    }

    public void S$set(Object object) {
        this.set = object;
    }

    public Object G$subarray() {
        return this.subarray;
    }

    public void S$subarray(Object object) {
        this.subarray = object;
    }

    public Object G$getIterator() {
        return this.getIterator;
    }

    public void S$getIterator(Object object) {
        this.getIterator = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(3);
        arrayList.add(AccessorProperty.create("set", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("subarray", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create(NativeSymbol.iterator, 2, cfr_ldc_4(), cfr_ldc_5()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeUint32Array$Prototype() {
        super($nasgenmap$);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("set", cfr_ldc_6());
        scriptFunction.setDocumentationKey("Uint32Array.prototype.set");
        this.set = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("subarray", cfr_ldc_7());
        scriptFunction2.setDocumentationKey("Uint32Array.prototype.subarray");
        this.subarray = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("Symbol[iterator]", cfr_ldc_8());
        scriptFunction3.setDocumentationKey("Uint32Array.prototype.@@iterator");
        this.getIterator = scriptFunction3;
    }

    @Override
    public String getClassName() {
        return "Uint32Array";
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeUint32Array$Prototype.class, "G$set", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeUint32Array$Prototype.class, "S$set", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeUint32Array$Prototype.class, "G$subarray", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_3() {
        try {
            return MethodHandles.lookup().findVirtual(NativeUint32Array$Prototype.class, "S$subarray", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_4() {
        try {
            return MethodHandles.lookup().findVirtual(NativeUint32Array$Prototype.class, "G$getIterator", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_5() {
        try {
            return MethodHandles.lookup().findVirtual(NativeUint32Array$Prototype.class, "S$getIterator", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_6() {
        try {
            return MethodHandles.lookup().findStatic(NativeUint32Array.class, "set", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_7() {
        try {
            return MethodHandles.lookup().findStatic(NativeUint32Array.class, "subarray", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeUint32Array;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_8() {
        try {
            return MethodHandles.lookup().findStatic(NativeUint32Array.class, "getIterator", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
