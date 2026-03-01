package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeJava;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;

final class NativeJava$Constructor
extends ScriptObject {
    private Object isType;
    private Object synchronizedFunc;
    private Object isJavaMethod;
    private Object isJavaFunction;
    private Object isJavaObject;
    private Object isScriptObject;
    private Object isScriptFunction;
    private Object type;
    private Object typeName;
    private Object to;
    private Object from;
    private Object extend;
    private Object _super;
    private Object asJSONCompatible;
    private static final PropertyMap $nasgenmap$;

    public Object G$isType() {
        return this.isType;
    }

    public void S$isType(Object object) {
        this.isType = object;
    }

    public Object G$synchronizedFunc() {
        return this.synchronizedFunc;
    }

    public void S$synchronizedFunc(Object object) {
        this.synchronizedFunc = object;
    }

    public Object G$isJavaMethod() {
        return this.isJavaMethod;
    }

    public void S$isJavaMethod(Object object) {
        this.isJavaMethod = object;
    }

    public Object G$isJavaFunction() {
        return this.isJavaFunction;
    }

    public void S$isJavaFunction(Object object) {
        this.isJavaFunction = object;
    }

    public Object G$isJavaObject() {
        return this.isJavaObject;
    }

    public void S$isJavaObject(Object object) {
        this.isJavaObject = object;
    }

    public Object G$isScriptObject() {
        return this.isScriptObject;
    }

    public void S$isScriptObject(Object object) {
        this.isScriptObject = object;
    }

    public Object G$isScriptFunction() {
        return this.isScriptFunction;
    }

    public void S$isScriptFunction(Object object) {
        this.isScriptFunction = object;
    }

    public Object G$type() {
        return this.type;
    }

    public void S$type(Object object) {
        this.type = object;
    }

    public Object G$typeName() {
        return this.typeName;
    }

    public void S$typeName(Object object) {
        this.typeName = object;
    }

    public Object G$to() {
        return this.to;
    }

    public void S$to(Object object) {
        this.to = object;
    }

    public Object G$from() {
        return this.from;
    }

    public void S$from(Object object) {
        this.from = object;
    }

    public Object G$extend() {
        return this.extend;
    }

    public void S$extend(Object object) {
        this.extend = object;
    }

    public Object G$_super() {
        return this._super;
    }

    public void S$_super(Object object) {
        this._super = object;
    }

    public Object G$asJSONCompatible() {
        return this.asJSONCompatible;
    }

    public void S$asJSONCompatible(Object object) {
        this.asJSONCompatible = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(14);
        arrayList.add(AccessorProperty.create("isType", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("synchronized", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create("isJavaMethod", 2, cfr_ldc_4(), cfr_ldc_5()));
        arrayList.add(AccessorProperty.create("isJavaFunction", 2, cfr_ldc_6(), cfr_ldc_7()));
        arrayList.add(AccessorProperty.create("isJavaObject", 2, cfr_ldc_8(), cfr_ldc_9()));
        arrayList.add(AccessorProperty.create("isScriptObject", 2, cfr_ldc_10(), cfr_ldc_11()));
        arrayList.add(AccessorProperty.create("isScriptFunction", 2, cfr_ldc_12(), cfr_ldc_13()));
        arrayList.add(AccessorProperty.create("type", 2, cfr_ldc_14(), cfr_ldc_15()));
        arrayList.add(AccessorProperty.create("typeName", 2, cfr_ldc_16(), cfr_ldc_17()));
        arrayList.add(AccessorProperty.create("to", 2, cfr_ldc_18(), cfr_ldc_19()));
        arrayList.add(AccessorProperty.create("from", 2, cfr_ldc_20(), cfr_ldc_21()));
        arrayList.add(AccessorProperty.create("extend", 2, cfr_ldc_22(), cfr_ldc_23()));
        arrayList.add(AccessorProperty.create("super", 2, cfr_ldc_24(), cfr_ldc_25()));
        arrayList.add(AccessorProperty.create("asJSONCompatible", 2, cfr_ldc_26(), cfr_ldc_27()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeJava$Constructor() {
        super($nasgenmap$);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("isType", cfr_ldc_28());
        scriptFunction.setDocumentationKey("Java.isType");
        this.isType = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("synchronized", cfr_ldc_29());
        scriptFunction2.setDocumentationKey("Java.synchronized");
        this.synchronizedFunc = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("isJavaMethod", cfr_ldc_30());
        scriptFunction3.setDocumentationKey("Java.isJavaMethod");
        this.isJavaMethod = scriptFunction3;
        ScriptFunction scriptFunction4 = ScriptFunction.createBuiltin("isJavaFunction", cfr_ldc_31());
        scriptFunction4.setDocumentationKey("Java.isJavaFunction");
        this.isJavaFunction = scriptFunction4;
        ScriptFunction scriptFunction5 = ScriptFunction.createBuiltin("isJavaObject", cfr_ldc_32());
        scriptFunction5.setDocumentationKey("Java.isJavaObject");
        this.isJavaObject = scriptFunction5;
        ScriptFunction scriptFunction6 = ScriptFunction.createBuiltin("isScriptObject", cfr_ldc_33());
        scriptFunction6.setDocumentationKey("Java.isScriptObject");
        this.isScriptObject = scriptFunction6;
        ScriptFunction scriptFunction7 = ScriptFunction.createBuiltin("isScriptFunction", cfr_ldc_34());
        scriptFunction7.setDocumentationKey("Java.isScriptFunction");
        this.isScriptFunction = scriptFunction7;
        ScriptFunction scriptFunction8 = ScriptFunction.createBuiltin("type", cfr_ldc_35());
        scriptFunction8.setDocumentationKey("Java.type");
        this.type = scriptFunction8;
        ScriptFunction scriptFunction9 = ScriptFunction.createBuiltin("typeName", cfr_ldc_36());
        scriptFunction9.setDocumentationKey("Java.typeName");
        this.typeName = scriptFunction9;
        ScriptFunction scriptFunction10 = ScriptFunction.createBuiltin("to", cfr_ldc_37());
        scriptFunction10.setDocumentationKey("Java.to");
        this.to = scriptFunction10;
        ScriptFunction scriptFunction11 = ScriptFunction.createBuiltin("from", cfr_ldc_38());
        scriptFunction11.setDocumentationKey("Java.from");
        this.from = scriptFunction11;
        ScriptFunction scriptFunction12 = ScriptFunction.createBuiltin("extend", cfr_ldc_39());
        scriptFunction12.setDocumentationKey("Java.extend");
        this.extend = scriptFunction12;
        ScriptFunction scriptFunction13 = ScriptFunction.createBuiltin("super", cfr_ldc_40());
        scriptFunction13.setDocumentationKey("Java.super");
        this._super = scriptFunction13;
        ScriptFunction scriptFunction14 = ScriptFunction.createBuiltin("asJSONCompatible", cfr_ldc_41());
        scriptFunction14.setDocumentationKey("Java.asJSONCompatible");
        this.asJSONCompatible = scriptFunction14;
    }

    @Override
    public String getClassName() {
        return "Java";
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$isType", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$isType", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$synchronizedFunc", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$synchronizedFunc", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$isJavaMethod", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$isJavaMethod", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$isJavaFunction", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$isJavaFunction", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$isJavaObject", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$isJavaObject", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$isScriptObject", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$isScriptObject", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$isScriptFunction", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$isScriptFunction", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$type", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$type", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$typeName", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$typeName", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$to", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$to", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_20() {
        try {
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$from", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_21() {
        try {
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$from", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_22() {
        try {
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$extend", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_23() {
        try {
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$extend", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_24() {
        try {
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$_super", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_25() {
        try {
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$_super", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_26() {
        try {
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "G$asJSONCompatible", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_27() {
        try {
            return MethodHandles.lookup().findVirtual(NativeJava$Constructor.class, "S$asJSONCompatible", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_28() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "isType", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_29() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "synchronizedFunc", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_30() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "isJavaMethod", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_31() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "isJavaFunction", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_32() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "isJavaObject", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_33() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "isScriptObject", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_34() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "isScriptFunction", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_35() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "type", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_36() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "typeName", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_37() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "to", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_38() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "from", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeArray;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_39() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "extend", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_40() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "_super", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_41() {
        try {
            return MethodHandles.lookup().findStatic(NativeJava.class, "asJSONCompatible", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
