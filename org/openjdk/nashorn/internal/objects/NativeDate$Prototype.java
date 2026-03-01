package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeDate;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

final class NativeDate$Prototype
extends PrototypeObject {
    private Object toString;
    private Object toDateString;
    private Object toTimeString;
    private Object toLocaleString;
    private Object toLocaleDateString;
    private Object toLocaleTimeString;
    private Object valueOf;
    private Object getTime;
    private Object getFullYear;
    private Object getUTCFullYear;
    private Object getYear;
    private Object getMonth;
    private Object getUTCMonth;
    private Object getDate;
    private Object getUTCDate;
    private Object getDay;
    private Object getUTCDay;
    private Object getHours;
    private Object getUTCHours;
    private Object getMinutes;
    private Object getUTCMinutes;
    private Object getSeconds;
    private Object getUTCSeconds;
    private Object getMilliseconds;
    private Object getUTCMilliseconds;
    private Object getTimezoneOffset;
    private Object setTime;
    private Object setMilliseconds;
    private Object setUTCMilliseconds;
    private Object setSeconds;
    private Object setUTCSeconds;
    private Object setMinutes;
    private Object setUTCMinutes;
    private Object setHours;
    private Object setUTCHours;
    private Object setDate;
    private Object setUTCDate;
    private Object setMonth;
    private Object setUTCMonth;
    private Object setFullYear;
    private Object setUTCFullYear;
    private Object setYear;
    private Object toUTCString;
    private Object toGMTString;
    private Object toISOString;
    private Object toJSON;
    private static final PropertyMap $nasgenmap$;

    public Object G$toString() {
        return this.toString;
    }

    public void S$toString(Object object) {
        this.toString = object;
    }

    public Object G$toDateString() {
        return this.toDateString;
    }

    public void S$toDateString(Object object) {
        this.toDateString = object;
    }

    public Object G$toTimeString() {
        return this.toTimeString;
    }

    public void S$toTimeString(Object object) {
        this.toTimeString = object;
    }

    public Object G$toLocaleString() {
        return this.toLocaleString;
    }

    public void S$toLocaleString(Object object) {
        this.toLocaleString = object;
    }

    public Object G$toLocaleDateString() {
        return this.toLocaleDateString;
    }

    public void S$toLocaleDateString(Object object) {
        this.toLocaleDateString = object;
    }

    public Object G$toLocaleTimeString() {
        return this.toLocaleTimeString;
    }

    public void S$toLocaleTimeString(Object object) {
        this.toLocaleTimeString = object;
    }

    public Object G$valueOf() {
        return this.valueOf;
    }

    public void S$valueOf(Object object) {
        this.valueOf = object;
    }

    public Object G$getTime() {
        return this.getTime;
    }

    public void S$getTime(Object object) {
        this.getTime = object;
    }

    public Object G$getFullYear() {
        return this.getFullYear;
    }

    public void S$getFullYear(Object object) {
        this.getFullYear = object;
    }

    public Object G$getUTCFullYear() {
        return this.getUTCFullYear;
    }

    public void S$getUTCFullYear(Object object) {
        this.getUTCFullYear = object;
    }

    public Object G$getYear() {
        return this.getYear;
    }

    public void S$getYear(Object object) {
        this.getYear = object;
    }

    public Object G$getMonth() {
        return this.getMonth;
    }

    public void S$getMonth(Object object) {
        this.getMonth = object;
    }

    public Object G$getUTCMonth() {
        return this.getUTCMonth;
    }

    public void S$getUTCMonth(Object object) {
        this.getUTCMonth = object;
    }

    public Object G$getDate() {
        return this.getDate;
    }

    public void S$getDate(Object object) {
        this.getDate = object;
    }

    public Object G$getUTCDate() {
        return this.getUTCDate;
    }

    public void S$getUTCDate(Object object) {
        this.getUTCDate = object;
    }

    public Object G$getDay() {
        return this.getDay;
    }

    public void S$getDay(Object object) {
        this.getDay = object;
    }

    public Object G$getUTCDay() {
        return this.getUTCDay;
    }

    public void S$getUTCDay(Object object) {
        this.getUTCDay = object;
    }

    public Object G$getHours() {
        return this.getHours;
    }

    public void S$getHours(Object object) {
        this.getHours = object;
    }

    public Object G$getUTCHours() {
        return this.getUTCHours;
    }

    public void S$getUTCHours(Object object) {
        this.getUTCHours = object;
    }

    public Object G$getMinutes() {
        return this.getMinutes;
    }

    public void S$getMinutes(Object object) {
        this.getMinutes = object;
    }

    public Object G$getUTCMinutes() {
        return this.getUTCMinutes;
    }

    public void S$getUTCMinutes(Object object) {
        this.getUTCMinutes = object;
    }

    public Object G$getSeconds() {
        return this.getSeconds;
    }

    public void S$getSeconds(Object object) {
        this.getSeconds = object;
    }

    public Object G$getUTCSeconds() {
        return this.getUTCSeconds;
    }

    public void S$getUTCSeconds(Object object) {
        this.getUTCSeconds = object;
    }

    public Object G$getMilliseconds() {
        return this.getMilliseconds;
    }

    public void S$getMilliseconds(Object object) {
        this.getMilliseconds = object;
    }

    public Object G$getUTCMilliseconds() {
        return this.getUTCMilliseconds;
    }

    public void S$getUTCMilliseconds(Object object) {
        this.getUTCMilliseconds = object;
    }

    public Object G$getTimezoneOffset() {
        return this.getTimezoneOffset;
    }

    public void S$getTimezoneOffset(Object object) {
        this.getTimezoneOffset = object;
    }

    public Object G$setTime() {
        return this.setTime;
    }

    public void S$setTime(Object object) {
        this.setTime = object;
    }

    public Object G$setMilliseconds() {
        return this.setMilliseconds;
    }

    public void S$setMilliseconds(Object object) {
        this.setMilliseconds = object;
    }

    public Object G$setUTCMilliseconds() {
        return this.setUTCMilliseconds;
    }

    public void S$setUTCMilliseconds(Object object) {
        this.setUTCMilliseconds = object;
    }

    public Object G$setSeconds() {
        return this.setSeconds;
    }

    public void S$setSeconds(Object object) {
        this.setSeconds = object;
    }

    public Object G$setUTCSeconds() {
        return this.setUTCSeconds;
    }

    public void S$setUTCSeconds(Object object) {
        this.setUTCSeconds = object;
    }

    public Object G$setMinutes() {
        return this.setMinutes;
    }

    public void S$setMinutes(Object object) {
        this.setMinutes = object;
    }

    public Object G$setUTCMinutes() {
        return this.setUTCMinutes;
    }

    public void S$setUTCMinutes(Object object) {
        this.setUTCMinutes = object;
    }

    public Object G$setHours() {
        return this.setHours;
    }

    public void S$setHours(Object object) {
        this.setHours = object;
    }

    public Object G$setUTCHours() {
        return this.setUTCHours;
    }

    public void S$setUTCHours(Object object) {
        this.setUTCHours = object;
    }

    public Object G$setDate() {
        return this.setDate;
    }

    public void S$setDate(Object object) {
        this.setDate = object;
    }

    public Object G$setUTCDate() {
        return this.setUTCDate;
    }

    public void S$setUTCDate(Object object) {
        this.setUTCDate = object;
    }

    public Object G$setMonth() {
        return this.setMonth;
    }

    public void S$setMonth(Object object) {
        this.setMonth = object;
    }

    public Object G$setUTCMonth() {
        return this.setUTCMonth;
    }

    public void S$setUTCMonth(Object object) {
        this.setUTCMonth = object;
    }

    public Object G$setFullYear() {
        return this.setFullYear;
    }

    public void S$setFullYear(Object object) {
        this.setFullYear = object;
    }

    public Object G$setUTCFullYear() {
        return this.setUTCFullYear;
    }

    public void S$setUTCFullYear(Object object) {
        this.setUTCFullYear = object;
    }

    public Object G$setYear() {
        return this.setYear;
    }

    public void S$setYear(Object object) {
        this.setYear = object;
    }

    public Object G$toUTCString() {
        return this.toUTCString;
    }

    public void S$toUTCString(Object object) {
        this.toUTCString = object;
    }

    public Object G$toGMTString() {
        return this.toGMTString;
    }

    public void S$toGMTString(Object object) {
        this.toGMTString = object;
    }

    public Object G$toISOString() {
        return this.toISOString;
    }

    public void S$toISOString(Object object) {
        this.toISOString = object;
    }

    public Object G$toJSON() {
        return this.toJSON;
    }

    public void S$toJSON(Object object) {
        this.toJSON = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(46);
        arrayList.add(AccessorProperty.create("toString", 2, cfr_ldc_0(), cfr_ldc_1()));
        arrayList.add(AccessorProperty.create("toDateString", 2, cfr_ldc_2(), cfr_ldc_3()));
        arrayList.add(AccessorProperty.create("toTimeString", 2, cfr_ldc_4(), cfr_ldc_5()));
        arrayList.add(AccessorProperty.create("toLocaleString", 2, cfr_ldc_6(), cfr_ldc_7()));
        arrayList.add(AccessorProperty.create("toLocaleDateString", 2, cfr_ldc_8(), cfr_ldc_9()));
        arrayList.add(AccessorProperty.create("toLocaleTimeString", 2, cfr_ldc_10(), cfr_ldc_11()));
        arrayList.add(AccessorProperty.create("valueOf", 2, cfr_ldc_12(), cfr_ldc_13()));
        arrayList.add(AccessorProperty.create("getTime", 2, cfr_ldc_14(), cfr_ldc_15()));
        arrayList.add(AccessorProperty.create("getFullYear", 2, cfr_ldc_16(), cfr_ldc_17()));
        arrayList.add(AccessorProperty.create("getUTCFullYear", 2, cfr_ldc_18(), cfr_ldc_19()));
        arrayList.add(AccessorProperty.create("getYear", 2, cfr_ldc_20(), cfr_ldc_21()));
        arrayList.add(AccessorProperty.create("getMonth", 2, cfr_ldc_22(), cfr_ldc_23()));
        arrayList.add(AccessorProperty.create("getUTCMonth", 2, cfr_ldc_24(), cfr_ldc_25()));
        arrayList.add(AccessorProperty.create("getDate", 2, cfr_ldc_26(), cfr_ldc_27()));
        arrayList.add(AccessorProperty.create("getUTCDate", 2, cfr_ldc_28(), cfr_ldc_29()));
        arrayList.add(AccessorProperty.create("getDay", 2, cfr_ldc_30(), cfr_ldc_31()));
        arrayList.add(AccessorProperty.create("getUTCDay", 2, cfr_ldc_32(), cfr_ldc_33()));
        arrayList.add(AccessorProperty.create("getHours", 2, cfr_ldc_34(), cfr_ldc_35()));
        arrayList.add(AccessorProperty.create("getUTCHours", 2, cfr_ldc_36(), cfr_ldc_37()));
        arrayList.add(AccessorProperty.create("getMinutes", 2, cfr_ldc_38(), cfr_ldc_39()));
        arrayList.add(AccessorProperty.create("getUTCMinutes", 2, cfr_ldc_40(), cfr_ldc_41()));
        arrayList.add(AccessorProperty.create("getSeconds", 2, cfr_ldc_42(), cfr_ldc_43()));
        arrayList.add(AccessorProperty.create("getUTCSeconds", 2, cfr_ldc_44(), cfr_ldc_45()));
        arrayList.add(AccessorProperty.create("getMilliseconds", 2, cfr_ldc_46(), cfr_ldc_47()));
        arrayList.add(AccessorProperty.create("getUTCMilliseconds", 2, cfr_ldc_48(), cfr_ldc_49()));
        arrayList.add(AccessorProperty.create("getTimezoneOffset", 2, cfr_ldc_50(), cfr_ldc_51()));
        arrayList.add(AccessorProperty.create("setTime", 2, cfr_ldc_52(), cfr_ldc_53()));
        arrayList.add(AccessorProperty.create("setMilliseconds", 2, cfr_ldc_54(), cfr_ldc_55()));
        arrayList.add(AccessorProperty.create("setUTCMilliseconds", 2, cfr_ldc_56(), cfr_ldc_57()));
        arrayList.add(AccessorProperty.create("setSeconds", 2, cfr_ldc_58(), cfr_ldc_59()));
        arrayList.add(AccessorProperty.create("setUTCSeconds", 2, cfr_ldc_60(), cfr_ldc_61()));
        arrayList.add(AccessorProperty.create("setMinutes", 2, cfr_ldc_62(), cfr_ldc_63()));
        arrayList.add(AccessorProperty.create("setUTCMinutes", 2, cfr_ldc_64(), cfr_ldc_65()));
        arrayList.add(AccessorProperty.create("setHours", 2, cfr_ldc_66(), cfr_ldc_67()));
        arrayList.add(AccessorProperty.create("setUTCHours", 2, cfr_ldc_68(), cfr_ldc_69()));
        arrayList.add(AccessorProperty.create("setDate", 2, cfr_ldc_70(), cfr_ldc_71()));
        arrayList.add(AccessorProperty.create("setUTCDate", 2, cfr_ldc_72(), cfr_ldc_73()));
        arrayList.add(AccessorProperty.create("setMonth", 2, cfr_ldc_74(), cfr_ldc_75()));
        arrayList.add(AccessorProperty.create("setUTCMonth", 2, cfr_ldc_76(), cfr_ldc_77()));
        arrayList.add(AccessorProperty.create("setFullYear", 2, cfr_ldc_78(), cfr_ldc_79()));
        arrayList.add(AccessorProperty.create("setUTCFullYear", 2, cfr_ldc_80(), cfr_ldc_81()));
        arrayList.add(AccessorProperty.create("setYear", 2, cfr_ldc_82(), cfr_ldc_83()));
        arrayList.add(AccessorProperty.create("toUTCString", 2, cfr_ldc_84(), cfr_ldc_85()));
        arrayList.add(AccessorProperty.create("toGMTString", 2, cfr_ldc_86(), cfr_ldc_87()));
        arrayList.add(AccessorProperty.create("toISOString", 2, cfr_ldc_88(), cfr_ldc_89()));
        arrayList.add(AccessorProperty.create("toJSON", 2, cfr_ldc_90(), cfr_ldc_91()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeDate$Prototype() {
        super($nasgenmap$);
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("toString", cfr_ldc_92());
        scriptFunction.setDocumentationKey("Date.prototype.toString");
        this.toString = scriptFunction;
        ScriptFunction scriptFunction2 = ScriptFunction.createBuiltin("toDateString", cfr_ldc_93());
        scriptFunction2.setDocumentationKey("Date.prototype.toDateString");
        this.toDateString = scriptFunction2;
        ScriptFunction scriptFunction3 = ScriptFunction.createBuiltin("toTimeString", cfr_ldc_94());
        scriptFunction3.setDocumentationKey("Date.prototype.toTimeString");
        this.toTimeString = scriptFunction3;
        ScriptFunction scriptFunction4 = ScriptFunction.createBuiltin("toLocaleString", cfr_ldc_95());
        scriptFunction4.setDocumentationKey("Date.prototype.toLocaleString");
        this.toLocaleString = scriptFunction4;
        ScriptFunction scriptFunction5 = ScriptFunction.createBuiltin("toLocaleDateString", cfr_ldc_96());
        scriptFunction5.setDocumentationKey("Date.prototype.toLocaleDateString");
        this.toLocaleDateString = scriptFunction5;
        ScriptFunction scriptFunction6 = ScriptFunction.createBuiltin("toLocaleTimeString", cfr_ldc_97());
        scriptFunction6.setDocumentationKey("Date.prototype.toLocaleTimeString");
        this.toLocaleTimeString = scriptFunction6;
        ScriptFunction scriptFunction7 = ScriptFunction.createBuiltin("valueOf", cfr_ldc_98());
        scriptFunction7.setDocumentationKey("Date.prototype.valueOf");
        this.valueOf = scriptFunction7;
        ScriptFunction scriptFunction8 = ScriptFunction.createBuiltin("getTime", cfr_ldc_99());
        scriptFunction8.setDocumentationKey("Date.prototype.getTime");
        this.getTime = scriptFunction8;
        ScriptFunction scriptFunction9 = ScriptFunction.createBuiltin("getFullYear", cfr_ldc_100());
        scriptFunction9.setDocumentationKey("Date.prototype.getFullYear");
        this.getFullYear = scriptFunction9;
        ScriptFunction scriptFunction10 = ScriptFunction.createBuiltin("getUTCFullYear", cfr_ldc_101());
        scriptFunction10.setDocumentationKey("Date.prototype.getUTCFullYear");
        this.getUTCFullYear = scriptFunction10;
        ScriptFunction scriptFunction11 = ScriptFunction.createBuiltin("getYear", cfr_ldc_102());
        scriptFunction11.setDocumentationKey("Date.prototype.getYear");
        this.getYear = scriptFunction11;
        ScriptFunction scriptFunction12 = ScriptFunction.createBuiltin("getMonth", cfr_ldc_103());
        scriptFunction12.setDocumentationKey("Date.prototype.getMonth");
        this.getMonth = scriptFunction12;
        ScriptFunction scriptFunction13 = ScriptFunction.createBuiltin("getUTCMonth", cfr_ldc_104());
        scriptFunction13.setDocumentationKey("Date.prototype.getUTCMonth");
        this.getUTCMonth = scriptFunction13;
        ScriptFunction scriptFunction14 = ScriptFunction.createBuiltin("getDate", cfr_ldc_105());
        scriptFunction14.setDocumentationKey("Date.prototype.getDate");
        this.getDate = scriptFunction14;
        ScriptFunction scriptFunction15 = ScriptFunction.createBuiltin("getUTCDate", cfr_ldc_106());
        scriptFunction15.setDocumentationKey("Date.prototype.getUTCDate");
        this.getUTCDate = scriptFunction15;
        ScriptFunction scriptFunction16 = ScriptFunction.createBuiltin("getDay", cfr_ldc_107());
        scriptFunction16.setDocumentationKey("Date.prototype.getDay");
        this.getDay = scriptFunction16;
        ScriptFunction scriptFunction17 = ScriptFunction.createBuiltin("getUTCDay", cfr_ldc_108());
        scriptFunction17.setDocumentationKey("Date.prototype.getUTCDay");
        this.getUTCDay = scriptFunction17;
        ScriptFunction scriptFunction18 = ScriptFunction.createBuiltin("getHours", cfr_ldc_109());
        scriptFunction18.setDocumentationKey("Date.prototype.getHours");
        this.getHours = scriptFunction18;
        ScriptFunction scriptFunction19 = ScriptFunction.createBuiltin("getUTCHours", cfr_ldc_110());
        scriptFunction19.setDocumentationKey("Date.prototype.getUTCHours");
        this.getUTCHours = scriptFunction19;
        ScriptFunction scriptFunction20 = ScriptFunction.createBuiltin("getMinutes", cfr_ldc_111());
        scriptFunction20.setDocumentationKey("Date.prototype.getMinutes");
        this.getMinutes = scriptFunction20;
        ScriptFunction scriptFunction21 = ScriptFunction.createBuiltin("getUTCMinutes", cfr_ldc_112());
        scriptFunction21.setDocumentationKey("Date.prototype.getUTCMinutes");
        this.getUTCMinutes = scriptFunction21;
        ScriptFunction scriptFunction22 = ScriptFunction.createBuiltin("getSeconds", cfr_ldc_113());
        scriptFunction22.setDocumentationKey("Date.prototype.getSeconds");
        this.getSeconds = scriptFunction22;
        ScriptFunction scriptFunction23 = ScriptFunction.createBuiltin("getUTCSeconds", cfr_ldc_114());
        scriptFunction23.setDocumentationKey("Date.prototype.getUTCSeconds");
        this.getUTCSeconds = scriptFunction23;
        ScriptFunction scriptFunction24 = ScriptFunction.createBuiltin("getMilliseconds", cfr_ldc_115());
        scriptFunction24.setDocumentationKey("Date.prototype.getMilliseconds");
        this.getMilliseconds = scriptFunction24;
        ScriptFunction scriptFunction25 = ScriptFunction.createBuiltin("getUTCMilliseconds", cfr_ldc_116());
        scriptFunction25.setDocumentationKey("Date.prototype.getUTCMilliseconds");
        this.getUTCMilliseconds = scriptFunction25;
        ScriptFunction scriptFunction26 = ScriptFunction.createBuiltin("getTimezoneOffset", cfr_ldc_117());
        scriptFunction26.setDocumentationKey("Date.prototype.getTimezoneOffset");
        this.getTimezoneOffset = scriptFunction26;
        ScriptFunction scriptFunction27 = ScriptFunction.createBuiltin("setTime", cfr_ldc_118());
        scriptFunction27.setDocumentationKey("Date.prototype.setTime");
        this.setTime = scriptFunction27;
        ScriptFunction scriptFunction28 = ScriptFunction.createBuiltin("setMilliseconds", cfr_ldc_119());
        scriptFunction28.setArity(1);
        scriptFunction28.setDocumentationKey("Date.prototype.setMilliseconds");
        this.setMilliseconds = scriptFunction28;
        ScriptFunction scriptFunction29 = ScriptFunction.createBuiltin("setUTCMilliseconds", cfr_ldc_120());
        scriptFunction29.setArity(1);
        scriptFunction29.setDocumentationKey("Date.prototype.setUTCMilliseconds");
        this.setUTCMilliseconds = scriptFunction29;
        ScriptFunction scriptFunction30 = ScriptFunction.createBuiltin("setSeconds", cfr_ldc_121());
        scriptFunction30.setArity(2);
        scriptFunction30.setDocumentationKey("Date.prototype.setSeconds");
        this.setSeconds = scriptFunction30;
        ScriptFunction scriptFunction31 = ScriptFunction.createBuiltin("setUTCSeconds", cfr_ldc_122());
        scriptFunction31.setArity(2);
        scriptFunction31.setDocumentationKey("Date.prototype.setUTCSeconds");
        this.setUTCSeconds = scriptFunction31;
        ScriptFunction scriptFunction32 = ScriptFunction.createBuiltin("setMinutes", cfr_ldc_123());
        scriptFunction32.setArity(3);
        scriptFunction32.setDocumentationKey("Date.prototype.setMinutes");
        this.setMinutes = scriptFunction32;
        ScriptFunction scriptFunction33 = ScriptFunction.createBuiltin("setUTCMinutes", cfr_ldc_124());
        scriptFunction33.setArity(3);
        scriptFunction33.setDocumentationKey("Date.prototype.setUTCMinutes");
        this.setUTCMinutes = scriptFunction33;
        ScriptFunction scriptFunction34 = ScriptFunction.createBuiltin("setHours", cfr_ldc_125());
        scriptFunction34.setArity(4);
        scriptFunction34.setDocumentationKey("Date.prototype.setHours");
        this.setHours = scriptFunction34;
        ScriptFunction scriptFunction35 = ScriptFunction.createBuiltin("setUTCHours", cfr_ldc_126());
        scriptFunction35.setArity(4);
        scriptFunction35.setDocumentationKey("Date.prototype.setUTCHours");
        this.setUTCHours = scriptFunction35;
        ScriptFunction scriptFunction36 = ScriptFunction.createBuiltin("setDate", cfr_ldc_127());
        scriptFunction36.setArity(1);
        scriptFunction36.setDocumentationKey("Date.prototype.setDate");
        this.setDate = scriptFunction36;
        ScriptFunction scriptFunction37 = ScriptFunction.createBuiltin("setUTCDate", cfr_ldc_128());
        scriptFunction37.setArity(1);
        scriptFunction37.setDocumentationKey("Date.prototype.setUTCDate");
        this.setUTCDate = scriptFunction37;
        ScriptFunction scriptFunction38 = ScriptFunction.createBuiltin("setMonth", cfr_ldc_129());
        scriptFunction38.setArity(2);
        scriptFunction38.setDocumentationKey("Date.prototype.setMonth");
        this.setMonth = scriptFunction38;
        ScriptFunction scriptFunction39 = ScriptFunction.createBuiltin("setUTCMonth", cfr_ldc_130());
        scriptFunction39.setArity(2);
        scriptFunction39.setDocumentationKey("Date.prototype.setUTCMonth");
        this.setUTCMonth = scriptFunction39;
        ScriptFunction scriptFunction40 = ScriptFunction.createBuiltin("setFullYear", cfr_ldc_131());
        scriptFunction40.setArity(3);
        scriptFunction40.setDocumentationKey("Date.prototype.setFullYear");
        this.setFullYear = scriptFunction40;
        ScriptFunction scriptFunction41 = ScriptFunction.createBuiltin("setUTCFullYear", cfr_ldc_132());
        scriptFunction41.setArity(3);
        scriptFunction41.setDocumentationKey("Date.prototype.setUTCFullYear");
        this.setUTCFullYear = scriptFunction41;
        ScriptFunction scriptFunction42 = ScriptFunction.createBuiltin("setYear", cfr_ldc_133());
        scriptFunction42.setDocumentationKey("Date.prototype.setYear");
        this.setYear = scriptFunction42;
        ScriptFunction scriptFunction43 = ScriptFunction.createBuiltin("toUTCString", cfr_ldc_134());
        scriptFunction43.setDocumentationKey("Date.prototype.toUTCString");
        this.toUTCString = scriptFunction43;
        ScriptFunction scriptFunction44 = ScriptFunction.createBuiltin("toGMTString", cfr_ldc_135());
        scriptFunction44.setDocumentationKey("Date.prototype.toGMTString");
        this.toGMTString = scriptFunction44;
        ScriptFunction scriptFunction45 = ScriptFunction.createBuiltin("toISOString", cfr_ldc_136());
        scriptFunction45.setDocumentationKey("Date.prototype.toISOString");
        this.toISOString = scriptFunction45;
        ScriptFunction scriptFunction46 = ScriptFunction.createBuiltin("toJSON", cfr_ldc_137());
        scriptFunction46.setDocumentationKey("Date.prototype.toJSON");
        this.toJSON = scriptFunction46;
    }

    @Override
    public String getClassName() {
        return "Date";
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toDateString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toDateString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toTimeString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toTimeString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toLocaleString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toLocaleString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toLocaleDateString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toLocaleDateString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toLocaleTimeString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toLocaleTimeString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$valueOf", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$valueOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getTime", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getTime", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getFullYear", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getFullYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getUTCFullYear", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getUTCFullYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getYear", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getMonth", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getMonth", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getUTCMonth", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getUTCMonth", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getDate", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getDate", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getUTCDate", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getUTCDate", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getDay", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getDay", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getUTCDay", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getUTCDay", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getHours", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getHours", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getUTCHours", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getUTCHours", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getMinutes", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getMinutes", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getUTCMinutes", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getUTCMinutes", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getSeconds", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getSeconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getUTCSeconds", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getUTCSeconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getMilliseconds", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getMilliseconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getUTCMilliseconds", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getUTCMilliseconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$getTimezoneOffset", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$getTimezoneOffset", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setTime", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setTime", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setMilliseconds", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setMilliseconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setUTCMilliseconds", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setUTCMilliseconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setSeconds", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setSeconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setUTCSeconds", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setUTCSeconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setMinutes", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setMinutes", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setUTCMinutes", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setUTCMinutes", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_66() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setHours", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_67() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setHours", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_68() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setUTCHours", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_69() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setUTCHours", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_70() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setDate", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_71() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setDate", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_72() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setUTCDate", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_73() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setUTCDate", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_74() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setMonth", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_75() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setMonth", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_76() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setUTCMonth", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_77() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setUTCMonth", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_78() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setFullYear", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_79() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setFullYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_80() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setUTCFullYear", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_81() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setUTCFullYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_82() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$setYear", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_83() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$setYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_84() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toUTCString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_85() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toUTCString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_86() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toGMTString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_87() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toGMTString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_88() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toISOString", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_89() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toISOString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_90() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "G$toJSON", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_91() {
        try {
            return MethodHandles.lookup().findVirtual(NativeDate$Prototype.class, "S$toJSON", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_92() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_93() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toDateString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_94() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toTimeString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_95() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toLocaleString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_96() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toLocaleDateString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_97() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toLocaleTimeString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_98() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "valueOf", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_99() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getTime", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_100() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getFullYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_101() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getUTCFullYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_102() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_103() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getMonth", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_104() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getUTCMonth", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_105() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getDate", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_106() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getUTCDate", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_107() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getDay", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_108() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getUTCDay", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_109() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getHours", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_110() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getUTCHours", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_111() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getMinutes", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_112() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getUTCMinutes", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_113() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getSeconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_114() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getUTCSeconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_115() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getMilliseconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_116() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getUTCMilliseconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_117() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "getTimezoneOffset", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_118() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setTime", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_119() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setMilliseconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_120() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setUTCMilliseconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_121() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setSeconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_122() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setUTCSeconds", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_123() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setMinutes", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_124() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setUTCMinutes", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_125() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setHours", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_126() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setUTCHours", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_127() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setDate", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_128() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setUTCDate", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_129() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setMonth", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_130() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setUTCMonth", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_131() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setFullYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_132() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setUTCFullYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;[Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_133() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "setYear", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)D", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_134() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toUTCString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_135() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toGMTString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_136() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toISOString", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/String;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_137() {
        try {
            return MethodHandles.lookup().findStatic(NativeDate.class, "toJSON", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}
