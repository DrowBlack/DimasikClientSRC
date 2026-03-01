package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeNumber;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.Specialization;

final class NativeNumber$Prototype
extends PrototypeObject {
    private Object toFixed;
    private Object toExponential;
    private Object toPrecision;
    private Object toString;
    private Object toLocaleString;
    private Object valueOf;
    private static final PropertyMap $nasgenmap$;

    public Object G$toFixed() {
        return this.toFixed;
    }

    public void S$toFixed(Object object) {
        this.toFixed = object;
    }

    public Object G$toExponential() {
        return this.toExponential;
    }

    public void S$toExponential(Object object) {
        this.toExponential = object;
    }

    public Object G$toPrecision() {
        return this.toPrecision;
    }

    public void S$toPrecision(Object object) {
        this.toPrecision = object;
    }

    public Object G$toString() {
        return this.toString;
    }

    public void S$toString(Object object) {
        this.toString = object;
    }

    public Object G$toLocaleString() {
        return this.toLocaleString;
    }

    public void S$toLocaleString(Object object) {
        this.toLocaleString = object;
    }

    public Object G$valueOf() {
        return this.valueOf;
    }

    public void S$valueOf(Object object) {
        this.valueOf = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(6);
        arrayList.add(AccessorProperty.create("toFixed", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("toExponential", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create("toPrecision", 2, cfr_ldc_4(), cfr_ldc_5()));
        arrayList.add(AccessorProperty.create("toString", 2, cfr_ldc_6(), cfr_ldc_7()));
        arrayList.add(AccessorProperty.create("toLocaleString", 2, cfr_ldc_8(), cfr_ldc_9()));
        arrayList.add(AccessorProperty.create("valueOf", 2, cfr_ldc_10(), cfr_ldc_11()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeNumber$Prototype() {
        super($nasgenmap$);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("toFixed", cfr_ldc_12(), new Specialization[]{new Specialization(cfr_ldc_13(), false, false)});
        scriptFunction.setDocumentationKey("Number.prototype.toFixed");
        this.toFixed = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("toExponential", cfr_ldc_14());
        scriptFunction2.setDocumentationKey("Number.prototype.toExponential");
        this.toExponential = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("toPrecision", cfr_ldc_15(), new Specialization[]{new Specialization(cfr_ldc_16(), false, false)});
        scriptFunction3.setDocumentationKey("Number.prototype.toPrecision");
        this.toPrecision = scriptFunction3;
        ScriptFunction scriptFunction4 = ScriptFunction.createBuiltin("toString", cfr_ldc_17());
        scriptFunction4.setDocumentationKey("Number.prototype.toString");
        this.toString = scriptFunction4;
        ScriptFunction scriptFunction5 = ScriptFunction.createBuiltin("toLocaleString", cfr_ldc_18());
        scriptFunction5.setDocumentationKey("Number.prototype.toLocaleString");
        this.toLocaleString = scriptFunction5;
        ScriptFunction scriptFunction6 = ScriptFunction.createBuiltin("valueOf", cfr_ldc_19());
        scriptFunction6.setDocumentationKey("Number.prototype.valueOf");
        this.valueOf = scriptFunction6;
    }

    @Override
    public String getClassName() {
        return "Number";
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "G$toFixed", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "S$toFixed", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "G$toExponential", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "S$toExponential", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "G$toPrecision", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "S$toPrecision", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "G$toString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "S$toString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "G$toLocaleString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_9() {
        try {
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "S$toLocaleString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_10() {
        try {
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "G$valueOf", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_11() {
        try {
            return MethodHandles.lookup().findVirtual(NativeNumber$Prototype.class, "S$valueOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_12() {
        try {
            return MethodHandles.lookup().findStatic(NativeNumber.class, "toFixed", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_13() {
        try {
            return MethodHandles.lookup().findStatic(NativeNumber.class, "toFixed", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;I)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_14() {
        try {
            return MethodHandles.lookup().findStatic(NativeNumber.class, "toExponential", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_15() {
        try {
            return MethodHandles.lookup().findStatic(NativeNumber.class, "toPrecision", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_16() {
        try {
            return MethodHandles.lookup().findStatic(NativeNumber.class, "toPrecision", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;I)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_17() {
        try {
            return MethodHandles.lookup().findStatic(NativeNumber.class, "toString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_18() {
        try {
            return MethodHandles.lookup().findStatic(NativeNumber.class, "toLocaleString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_19() {
        try {
            return MethodHandles.lookup().findStatic(NativeNumber.class, "valueOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
