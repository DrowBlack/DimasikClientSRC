package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeSet;
import org.openjdk.nashorn.internal.objects.NativeSymbol;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeSet$Prototype
extends PrototypeObject {
    private Object add;
    private Object has;
    private Object clear;
    private Object delete;
    private Object entries;
    private Object keys;
    private Object values;
    private Object getIterator;
    private Object forEach;
    private static final PropertyMap $nasgenmap$;

    public Object G$add() {
        return this.add;
    }

    public void S$add(Object object) {
        this.add = object;
    }

    public Object G$has() {
        return this.has;
    }

    public void S$has(Object object) {
        this.has = object;
    }

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
        ArrayList<Property> arrayList = new ArrayList<Property>(10);
        arrayList.add(AccessorProperty.create("add", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("has", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create("clear", 2, cfr_ldc_4(), cfr_ldc_5()));
        arrayList.add(AccessorProperty.create("delete", 2, cfr_ldc_6(), cfr_ldc_7()));
        arrayList.add(AccessorProperty.create("size", 4098, cfr_ldc_8(), null));
        arrayList.add(AccessorProperty.create("entries", 2, cfr_ldc_9(), cfr_ldc_10()));
        arrayList.add(AccessorProperty.create("keys", 2, cfr_ldc_11(), cfr_ldc_12()));
        arrayList.add(AccessorProperty.create("values", 2, cfr_ldc_13(), cfr_ldc_14()));
        arrayList.add(AccessorProperty.create(NativeSymbol.iterator, 2, cfr_ldc_15(), cfr_ldc_16()));
        arrayList.add(AccessorProperty.create("forEach", 2, cfr_ldc_17(), cfr_ldc_18()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeSet$Prototype() {
        super($nasgenmap$);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("add", cfr_ldc_19());
        scriptFunction.setDocumentationKey("Set.prototype.add");
        this.add = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("has", cfr_ldc_20());
        scriptFunction2.setDocumentationKey("Set.prototype.has");
        this.has = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("clear", cfr_ldc_21());
        scriptFunction3.setDocumentationKey("Set.prototype.clear");
        this.clear = scriptFunction3;
        ScriptFunction scriptFunction4 = ScriptFunction.createBuiltin("delete", cfr_ldc_22());
        scriptFunction4.setDocumentationKey("Set.prototype.delete");
        this.delete = scriptFunction4;
        ScriptFunction scriptFunction5 = ScriptFunction.createBuiltin("entries", cfr_ldc_23());
        scriptFunction5.setDocumentationKey("Set.prototype.entries");
        this.entries = scriptFunction5;
        ScriptFunction scriptFunction6 = ScriptFunction.createBuiltin("keys", cfr_ldc_24());
        scriptFunction6.setDocumentationKey("Set.prototype.keys");
        this.keys = scriptFunction6;
        ScriptFunction scriptFunction7 = ScriptFunction.createBuiltin("values", cfr_ldc_25());
        scriptFunction7.setDocumentationKey("Set.prototype.values");
        this.values = scriptFunction7;
        ScriptFunction scriptFunction8 = ScriptFunction.createBuiltin("Symbol[iterator]", cfr_ldc_26());
        scriptFunction8.setDocumentationKey("Set.prototype.@@iterator");
        this.getIterator = scriptFunction8;
        ScriptFunction scriptFunction9 = ScriptFunction.createBuiltin("forEach", cfr_ldc_27());
        scriptFunction9.setArity(1);
        scriptFunction9.setDocumentationKey("Set.prototype.forEach");
        this.forEach = scriptFunction9;
    }

    @Override
    public String getClassName() {
        return "Set";
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "G$add", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "S$add", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "G$has", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "S$has", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "G$clear", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "S$clear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "G$delete", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "S$delete", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "size", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)I", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "G$entries", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "S$entries", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "G$keys", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "S$keys", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "G$values", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "S$values", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "G$getIterator", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "S$getIterator", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "G$forEach", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeSet$Prototype.class, "S$forEach", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "add", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "has", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "clear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "delete", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "entries", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "keys", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "values", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "getIterator", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeSet.class, "forEach", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
