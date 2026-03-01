package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeObject;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeObject$Prototype
extends PrototypeObject {
    private Object toString;
    private Object toLocaleString;
    private Object valueOf;
    private Object hasOwnProperty;
    private Object isPrototypeOf;
    private Object propertyIsEnumerable;
    private static final PropertyMap $nasgenmap$;

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

    public Object G$hasOwnProperty() {
        return this.hasOwnProperty;
    }

    public void S$hasOwnProperty(Object object) {
        this.hasOwnProperty = object;
    }

    public Object G$isPrototypeOf() {
        return this.isPrototypeOf;
    }

    public void S$isPrototypeOf(Object object) {
        this.isPrototypeOf = object;
    }

    public Object G$propertyIsEnumerable() {
        return this.propertyIsEnumerable;
    }

    public void S$propertyIsEnumerable(Object object) {
        this.propertyIsEnumerable = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(6);
        arrayList.add(AccessorProperty.create("toString", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("toLocaleString", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create("valueOf", 2, cfr_ldc_4(), cfr_ldc_5()));
        arrayList.add(AccessorProperty.create("hasOwnProperty", 2, cfr_ldc_6(), cfr_ldc_7()));
        arrayList.add(AccessorProperty.create("isPrototypeOf", 2, cfr_ldc_8(), cfr_ldc_9()));
        arrayList.add(AccessorProperty.create("propertyIsEnumerable", 2, cfr_ldc_10(), cfr_ldc_11()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeObject$Prototype() {
        super($nasgenmap$);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("toString", cfr_ldc_12());
        scriptFunction.setDocumentationKey("Object.prototype.toString");
        this.toString = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("toLocaleString", cfr_ldc_13());
        scriptFunction2.setDocumentationKey("Object.prototype.toLocaleString");
        this.toLocaleString = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("valueOf", cfr_ldc_14());
        scriptFunction3.setDocumentationKey("Object.prototype.valueOf");
        this.valueOf = scriptFunction3;
        ScriptFunction scriptFunction4 = ScriptFunction.createBuiltin("hasOwnProperty", cfr_ldc_15());
        scriptFunction4.setDocumentationKey("Object.prototype.hasOwnProperty");
        this.hasOwnProperty = scriptFunction4;
        ScriptFunction scriptFunction5 = ScriptFunction.createBuiltin("isPrototypeOf", cfr_ldc_16());
        scriptFunction5.setDocumentationKey("Object.prototype.isPrototypeOf");
        this.isPrototypeOf = scriptFunction5;
        ScriptFunction scriptFunction6 = ScriptFunction.createBuiltin("propertyIsEnumerable", cfr_ldc_17());
        scriptFunction6.setDocumentationKey("Object.prototype.propertyIsEnumerable");
        this.propertyIsEnumerable = scriptFunction6;
    }

    @Override
    public String getClassName() {
        return "Object";
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "G$toString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "S$toString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "G$toLocaleString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "S$toLocaleString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "G$valueOf", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "S$valueOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "G$hasOwnProperty", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "S$hasOwnProperty", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "G$isPrototypeOf", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "S$isPrototypeOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "G$propertyIsEnumerable", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Prototype.class, "S$propertyIsEnumerable", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "toString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "toLocaleString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "valueOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "hasOwnProperty", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "isPrototypeOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "propertyIsEnumerable", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
