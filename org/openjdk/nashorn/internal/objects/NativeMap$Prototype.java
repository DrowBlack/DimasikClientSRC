package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeMap;
import org.openjdk.nashorn.internal.objects.NativeSymbol;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeMap$Prototype
extends PrototypeObject {
    private Object clear;
    private Object delete;
    private Object has;
    private Object set;
    private Object get;
    private Object entries;
    private Object keys;
    private Object values;
    private Object getIterator;
    private Object forEach;
    private static final PropertyMap $nasgenmap$;

    public Object G$clear() {
        return this.clear;
    }

    public void S$clear(Object object) {
        this.clear = object;
    }

    public Object G$delete() {
        return this.delete;
    }

    public void S$delete(Object object) {
        this.delete = object;
    }

    public Object G$has() {
        return this.has;
    }

    public void S$has(Object object) {
        this.has = object;
    }

    public Object G$set() {
        return this.set;
    }

    public void S$set(Object object) {
        this.set = object;
    }

    public Object G$get() {
        return this.get;
    }

    public void S$get(Object object) {
        this.get = object;
    }

    public Object G$entries() {
        return this.entries;
    }

    public void S$entries(Object object) {
        this.entries = object;
    }

    public Object G$keys() {
        return this.keys;
    }

    public void S$keys(Object object) {
        this.keys = object;
    }

    public Object G$values() {
        return this.values;
    }

    public void S$values(Object object) {
        this.values = object;
    }

    public Object G$getIterator() {
        return this.getIterator;
    }

    public void S$getIterator(Object object) {
        this.getIterator = object;
    }

    public Object G$forEach() {
        return this.forEach;
    }

    public void S$forEach(Object object) {
        this.forEach = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(11);
        arrayList.add(AccessorProperty.create("clear", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("delete", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create("has", 2, cfr_ldc_4(), cfr_ldc_5()));
        arrayList.add(AccessorProperty.create("set", 2, cfr_ldc_6(), cfr_ldc_7()));
        arrayList.add(AccessorProperty.create("get", 2, cfr_ldc_8(), cfr_ldc_9()));
        arrayList.add(AccessorProperty.create("size", 4098, cfr_ldc_10(), null));
        arrayList.add(AccessorProperty.create("entries", 2, cfr_ldc_11(), cfr_ldc_12()));
        arrayList.add(AccessorProperty.create("keys", 2, cfr_ldc_13(), cfr_ldc_14()));
        arrayList.add(AccessorProperty.create("values", 2, cfr_ldc_15(), cfr_ldc_16()));
        arrayList.add(AccessorProperty.create(NativeSymbol.iterator, 2, cfr_ldc_17(), cfr_ldc_18()));
        arrayList.add(AccessorProperty.create("forEach", 2, cfr_ldc_19(), cfr_ldc_20()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeMap$Prototype() {
        super($nasgenmap$);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("clear", cfr_ldc_21());
        scriptFunction.setDocumentationKey("Map.prototype.clear");
        this.clear = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("delete", cfr_ldc_22());
        scriptFunction2.setDocumentationKey("Map.prototype.delete");
        this.delete = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("has", cfr_ldc_23());
        scriptFunction3.setDocumentationKey("Map.prototype.has");
        this.has = scriptFunction3;
        ScriptFunction scriptFunction4 = ScriptFunction.createBuiltin("set", cfr_ldc_24());
        scriptFunction4.setDocumentationKey("Map.prototype.set");
        this.set = scriptFunction4;
        ScriptFunction scriptFunction5 = ScriptFunction.createBuiltin("get", cfr_ldc_25());
        scriptFunction5.setDocumentationKey("Map.prototype.get");
        this.get = scriptFunction5;
        ScriptFunction scriptFunction6 = ScriptFunction.createBuiltin("entries", cfr_ldc_26());
        scriptFunction6.setDocumentationKey("Map.prototype.entries");
        this.entries = scriptFunction6;
        ScriptFunction scriptFunction7 = ScriptFunction.createBuiltin("keys", cfr_ldc_27());
        scriptFunction7.setDocumentationKey("Map.prototype.keys");
        this.keys = scriptFunction7;
        ScriptFunction scriptFunction8 = ScriptFunction.createBuiltin("values", cfr_ldc_28());
        scriptFunction8.setDocumentationKey("Map.prototype.values");
        this.values = scriptFunction8;
        ScriptFunction scriptFunction9 = ScriptFunction.createBuiltin("Symbol[iterator]", cfr_ldc_29());
        scriptFunction9.setDocumentationKey("Map.prototype.@@iterator");
        this.getIterator = scriptFunction9;
        ScriptFunction scriptFunction10 = ScriptFunction.createBuiltin("forEach", cfr_ldc_30());
        scriptFunction10.setArity(1);
        scriptFunction10.setDocumentationKey("Map.prototype.forEach");
        this.forEach = scriptFunction10;
    }

    @Override
    public String getClassName() {
        return "Map";
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$clear", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$clear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$delete", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$delete", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$has", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$has", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$set", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$set", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$get", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$get", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "size", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)I", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$entries", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$entries", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$keys", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$keys", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$values", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$values", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$getIterator", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$getIterator", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "G$forEach", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeMap$Prototype.class, "S$forEach", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "clear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "delete", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "has", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "set", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "get", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "entries", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "keys", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "values", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "getIterator", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeMap.class, "forEach", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
