package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeWeakMap;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeWeakMap$Prototype
extends PrototypeObject {
    private Object set;
    private Object get;
    private Object delete;
    private Object has;
    private static final PropertyMap $nasgenmap$;

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

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(4);
        arrayList.add(AccessorProperty.create("set", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("get", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create("delete", 2, cfr_ldc_4(), cfr_ldc_5()));
        arrayList.add(AccessorProperty.create("has", 2, cfr_ldc_6(), cfr_ldc_7()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeWeakMap$Prototype() {
        super($nasgenmap$);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("set", cfr_ldc_8());
        scriptFunction.setDocumentationKey("WeakMap.prototype.set");
        this.set = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("get", cfr_ldc_9());
        scriptFunction2.setDocumentationKey("WeakMap.prototype.get");
        this.get = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("delete", cfr_ldc_10());
        scriptFunction3.setDocumentationKey("WeakMap.prototype.delete");
        this.delete = scriptFunction3;
        ScriptFunction scriptFunction4 = ScriptFunction.createBuiltin("has", cfr_ldc_11());
        scriptFunction4.setDocumentationKey("WeakMap.prototype.has");
        this.has = scriptFunction4;
    }

    @Override
    public String getClassName() {
        return "WeakMap";
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeWeakMap$Prototype.class, "G$set", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeWeakMap$Prototype.class, "S$set", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeWeakMap$Prototype.class, "G$get", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeWeakMap$Prototype.class, "S$get", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeWeakMap$Prototype.class, "G$delete", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeWeakMap$Prototype.class, "S$delete", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeWeakMap$Prototype.class, "G$has", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeWeakMap$Prototype.class, "S$has", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeWeakMap.class, "set", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeWeakMap.class, "get", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeWeakMap.class, "delete", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
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
            return MethodHandles.lookup().findStatic(NativeWeakMap.class, "has", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
