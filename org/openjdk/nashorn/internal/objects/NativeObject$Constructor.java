package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeObject;
import org.openjdk.nashorn.internal.objects.NativeObject$Prototype;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeObject$Constructor
extends ScriptFunction {
    private Object setIndexedPropertiesToExternalArrayData;
    private Object getPrototypeOf;
    private Object setPrototypeOf;
    private Object getOwnPropertyDescriptor;
    private Object getOwnPropertyNames;
    private Object getOwnPropertySymbols;
    private Object create;
    private Object defineProperty;
    private Object defineProperties;
    private Object seal;
    private Object freeze;
    private Object preventExtensions;
    private Object isSealed;
    private Object isFrozen;
    private Object isExtensible;
    private Object keys;
    private Object bindProperties;
    private static final PropertyMap $nasgenmap$;

    public Object G$setIndexedPropertiesToExternalArrayData() {
        return this.setIndexedPropertiesToExternalArrayData;
    }

    public void S$setIndexedPropertiesToExternalArrayData(Object object) {
        this.setIndexedPropertiesToExternalArrayData = object;
    }

    public Object G$getPrototypeOf() {
        return this.getPrototypeOf;
    }

    public void S$getPrototypeOf(Object object) {
        this.getPrototypeOf = object;
    }

    public Object G$setPrototypeOf() {
        return this.setPrototypeOf;
    }

    public void S$setPrototypeOf(Object object) {
        this.setPrototypeOf = object;
    }

    public Object G$getOwnPropertyDescriptor() {
        return this.getOwnPropertyDescriptor;
    }

    public void S$getOwnPropertyDescriptor(Object object) {
        this.getOwnPropertyDescriptor = object;
    }

    public Object G$getOwnPropertyNames() {
        return this.getOwnPropertyNames;
    }

    public void S$getOwnPropertyNames(Object object) {
        this.getOwnPropertyNames = object;
    }

    public Object G$getOwnPropertySymbols() {
        return this.getOwnPropertySymbols;
    }

    public void S$getOwnPropertySymbols(Object object) {
        this.getOwnPropertySymbols = object;
    }

    public Object G$create() {
        return this.create;
    }

    public void S$create(Object object) {
        this.create = object;
    }

    public Object G$defineProperty() {
        return this.defineProperty;
    }

    public void S$defineProperty(Object object) {
        this.defineProperty = object;
    }

    public Object G$defineProperties() {
        return this.defineProperties;
    }

    public void S$defineProperties(Object object) {
        this.defineProperties = object;
    }

    public Object G$seal() {
        return this.seal;
    }

    public void S$seal(Object object) {
        this.seal = object;
    }

    public Object G$freeze() {
        return this.freeze;
    }

    public void S$freeze(Object object) {
        this.freeze = object;
    }

    public Object G$preventExtensions() {
        return this.preventExtensions;
    }

    public void S$preventExtensions(Object object) {
        this.preventExtensions = object;
    }

    public Object G$isSealed() {
        return this.isSealed;
    }

    public void S$isSealed(Object object) {
        this.isSealed = object;
    }

    public Object G$isFrozen() {
        return this.isFrozen;
    }

    public void S$isFrozen(Object object) {
        this.isFrozen = object;
    }

    public Object G$isExtensible() {
        return this.isExtensible;
    }

    public void S$isExtensible(Object object) {
        this.isExtensible = object;
    }

    public Object G$keys() {
        return this.keys;
    }

    public void S$keys(Object object) {
        this.keys = object;
    }

    public Object G$bindProperties() {
        return this.bindProperties;
    }

    public void S$bindProperties(Object object) {
        this.bindProperties = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(17);
        arrayList.add(AccessorProperty.create("setIndexedPropertiesToExternalArrayData", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("getPrototypeOf", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create("setPrototypeOf", 2, cfr_ldc_4(), cfr_ldc_5()));
        arrayList.add(AccessorProperty.create("getOwnPropertyDescriptor", 2, cfr_ldc_6(), cfr_ldc_7()));
        arrayList.add(AccessorProperty.create("getOwnPropertyNames", 2, cfr_ldc_8(), cfr_ldc_9()));
        arrayList.add(AccessorProperty.create("getOwnPropertySymbols", 2, cfr_ldc_10(), cfr_ldc_11()));
        arrayList.add(AccessorProperty.create("create", 2, cfr_ldc_12(), cfr_ldc_13()));
        arrayList.add(AccessorProperty.create("defineProperty", 2, cfr_ldc_14(), cfr_ldc_15()));
        arrayList.add(AccessorProperty.create("defineProperties", 2, cfr_ldc_16(), cfr_ldc_17()));
        arrayList.add(AccessorProperty.create("seal", 2, cfr_ldc_18(), cfr_ldc_19()));
        arrayList.add(AccessorProperty.create("freeze", 2, cfr_ldc_20(), cfr_ldc_21()));
        arrayList.add(AccessorProperty.create("preventExtensions", 2, cfr_ldc_22(), cfr_ldc_23()));
        arrayList.add(AccessorProperty.create("isSealed", 2, cfr_ldc_24(), cfr_ldc_25()));
        arrayList.add(AccessorProperty.create("isFrozen", 2, cfr_ldc_26(), cfr_ldc_27()));
        arrayList.add(AccessorProperty.create("isExtensible", 2, cfr_ldc_28(), cfr_ldc_29()));
        arrayList.add(AccessorProperty.create("keys", 2, cfr_ldc_30(), cfr_ldc_31()));
        arrayList.add(AccessorProperty.create("bindProperties", 2, cfr_ldc_32(), cfr_ldc_33()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeObject$Constructor() {
        super("Object", cfr_ldc_34(), $nasgenmap$, null);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("setIndexedPropertiesToExternalArrayData", cfr_ldc_35());
        scriptFunction.setDocumentationKey("Object.setIndexedPropertiesToExternalArrayData");
        this.setIndexedPropertiesToExternalArrayData = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("getPrototypeOf", cfr_ldc_36());
        scriptFunction2.setDocumentationKey("Object.getPrototypeOf");
        this.getPrototypeOf = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("setPrototypeOf", cfr_ldc_37());
        scriptFunction3.setDocumentationKey("Object.setPrototypeOf");
        this.setPrototypeOf = scriptFunction3;
        ScriptFunction scriptFunction4 = ScriptFunction.createBuiltin("getOwnPropertyDescriptor", cfr_ldc_38());
        scriptFunction4.setDocumentationKey("Object.getOwnPropertyDescriptor");
        this.getOwnPropertyDescriptor = scriptFunction4;
        ScriptFunction scriptFunction5 = ScriptFunction.createBuiltin("getOwnPropertyNames", cfr_ldc_39());
        scriptFunction5.setDocumentationKey("Object.getOwnPropertyNames");
        this.getOwnPropertyNames = scriptFunction5;
        ScriptFunction scriptFunction6 = ScriptFunction.createBuiltin("getOwnPropertySymbols", cfr_ldc_40());
        scriptFunction6.setDocumentationKey("Object.getOwnPropertySymbols");
        this.getOwnPropertySymbols = scriptFunction6;
        ScriptFunction scriptFunction7 = ScriptFunction.createBuiltin("create", cfr_ldc_41());
        scriptFunction7.setDocumentationKey("Object.create");
        this.create = scriptFunction7;
        ScriptFunction scriptFunction8 = ScriptFunction.createBuiltin("defineProperty", cfr_ldc_42());
        scriptFunction8.setDocumentationKey("Object.defineProperty");
        this.defineProperty = scriptFunction8;
        ScriptFunction scriptFunction9 = ScriptFunction.createBuiltin("defineProperties", cfr_ldc_43());
        scriptFunction9.setDocumentationKey("Object.defineProperties");
        this.defineProperties = scriptFunction9;
        ScriptFunction scriptFunction10 = ScriptFunction.createBuiltin("seal", cfr_ldc_44());
        scriptFunction10.setDocumentationKey("Object.seal");
        this.seal = scriptFunction10;
        ScriptFunction scriptFunction11 = ScriptFunction.createBuiltin("freeze", cfr_ldc_45());
        scriptFunction11.setDocumentationKey("Object.freeze");
        this.freeze = scriptFunction11;
        ScriptFunction scriptFunction12 = ScriptFunction.createBuiltin("preventExtensions", cfr_ldc_46());
        scriptFunction12.setDocumentationKey("Object.preventExtensions");
        this.preventExtensions = scriptFunction12;
        ScriptFunction scriptFunction13 = ScriptFunction.createBuiltin("isSealed", cfr_ldc_47());
        scriptFunction13.setDocumentationKey("Object.isSealed");
        this.isSealed = scriptFunction13;
        ScriptFunction scriptFunction14 = ScriptFunction.createBuiltin("isFrozen", cfr_ldc_48());
        scriptFunction14.setDocumentationKey("Object.isFrozen");
        this.isFrozen = scriptFunction14;
        ScriptFunction scriptFunction15 = ScriptFunction.createBuiltin("isExtensible", cfr_ldc_49());
        scriptFunction15.setDocumentationKey("Object.isExtensible");
        this.isExtensible = scriptFunction15;
        ScriptFunction scriptFunction16 = ScriptFunction.createBuiltin("keys", cfr_ldc_50());
        scriptFunction16.setDocumentationKey("Object.keys");
        this.keys = scriptFunction16;
        ScriptFunction scriptFunction17 = ScriptFunction.createBuiltin("bindProperties", cfr_ldc_51());
        scriptFunction17.setDocumentationKey("Object.bindProperties");
        this.bindProperties = scriptFunction17;
        NativeObject$Prototype nativeObject$Prototype = new NativeObject$Prototype();
        PrototypeObject.setConstructor(nativeObject$Prototype, this);
        this.setPrototype(nativeObject$Prototype);
        this.setDocumentationKey("Object");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$setIndexedPropertiesToExternalArrayData", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$setIndexedPropertiesToExternalArrayData", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$getPrototypeOf", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$getPrototypeOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$setPrototypeOf", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$setPrototypeOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$getOwnPropertyDescriptor", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$getOwnPropertyDescriptor", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$getOwnPropertyNames", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$getOwnPropertyNames", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$getOwnPropertySymbols", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$getOwnPropertySymbols", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$create", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$create", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$defineProperty", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$defineProperty", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$defineProperties", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$defineProperties", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$seal", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$seal", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$freeze", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$freeze", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$preventExtensions", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$preventExtensions", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$isSealed", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$isSealed", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$isFrozen", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$isFrozen", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$isExtensible", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$isExtensible", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$keys", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$keys", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "G$bindProperties", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeObject$Constructor.class, "S$bindProperties", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "setIndexedPropertiesToExternalArrayData", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/runtime/ScriptObject;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "getPrototypeOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "setPrototypeOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "getOwnPropertyDescriptor", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "getOwnPropertyNames", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/runtime/ScriptObject;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "getOwnPropertySymbols", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/runtime/ScriptObject;", null));
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
            return MethodHandles.lookup().findStatic(NativeObject.class, "create", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/runtime/ScriptObject;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_42() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "defineProperty", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/runtime/ScriptObject;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_43() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "defineProperties", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/runtime/ScriptObject;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_44() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "seal", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_45() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "freeze", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_46() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "preventExtensions", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_47() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "isSealed", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_48() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "isFrozen", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_49() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "isExtensible", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_50() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "keys", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/runtime/ScriptObject;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_51() {
        try {
            return MethodHandles.lookup().findStatic(NativeObject.class, "bindProperties", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
