package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeDebug;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;

final class NativeDebug$Constructor
extends ScriptObject {
    private Object getArrayDataClass;
    private Object getArrayData;
    private Object getContext;
    private Object map;
    private Object identical;
    private Object equalWithoutType;
    private Object diffPropertyMaps;
    private Object getClass;
    private Object equals;
    private Object toJavaString;
    private Object toIdentString;
    private Object isDebuggableFunction;
    private Object getListenerCount;
    private Object dumpCounters;
    private Object getEventQueueCapacity;
    private Object setEventQueueCapacity;
    private Object addRuntimeEvent;
    private Object expandEventQueueCapacity;
    private Object clearRuntimeEvents;
    private Object removeRuntimeEvent;
    private Object getRuntimeEvents;
    private Object getLastRuntimeEvent;
    private static final PropertyMap $nasgenmap$;

    public Object G$getArrayDataClass() {
        return this.getArrayDataClass;
    }

    public void S$getArrayDataClass(Object object) {
        this.getArrayDataClass = object;
    }

    public Object G$getArrayData() {
        return this.getArrayData;
    }

    public void S$getArrayData(Object object) {
        this.getArrayData = object;
    }

    public Object G$getContext() {
        return this.getContext;
    }

    public void S$getContext(Object object) {
        this.getContext = object;
    }

    public Object G$map() {
        return this.map;
    }

    public void S$map(Object object) {
        this.map = object;
    }

    public Object G$identical() {
        return this.identical;
    }

    public void S$identical(Object object) {
        this.identical = object;
    }

    public Object G$equalWithoutType() {
        return this.equalWithoutType;
    }

    public void S$equalWithoutType(Object object) {
        this.equalWithoutType = object;
    }

    public Object G$diffPropertyMaps() {
        return this.diffPropertyMaps;
    }

    public void S$diffPropertyMaps(Object object) {
        this.diffPropertyMaps = object;
    }

    public Object G$getClass() {
        return this.getClass;
    }

    public void S$getClass(Object object) {
        this.getClass = object;
    }

    public Object G$equals() {
        return this.equals;
    }

    public void S$equals(Object object) {
        this.equals = object;
    }

    public Object G$toJavaString() {
        return this.toJavaString;
    }

    public void S$toJavaString(Object object) {
        this.toJavaString = object;
    }

    public Object G$toIdentString() {
        return this.toIdentString;
    }

    public void S$toIdentString(Object object) {
        this.toIdentString = object;
    }

    public Object G$isDebuggableFunction() {
        return this.isDebuggableFunction;
    }

    public void S$isDebuggableFunction(Object object) {
        this.isDebuggableFunction = object;
    }

    public Object G$getListenerCount() {
        return this.getListenerCount;
    }

    public void S$getListenerCount(Object object) {
        this.getListenerCount = object;
    }

    public Object G$dumpCounters() {
        return this.dumpCounters;
    }

    public void S$dumpCounters(Object object) {
        this.dumpCounters = object;
    }

    public Object G$getEventQueueCapacity() {
        return this.getEventQueueCapacity;
    }

    public void S$getEventQueueCapacity(Object object) {
        this.getEventQueueCapacity = object;
    }

    public Object G$setEventQueueCapacity() {
        return this.setEventQueueCapacity;
    }

    public void S$setEventQueueCapacity(Object object) {
        this.setEventQueueCapacity = object;
    }

    public Object G$addRuntimeEvent() {
        return this.addRuntimeEvent;
    }

    public void S$addRuntimeEvent(Object object) {
        this.addRuntimeEvent = object;
    }

    public Object G$expandEventQueueCapacity() {
        return this.expandEventQueueCapacity;
    }

    public void S$expandEventQueueCapacity(Object object) {
        this.expandEventQueueCapacity = object;
    }

    public Object G$clearRuntimeEvents() {
        return this.clearRuntimeEvents;
    }

    public void S$clearRuntimeEvents(Object object) {
        this.clearRuntimeEvents = object;
    }

    public Object G$removeRuntimeEvent() {
        return this.removeRuntimeEvent;
    }

    public void S$removeRuntimeEvent(Object object) {
        this.removeRuntimeEvent = object;
    }

    public Object G$getRuntimeEvents() {
        return this.getRuntimeEvents;
    }

    public void S$getRuntimeEvents(Object object) {
        this.getRuntimeEvents = object;
    }

    public Object G$getLastRuntimeEvent() {
        return this.getLastRuntimeEvent;
    }

    public void S$getLastRuntimeEvent(Object object) {
        this.getLastRuntimeEvent = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(22);
        arrayList.add(AccessorProperty.create("getArrayDataClass", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("getArrayData", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create("getContext", 2, cfr_ldc_4(), cfr_ldc_5()));
        arrayList.add(AccessorProperty.create("map", 2, cfr_ldc_6(), cfr_ldc_7()));
        arrayList.add(AccessorProperty.create("identical", 2, cfr_ldc_8(), cfr_ldc_9()));
        arrayList.add(AccessorProperty.create("equalWithoutType", 2, cfr_ldc_10(), cfr_ldc_11()));
        arrayList.add(AccessorProperty.create("diffPropertyMaps", 2, cfr_ldc_12(), cfr_ldc_13()));
        arrayList.add(AccessorProperty.create("getClass", 2, cfr_ldc_14(), cfr_ldc_15()));
        arrayList.add(AccessorProperty.create("equals", 2, cfr_ldc_16(), cfr_ldc_17()));
        arrayList.add(AccessorProperty.create("toJavaString", 2, cfr_ldc_18(), cfr_ldc_19()));
        arrayList.add(AccessorProperty.create("toIdentString", 2, cfr_ldc_20(), cfr_ldc_21()));
        arrayList.add(AccessorProperty.create("isDebuggableFunction", 2, cfr_ldc_22(), cfr_ldc_23()));
        arrayList.add(AccessorProperty.create("getListenerCount", 2, cfr_ldc_24(), cfr_ldc_25()));
        arrayList.add(AccessorProperty.create("dumpCounters", 2, cfr_ldc_26(), cfr_ldc_27()));
        arrayList.add(AccessorProperty.create("getEventQueueCapacity", 2, cfr_ldc_28(), cfr_ldc_29()));
        arrayList.add(AccessorProperty.create("setEventQueueCapacity", 2, cfr_ldc_30(), cfr_ldc_31()));
        arrayList.add(AccessorProperty.create("addRuntimeEvent", 2, cfr_ldc_32(), cfr_ldc_33()));
        arrayList.add(AccessorProperty.create("expandEventQueueCapacity", 2, cfr_ldc_34(), cfr_ldc_35()));
        arrayList.add(AccessorProperty.create("clearRuntimeEvents", 2, cfr_ldc_36(), cfr_ldc_37()));
        arrayList.add(AccessorProperty.create("removeRuntimeEvent", 2, cfr_ldc_38(), cfr_ldc_39()));
        arrayList.add(AccessorProperty.create("getRuntimeEvents", 2, cfr_ldc_40(), cfr_ldc_41()));
        arrayList.add(AccessorProperty.create("getLastRuntimeEvent", 2, cfr_ldc_42(), cfr_ldc_43()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeDebug$Constructor() {
        super($nasgenmap$);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("getArrayDataClass", cfr_ldc_44());
        scriptFunction.setDocumentationKey("Debug.getArrayDataClass");
        this.getArrayDataClass = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("getArrayData", cfr_ldc_45());
        scriptFunction2.setDocumentationKey("Debug.getArrayData");
        this.getArrayData = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("getContext", cfr_ldc_46());
        scriptFunction3.setDocumentationKey("Debug.getContext");
        this.getContext = scriptFunction3;
        ScriptFunction scriptFunction4 = ScriptFunction.createBuiltin("map", cfr_ldc_47());
        scriptFunction4.setDocumentationKey("Debug.map");
        this.map = scriptFunction4;
        ScriptFunction scriptFunction5 = ScriptFunction.createBuiltin("identical", cfr_ldc_48());
        scriptFunction5.setDocumentationKey("Debug.identical");
        this.identical = scriptFunction5;
        ScriptFunction scriptFunction6 = ScriptFunction.createBuiltin("equalWithoutType", cfr_ldc_49());
        scriptFunction6.setDocumentationKey("Debug.equalWithoutType");
        this.equalWithoutType = scriptFunction6;
        ScriptFunction scriptFunction7 = ScriptFunction.createBuiltin("diffPropertyMaps", cfr_ldc_50());
        scriptFunction7.setDocumentationKey("Debug.diffPropertyMaps");
        this.diffPropertyMaps = scriptFunction7;
        ScriptFunction scriptFunction8 = ScriptFunction.createBuiltin("getClass", cfr_ldc_51());
        scriptFunction8.setDocumentationKey("Debug.getClass");
        this.getClass = scriptFunction8;
        ScriptFunction scriptFunction9 = ScriptFunction.createBuiltin("equals", cfr_ldc_52());
        scriptFunction9.setDocumentationKey("Debug.equals");
        this.equals = scriptFunction9;
        ScriptFunction scriptFunction10 = ScriptFunction.createBuiltin("toJavaString", cfr_ldc_53());
        scriptFunction10.setDocumentationKey("Debug.toJavaString");
        this.toJavaString = scriptFunction10;
        ScriptFunction scriptFunction11 = ScriptFunction.createBuiltin("toIdentString", cfr_ldc_54());
        scriptFunction11.setDocumentationKey("Debug.toIdentString");
        this.toIdentString = scriptFunction11;
        ScriptFunction scriptFunction12 = ScriptFunction.createBuiltin("isDebuggableFunction", cfr_ldc_55());
        scriptFunction12.setDocumentationKey("Debug.isDebuggableFunction");
        this.isDebuggableFunction = scriptFunction12;
        ScriptFunction scriptFunction13 = ScriptFunction.createBuiltin("getListenerCount", cfr_ldc_56());
        scriptFunction13.setDocumentationKey("Debug.getListenerCount");
        this.getListenerCount = scriptFunction13;
        ScriptFunction scriptFunction14 = ScriptFunction.createBuiltin("dumpCounters", cfr_ldc_57());
        scriptFunction14.setDocumentationKey("Debug.dumpCounters");
        this.dumpCounters = scriptFunction14;
        ScriptFunction scriptFunction15 = ScriptFunction.createBuiltin("getEventQueueCapacity", cfr_ldc_58());
        scriptFunction15.setDocumentationKey("Debug.getEventQueueCapacity");
        this.getEventQueueCapacity = scriptFunction15;
        ScriptFunction scriptFunction16 = ScriptFunction.createBuiltin("setEventQueueCapacity", cfr_ldc_59());
        scriptFunction16.setDocumentationKey("Debug.setEventQueueCapacity");
        this.setEventQueueCapacity = scriptFunction16;
        ScriptFunction scriptFunction17 = ScriptFunction.createBuiltin("addRuntimeEvent", cfr_ldc_60());
        scriptFunction17.setDocumentationKey("Debug.addRuntimeEvent");
        this.addRuntimeEvent = scriptFunction17;
        ScriptFunction scriptFunction18 = ScriptFunction.createBuiltin("expandEventQueueCapacity", cfr_ldc_61());
        scriptFunction18.setDocumentationKey("Debug.expandEventQueueCapacity");
        this.expandEventQueueCapacity = scriptFunction18;
        ScriptFunction scriptFunction19 = ScriptFunction.createBuiltin("clearRuntimeEvents", cfr_ldc_62());
        scriptFunction19.setDocumentationKey("Debug.clearRuntimeEvents");
        this.clearRuntimeEvents = scriptFunction19;
        ScriptFunction scriptFunction20 = ScriptFunction.createBuiltin("removeRuntimeEvent", cfr_ldc_63());
        scriptFunction20.setDocumentationKey("Debug.removeRuntimeEvent");
        this.removeRuntimeEvent = scriptFunction20;
        ScriptFunction scriptFunction21 = ScriptFunction.createBuiltin("getRuntimeEvents", cfr_ldc_64());
        scriptFunction21.setDocumentationKey("Debug.getRuntimeEvents");
        this.getRuntimeEvents = scriptFunction21;
        ScriptFunction scriptFunction22 = ScriptFunction.createBuiltin("getLastRuntimeEvent", cfr_ldc_65());
        scriptFunction22.setDocumentationKey("Debug.getLastRuntimeEvent");
        this.getLastRuntimeEvent = scriptFunction22;
    }

    @Override
    public String getClassName() {
        return "Debug";
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$getArrayDataClass", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$getArrayDataClass", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$getArrayData", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$getArrayData", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$getContext", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$getContext", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$map", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$map", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$identical", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$identical", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$equalWithoutType", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$equalWithoutType", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$diffPropertyMaps", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$diffPropertyMaps", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$getClass", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$getClass", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$equals", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$equals", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$toJavaString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$toJavaString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$toIdentString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$toIdentString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$isDebuggableFunction", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$isDebuggableFunction", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$getListenerCount", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$getListenerCount", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$dumpCounters", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$dumpCounters", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$getEventQueueCapacity", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$getEventQueueCapacity", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$setEventQueueCapacity", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$setEventQueueCapacity", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$addRuntimeEvent", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$addRuntimeEvent", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$expandEventQueueCapacity", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$expandEventQueueCapacity", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$clearRuntimeEvents", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$clearRuntimeEvents", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$removeRuntimeEvent", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$removeRuntimeEvent", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$getRuntimeEvents", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$getRuntimeEvents", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "G$getLastRuntimeEvent", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDebug$Constructor.class, "S$getLastRuntimeEvent", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findStatic(NativeDebug.class, "getArrayDataClass", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeDebug.class, "getArrayData", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeDebug.class, "getContext", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeDebug.class, "map", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeDebug.class, "identical", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z", null));
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
            return MethodHandles.lookup().findStatic(NativeDebug.class, "equalWithoutType", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeDebug.class, "diffPropertyMaps", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeDebug.class, "getClass", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_52() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "equals", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_53() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "toJavaString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_54() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "toIdentString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_55() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "isDebuggableFunction", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_56() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "getListenerCount", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)I", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_57() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "dumpCounters", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_58() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "getEventQueueCapacity", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_59() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "setEventQueueCapacity", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_60() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "addRuntimeEvent", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_61() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "expandEventQueueCapacity", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_62() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "clearRuntimeEvents", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_63() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "removeRuntimeEvent", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_64() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "getRuntimeEvents", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_65() {
        try {
            return MethodHandles.lookup().findStatic(NativeDebug.class, "getLastRuntimeEvent", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
