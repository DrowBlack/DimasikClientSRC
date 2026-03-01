package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.LinkRequest;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.internal.objects.ArrayIterator;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.annotations.SpecializedFunction;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.Debug;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.OptimisticBuiltins;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyDescriptor;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Undefined;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayData;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayIndex;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import org.openjdk.nashorn.internal.runtime.arrays.ContinuousArrayData;
import org.openjdk.nashorn.internal.runtime.arrays.IteratorAction;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.linker.InvokeByName;

public final class NativeArray
extends ScriptObject
implements OptimisticBuiltins {
    private static final Object JOIN = new Object();
    private static final Object EVERY_CALLBACK_INVOKER = new Object();
    private static final Object SOME_CALLBACK_INVOKER = new Object();
    private static final Object FOREACH_CALLBACK_INVOKER = new Object();
    private static final Object MAP_CALLBACK_INVOKER = new Object();
    private static final Object FILTER_CALLBACK_INVOKER = new Object();
    private static final Object REDUCE_CALLBACK_INVOKER = new Object();
    private static final Object CALL_CMP = new Object();
    private static final Object TO_LOCALE_STRING = new Object();
    private static PropertyMap $nasgenmap$;

    NativeArray() {
        this(ArrayData.initialArray());
    }

    NativeArray(long length) {
        this(ArrayData.allocate(length));
    }

    NativeArray(int[] array) {
        this(ArrayData.allocate(array));
    }

    NativeArray(double[] array) {
        this(ArrayData.allocate(array));
    }

    NativeArray(long[] array) {
        this(ArrayData.allocate(array.length));
        ArrayData arrayData = this.getArray();
        Class<Object> widest = Integer.TYPE;
        for (int index = 0; index < array.length; ++index) {
            long value = array[index];
            if (widest == Integer.TYPE && JSType.isRepresentableAsInt(value)) {
                arrayData = arrayData.set(index, (int)value, false);
                continue;
            }
            if (widest != Object.class && JSType.isRepresentableAsDouble(value)) {
                arrayData = arrayData.set(index, value, false);
                widest = Double.TYPE;
                continue;
            }
            arrayData = arrayData.set(index, (Object)value, false);
            widest = Object.class;
        }
        this.setArray(arrayData);
    }

    NativeArray(Object[] array) {
        this(ArrayData.allocate(array.length));
        ArrayData arrayData = this.getArray();
        for (int index = 0; index < array.length; ++index) {
            Object value = array[index];
            arrayData = value == ScriptRuntime.EMPTY ? arrayData.delete(index) : arrayData.set(index, value, false);
        }
        this.setArray(arrayData);
    }

    NativeArray(ArrayData arrayData) {
        this(arrayData, Global.instance());
    }

    NativeArray(ArrayData arrayData, Global global) {
        super(global.getArrayPrototype(), $nasgenmap$);
        this.setArray(arrayData);
        this.setIsArray();
    }

    @Override
    protected GuardedInvocation findGetIndexMethod(CallSiteDescriptor desc, LinkRequest request) {
        GuardedInvocation inv = this.getArray().findFastGetIndexMethod(this.getArray().getClass(), desc, request);
        if (inv != null) {
            return inv;
        }
        return super.findGetIndexMethod(desc, request);
    }

    @Override
    protected GuardedInvocation findSetIndexMethod(CallSiteDescriptor desc, LinkRequest request) {
        GuardedInvocation inv = this.getArray().findFastSetIndexMethod(this.getArray().getClass(), desc, request);
        if (inv != null) {
            return inv;
        }
        return super.findSetIndexMethod(desc, request);
    }

    private static InvokeByName getJOIN() {
        return Global.instance().getInvokeByName(JOIN, () -> new InvokeByName("join", ScriptObject.class));
    }

    private static MethodHandle createIteratorCallbackInvoker(Object key, Class<?> rtype) {
        return Global.instance().getDynamicInvoker(key, () -> Bootstrap.createDynamicCallInvoker(rtype, Object.class, Object.class, Object.class, Double.TYPE, Object.class));
    }

    private static MethodHandle getEVERY_CALLBACK_INVOKER() {
        return NativeArray.createIteratorCallbackInvoker(EVERY_CALLBACK_INVOKER, Boolean.TYPE);
    }

    private static MethodHandle getSOME_CALLBACK_INVOKER() {
        return NativeArray.createIteratorCallbackInvoker(SOME_CALLBACK_INVOKER, Boolean.TYPE);
    }

    private static MethodHandle getFOREACH_CALLBACK_INVOKER() {
        return NativeArray.createIteratorCallbackInvoker(FOREACH_CALLBACK_INVOKER, Void.TYPE);
    }

    private static MethodHandle getMAP_CALLBACK_INVOKER() {
        return NativeArray.createIteratorCallbackInvoker(MAP_CALLBACK_INVOKER, Object.class);
    }

    private static MethodHandle getFILTER_CALLBACK_INVOKER() {
        return NativeArray.createIteratorCallbackInvoker(FILTER_CALLBACK_INVOKER, Boolean.TYPE);
    }

    private static MethodHandle getREDUCE_CALLBACK_INVOKER() {
        return Global.instance().getDynamicInvoker(REDUCE_CALLBACK_INVOKER, () -> Bootstrap.createDynamicCallInvoker(Object.class, Object.class, Undefined.class, Object.class, Object.class, Double.TYPE, Object.class));
    }

    private static MethodHandle getCALL_CMP() {
        return Global.instance().getDynamicInvoker(CALL_CMP, () -> Bootstrap.createDynamicCallInvoker(Double.TYPE, Object.class, Object.class, Object.class, Object.class));
    }

    private static InvokeByName getTO_LOCALE_STRING() {
        return Global.instance().getInvokeByName(TO_LOCALE_STRING, () -> new InvokeByName("toLocaleString", ScriptObject.class, String.class, new Class[0]));
    }

    @Override
    public String getClassName() {
        return "Array";
    }

    @Override
    public Object getLength() {
        long length = this.getArray().length();
        assert (length >= 0L);
        if (length <= Integer.MAX_VALUE) {
            return (int)length;
        }
        return length;
    }

    private boolean defineLength(long oldLen, PropertyDescriptor oldLenDesc, PropertyDescriptor desc, boolean reject) {
        boolean succeeded;
        boolean newWritable;
        if (!desc.has("value")) {
            return super.defineOwnProperty("length", desc, reject);
        }
        long newLen = NativeArray.validLength(desc.getValue());
        desc.setValue(JSType.toNarrowestNumber(newLen));
        if (newLen >= oldLen) {
            return super.defineOwnProperty("length", desc, reject);
        }
        if (!oldLenDesc.isWritable()) {
            if (reject) {
                throw ECMAErrors.typeError("property.not.writable", "length", ScriptRuntime.safeToString(this));
            }
            return false;
        }
        boolean bl = newWritable = !desc.has("writable") || desc.isWritable();
        if (!newWritable) {
            desc.setWritable(true);
        }
        if (!(succeeded = super.defineOwnProperty("length", desc, reject))) {
            return false;
        }
        long o = oldLen;
        while (newLen < o) {
            boolean deleteSucceeded;
            if (deleteSucceeded = this.delete(--o, false)) continue;
            desc.setValue(o + 1L);
            if (!newWritable) {
                desc.setWritable(false);
            }
            super.defineOwnProperty("length", desc, false);
            if (reject) {
                throw ECMAErrors.typeError("property.not.writable", "length", ScriptRuntime.safeToString(this));
            }
            return false;
        }
        if (!newWritable) {
            ScriptObject newDesc = Global.newEmptyInstance();
            newDesc.set((Object)"writable", (Object)false, 0);
            return super.defineOwnProperty("length", newDesc, false);
        }
        return true;
    }

    @Override
    public boolean defineOwnProperty(Object key, Object propertyDesc, boolean reject) {
        PropertyDescriptor desc = NativeArray.toPropertyDescriptor(Global.instance(), propertyDesc);
        PropertyDescriptor oldLenDesc = (PropertyDescriptor)super.getOwnPropertyDescriptor("length");
        long oldLen = JSType.toUint32(oldLenDesc.getValue());
        if ("length".equals(key)) {
            boolean result = this.defineLength(oldLen, oldLenDesc, desc, reject);
            if (desc.has("writable") && !desc.isWritable()) {
                this.setIsLengthNotWritable();
            }
            return result;
        }
        int index = ArrayIndex.getArrayIndex(key);
        if (ArrayIndex.isValidArrayIndex(index)) {
            long longIndex = ArrayIndex.toLongIndex(index);
            if (longIndex >= oldLen && !oldLenDesc.isWritable()) {
                if (reject) {
                    throw ECMAErrors.typeError("property.not.writable", Long.toString(longIndex), ScriptRuntime.safeToString(this));
                }
                return false;
            }
            boolean succeeded = super.defineOwnProperty(key, desc, false);
            if (!succeeded) {
                if (reject) {
                    throw ECMAErrors.typeError("cant.redefine.property", key.toString(), ScriptRuntime.safeToString(this));
                }
                return false;
            }
            if (longIndex >= oldLen) {
                oldLenDesc.setValue(longIndex + 1L);
                super.defineOwnProperty("length", oldLenDesc, false);
            }
            return true;
        }
        return super.defineOwnProperty(key, desc, reject);
    }

    @Override
    public final void defineOwnProperty(int index, Object value) {
        assert (ArrayIndex.isValidArrayIndex(index)) : "invalid array index";
        long longIndex = ArrayIndex.toLongIndex(index);
        if (longIndex >= this.getArray().length()) {
            this.setArray(this.getArray().ensure(longIndex));
        }
        this.setArray(this.getArray().set(index, value, false));
    }

    public Object[] asObjectArray() {
        return this.getArray().asObjectArray();
    }

    @Override
    public void setIsLengthNotWritable() {
        super.setIsLengthNotWritable();
        this.setArray(ArrayData.setIsLengthNotWritable(this.getArray()));
    }

    public static boolean isArray(Object self, Object arg) {
        return NativeArray.isArray(arg) || arg instanceof JSObject && ((JSObject)arg).isArray();
    }

    public static Object length(Object self) {
        if (NativeArray.isArray(self)) {
            long length = ((ScriptObject)self).getArray().length();
            assert (length >= 0L);
            if (length <= Integer.MAX_VALUE) {
                return (int)length;
            }
            return (double)length;
        }
        return 0;
    }

    public static void length(Object self, Object length) {
        if (NativeArray.isArray(self)) {
            ((ScriptObject)self).setLength(NativeArray.validLength(length));
        }
    }

    public static Object getProtoLength(Object self) {
        return NativeArray.length(self);
    }

    public static void setProtoLength(Object self, Object length) {
        NativeArray.length(self, length);
    }

    static long validLength(Object length) {
        double doubleLength = JSType.toNumber(length);
        if (doubleLength != (double)JSType.toUint32(length)) {
            throw ECMAErrors.rangeError("inappropriate.array.length", ScriptRuntime.safeToString(length));
        }
        return (long)doubleLength;
    }

    public static Object toString(Object self) {
        Object obj = Global.toObject(self);
        if (obj instanceof ScriptObject) {
            InvokeByName joinInvoker = NativeArray.getJOIN();
            ScriptObject sobj = (ScriptObject)obj;
            try {
                Object join = joinInvoker.getGetter().invokeExact(sobj);
                if (Bootstrap.isCallable(join)) {
                    return joinInvoker.getInvoker().invokeExact(join, sobj);
                }
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return ScriptRuntime.builtinObjectToString(self);
    }

    public static Object assertNumeric(Object self) {
        if (!(self instanceof NativeArray) || !((NativeArray)self).getArray().getOptimisticType().isNumeric()) {
            throw ECMAErrors.typeError("not.a.numeric.array", ScriptRuntime.safeToString(self));
        }
        return Boolean.TRUE;
    }

    public static String toLocaleString(Object self) {
        StringBuilder sb = new StringBuilder();
        ArrayLikeIterator<Object> iter = ArrayLikeIterator.arrayLikeIterator(self, true);
        while (iter.hasNext()) {
            block6: {
                Object obj = iter.next();
                if (obj != null && obj != ScriptRuntime.UNDEFINED) {
                    Object val = JSType.toScriptObject(obj);
                    try {
                        if (!(val instanceof ScriptObject)) break block6;
                        InvokeByName localeInvoker = NativeArray.getTO_LOCALE_STRING();
                        ScriptObject sobj = (ScriptObject)val;
                        Object toLocaleString = localeInvoker.getGetter().invokeExact(sobj);
                        if (Bootstrap.isCallable(toLocaleString)) {
                            sb.append(localeInvoker.getInvoker().invokeExact(toLocaleString, sobj));
                            break block6;
                        }
                        throw ECMAErrors.typeError("not.a.function", "toLocaleString");
                    }
                    catch (Error | RuntimeException t) {
                        throw t;
                    }
                    catch (Throwable t) {
                        throw new RuntimeException(t);
                    }
                }
            }
            if (!iter.hasNext()) continue;
            sb.append(",");
        }
        return sb.toString();
    }

    public static NativeArray construct(boolean newObj, Object self, Object ... args) {
        switch (args.length) {
            case 0: {
                return new NativeArray(0L);
            }
            case 1: {
                Object len = args[0];
                if (len instanceof Number) {
                    double numberLength;
                    long length;
                    if ((len instanceof Integer || len instanceof Long) && (length = ((Number)len).longValue()) >= 0L && length < 0xFFFFFFFFL) {
                        return new NativeArray(length);
                    }
                    length = JSType.toUint32(len);
                    if ((double)length != (numberLength = ((Number)len).doubleValue())) {
                        throw ECMAErrors.rangeError("inappropriate.array.length", JSType.toString(numberLength));
                    }
                    return new NativeArray(length);
                }
                return new NativeArray(new Object[]{args[0]});
            }
        }
        return new NativeArray(args);
    }

    public static NativeArray construct(boolean newObj, Object self) {
        return new NativeArray(0L);
    }

    public static Object construct(boolean newObj, Object self, boolean element) {
        return new NativeArray(new Object[]{element});
    }

    public static NativeArray construct(boolean newObj, Object self, int length) {
        if (length >= 0) {
            return new NativeArray(length);
        }
        return NativeArray.construct(newObj, self, new Object[]{length});
    }

    public static NativeArray construct(boolean newObj, Object self, long length) {
        if (length >= 0L && length <= 0xFFFFFFFFL) {
            return new NativeArray(length);
        }
        return NativeArray.construct(newObj, self, new Object[]{length});
    }

    public static NativeArray construct(boolean newObj, Object self, double length) {
        long uint32length = JSType.toUint32(length);
        if ((double)uint32length == length) {
            return new NativeArray(uint32length);
        }
        return NativeArray.construct(newObj, self, new Object[]{length});
    }

    public static NativeArray concat(Object self, int arg) {
        ContinuousArrayData newData = NativeArray.getContinuousArrayDataCCE(self, Integer.class).copy();
        newData.fastPush(arg);
        return new NativeArray(newData);
    }

    public static NativeArray concat(Object self, double arg) {
        ContinuousArrayData newData = NativeArray.getContinuousArrayDataCCE(self, Double.class).copy();
        newData.fastPush(arg);
        return new NativeArray(newData);
    }

    public static NativeArray concat(Object self, Object arg) {
        ContinuousArrayData newData;
        ContinuousArrayData selfData = NativeArray.getContinuousArrayDataCCE(self);
        if (arg instanceof NativeArray) {
            ContinuousArrayData argData = (ContinuousArrayData)((NativeArray)arg).getArray();
            if (argData.isEmpty()) {
                newData = selfData.copy();
            } else if (selfData.isEmpty()) {
                newData = argData.copy();
            } else {
                Class<?> widestElementType = selfData.widest(argData).getBoxedElementType();
                newData = ((ContinuousArrayData)selfData.convert(widestElementType)).fastConcat((ContinuousArrayData)argData.convert(widestElementType));
            }
        } else {
            newData = NativeArray.getContinuousArrayDataCCE(self, Object.class).copy();
            newData.fastPush(arg);
        }
        return new NativeArray(newData);
    }

    public static NativeArray concat(Object self, Object ... args) {
        ArrayList<Object> list = new ArrayList<Object>();
        NativeArray.concatToList(list, Global.toObject(self));
        for (Object obj : args) {
            NativeArray.concatToList(list, obj);
        }
        return new NativeArray(list.toArray());
    }

    private static void concatToList(ArrayList<Object> list, Object obj) {
        boolean isScriptObject;
        boolean isScriptArray = NativeArray.isArray(obj);
        boolean bl = isScriptObject = isScriptArray || obj instanceof ScriptObject;
        if (isScriptArray || obj instanceof Iterable || obj instanceof JSObject || obj != null && obj.getClass().isArray()) {
            ArrayLikeIterator<Object> iter = ArrayLikeIterator.arrayLikeIterator(obj, true);
            if (iter.hasNext()) {
                int i = 0;
                while (iter.hasNext()) {
                    Object value = iter.next();
                    if (value == ScriptRuntime.UNDEFINED && isScriptObject && !((ScriptObject)obj).has(i)) {
                        list.add(ScriptRuntime.EMPTY);
                    } else {
                        list.add(value);
                    }
                    ++i;
                }
            } else if (!isScriptArray) {
                list.add(obj);
            }
        } else {
            list.add(obj);
        }
    }

    public static String join(Object self, Object separator) {
        String sep;
        StringBuilder sb = new StringBuilder();
        ArrayLikeIterator<Object> iter = ArrayLikeIterator.arrayLikeIterator(self, true);
        String string = sep = separator == ScriptRuntime.UNDEFINED ? "," : JSType.toString(separator);
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj != null && obj != ScriptRuntime.UNDEFINED) {
                sb.append(JSType.toString(obj));
            }
            if (!iter.hasNext()) continue;
            sb.append(sep);
        }
        return sb.toString();
    }

    public static int popInt(Object self) {
        return NativeArray.getContinuousNonEmptyArrayDataCCE(self).fastPopInt();
    }

    public static double popDouble(Object self) {
        return NativeArray.getContinuousNonEmptyArrayDataCCE(self).fastPopDouble();
    }

    public static Object popObject(Object self) {
        return NativeArray.getContinuousArrayDataCCE(self, null).fastPopObject();
    }

    public static Object pop(Object self) {
        Object obj = Global.toObject(self);
        if (!(obj instanceof ScriptObject)) {
            return ScriptRuntime.UNDEFINED;
        }
        ScriptObject sobj = (ScriptObject)obj;
        if (NativeArray.bulkable(sobj)) {
            return sobj.getArray().pop();
        }
        long len = JSType.toUint32(sobj.getLength());
        if (len == 0L) {
            sobj.set((Object)"length", 0, 32);
            return ScriptRuntime.UNDEFINED;
        }
        long index = len - 1L;
        Object element = sobj.get(index);
        sobj.delete(index, true);
        sobj.set((Object)"length", (double)index, 32);
        return element;
    }

    public static double push(Object self, int arg) {
        return NativeArray.getContinuousArrayDataCCE(self, Integer.class).fastPush(arg);
    }

    public static double push(Object self, double arg) {
        return NativeArray.getContinuousArrayDataCCE(self, Double.class).fastPush(arg);
    }

    public static double pushObject(Object self, Object arg) {
        return NativeArray.getContinuousArrayDataCCE(self, Object.class).fastPush(arg);
    }

    public static Object push(Object self, Object ... args) {
        try {
            Object obj = Global.toObject(self);
            if (!(obj instanceof ScriptObject)) {
                return args.length;
            }
            ScriptObject sobj = (ScriptObject)obj;
            if (NativeArray.bulkable(sobj) && sobj.getArray().length() + (long)args.length <= 0xFFFFFFFFL) {
                ArrayData newData = sobj.getArray().push(true, args);
                sobj.setArray(newData);
                return JSType.toNarrowestNumber(newData.length());
            }
            long len = JSType.toUint32(sobj.getLength());
            for (Object element : args) {
                sobj.set((double)len++, element, 32);
            }
            sobj.set((Object)"length", (double)len, 32);
            return JSType.toNarrowestNumber(len);
        }
        catch (ClassCastException | NullPointerException e) {
            throw ECMAErrors.typeError(Context.getGlobal(), e, "not.an.object", ScriptRuntime.safeToString(self));
        }
    }

    public static double push(Object self, Object arg) {
        Object obj = Global.toObject(self);
        if (!(obj instanceof ScriptObject)) {
            return 1.0;
        }
        ScriptObject sobj = (ScriptObject)obj;
        ArrayData arrayData = sobj.getArray();
        long length = arrayData.length();
        if (NativeArray.bulkable(sobj) && length < 0xFFFFFFFFL) {
            sobj.setArray(arrayData.push(true, arg));
            return length + 1L;
        }
        long len = JSType.toUint32(sobj.getLength());
        sobj.set((double)len++, arg, 32);
        sobj.set((Object)"length", (double)len, 32);
        return len;
    }

    public static Object reverse(Object self) {
        Object obj = Global.toObject(self);
        if (!(obj instanceof ScriptObject)) {
            return obj;
        }
        ScriptObject sobj = (ScriptObject)obj;
        long len = JSType.toUint32(sobj.getLength());
        long middle = len / 2L;
        for (long lower = 0L; lower != middle; ++lower) {
            long upper = len - lower - 1L;
            Object lowerValue = sobj.get(lower);
            Object upperValue = sobj.get(upper);
            boolean lowerExists = sobj.has(lower);
            boolean upperExists = sobj.has(upper);
            if (lowerExists && upperExists) {
                sobj.set((double)lower, upperValue, 32);
                sobj.set((double)upper, lowerValue, 32);
                continue;
            }
            if (!lowerExists && upperExists) {
                sobj.set((double)lower, upperValue, 32);
                sobj.delete(upper, true);
                continue;
            }
            if (!lowerExists) continue;
            sobj.delete(lower, true);
            sobj.set((double)upper, lowerValue, 32);
        }
        return sobj;
    }

    public static Object shift(Object self) {
        Object obj = Global.toObject(self);
        Object first = ScriptRuntime.UNDEFINED;
        if (!(obj instanceof ScriptObject)) {
            return first;
        }
        ScriptObject sobj = (ScriptObject)obj;
        long len = JSType.toUint32(sobj.getLength());
        if (len > 0L) {
            first = sobj.get(0);
            if (NativeArray.bulkable(sobj)) {
                sobj.getArray().shiftLeft(1);
            } else {
                boolean hasPrevious = true;
                for (long k = 1L; k < len; ++k) {
                    boolean hasCurrent = sobj.has(k);
                    if (hasCurrent) {
                        sobj.set((double)(k - 1L), sobj.get(k), 32);
                    } else if (hasPrevious) {
                        sobj.delete(k - 1L, true);
                    }
                    hasPrevious = hasCurrent;
                }
            }
            sobj.delete(--len, true);
        } else {
            len = 0L;
        }
        sobj.set((Object)"length", (double)len, 32);
        return first;
    }

    public static Object slice(Object self, Object start, Object end) {
        long k;
        long finale;
        Object obj = Global.toObject(self);
        if (!(obj instanceof ScriptObject)) {
            return ScriptRuntime.UNDEFINED;
        }
        ScriptObject sobj = (ScriptObject)obj;
        long len = JSType.toUint32(sobj.getLength());
        long relativeStart = JSType.toLong(start);
        long relativeEnd = end == ScriptRuntime.UNDEFINED ? len : JSType.toLong(end);
        long l = finale = relativeEnd < 0L ? Math.max(len + relativeEnd, 0L) : Math.min(relativeEnd, len);
        if (k >= finale) {
            return new NativeArray(0L);
        }
        if (NativeArray.bulkable(sobj)) {
            return new NativeArray(sobj.getArray().slice(k, finale));
        }
        NativeArray copy = new NativeArray(finale - k);
        long n = 0L;
        for (k = relativeStart < 0L ? Math.max(len + relativeStart, 0L) : Math.min(relativeStart, len); k < finale; ++k) {
            if (sobj.has(k)) {
                copy.defineOwnProperty(ArrayIndex.getArrayIndex(n), sobj.get(k));
            }
            ++n;
        }
        return copy;
    }

    private static Object compareFunction(Object comparefn) {
        if (comparefn == ScriptRuntime.UNDEFINED) {
            return null;
        }
        if (!Bootstrap.isCallable(comparefn)) {
            throw ECMAErrors.typeError("not.a.function", ScriptRuntime.safeToString(comparefn));
        }
        return comparefn;
    }

    private static Object[] sort(Object[] array, Object comparefn) {
        final Object cmp = NativeArray.compareFunction(comparefn);
        List<Object> list = Arrays.asList(array);
        final Undefined cmpThis = cmp == null || Bootstrap.isStrictCallable(cmp) ? ScriptRuntime.UNDEFINED : Global.instance();
        try {
            list.sort(new Comparator<Object>(){
                private final MethodHandle call_cmp = NativeArray.getCALL_CMP();

                @Override
                public int compare(Object x, Object y) {
                    if (x == ScriptRuntime.UNDEFINED && y == ScriptRuntime.UNDEFINED) {
                        return 0;
                    }
                    if (x == ScriptRuntime.UNDEFINED) {
                        return 1;
                    }
                    if (y == ScriptRuntime.UNDEFINED) {
                        return -1;
                    }
                    if (cmp != null) {
                        try {
                            return (int)Math.signum(this.call_cmp.invokeExact(cmp, cmpThis, x, y));
                        }
                        catch (Error | RuntimeException e) {
                            throw e;
                        }
                        catch (Throwable t) {
                            throw new RuntimeException(t);
                        }
                    }
                    return JSType.toString(x).compareTo(JSType.toString(y));
                }
            });
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return list.toArray(new Object[0]);
    }

    public static ScriptObject sort(Object self, Object comparefn) {
        try {
            ScriptObject sobj = (ScriptObject)self;
            long len = JSType.toUint32(sobj.getLength());
            ArrayData array = sobj.getArray();
            if (len > 1L) {
                long index;
                ArrayList<Object> src = new ArrayList<Object>();
                Iterator<Long> iter = array.indexIterator();
                while (iter.hasNext() && (index = iter.next().longValue()) < len) {
                    src.add(array.getObject((int)index));
                }
                Object[] sorted = NativeArray.sort(src.toArray(), comparefn);
                for (int i = 0; i < sorted.length; ++i) {
                    array = array.set(i, sorted[i], true);
                }
                if ((long)sorted.length != len) {
                    array = array.delete(sorted.length, len - 1L);
                }
                sobj.setArray(array);
            }
            return sobj;
        }
        catch (ClassCastException | NullPointerException e) {
            throw ECMAErrors.typeError("not.an.object", ScriptRuntime.safeToString(self));
        }
    }

    public static Object splice(Object self, Object ... args) {
        NativeArray returnValue;
        long actualDeleteCount;
        Object obj = Global.toObject(self);
        if (!(obj instanceof ScriptObject)) {
            return ScriptRuntime.UNDEFINED;
        }
        ScriptObject sobj = (ScriptObject)obj;
        long len = JSType.toUint32(sobj.getLength());
        long relativeStart = JSType.toLong(args.length > 0 ? args[0] : ScriptRuntime.UNDEFINED);
        long actualStart = relativeStart < 0L ? Math.max(len + relativeStart, 0L) : Math.min(relativeStart, len);
        Object[] items = ScriptRuntime.EMPTY_ARRAY;
        if (args.length == 0) {
            actualDeleteCount = 0L;
        } else if (args.length == 1) {
            actualDeleteCount = len - actualStart;
        } else {
            actualDeleteCount = Math.min(Math.max(JSType.toLong(args[1]), 0L), len - actualStart);
            if (args.length > 2) {
                items = new Object[args.length - 2];
                System.arraycopy(args, 2, items, 0, items.length);
            }
        }
        if (actualStart <= Integer.MAX_VALUE && actualDeleteCount <= Integer.MAX_VALUE && NativeArray.bulkable(sobj)) {
            try {
                returnValue = new NativeArray(sobj.getArray().fastSplice((int)actualStart, (int)actualDeleteCount, items.length));
                int k = (int)actualStart;
                int i = 0;
                while (i < items.length) {
                    sobj.defineOwnProperty(k, items[i]);
                    ++i;
                    ++k;
                }
            }
            catch (UnsupportedOperationException uoe) {
                returnValue = NativeArray.slowSplice(sobj, actualStart, actualDeleteCount, items, len);
            }
        } else {
            returnValue = NativeArray.slowSplice(sobj, actualStart, actualDeleteCount, items, len);
        }
        return returnValue;
    }

    private static NativeArray slowSplice(ScriptObject sobj, long start, long deleteCount, Object[] items, long len) {
        long to;
        long from;
        long k;
        NativeArray array = new NativeArray(deleteCount);
        for (k = 0L; k < deleteCount; ++k) {
            from = start + k;
            if (!sobj.has(from)) continue;
            array.defineOwnProperty(ArrayIndex.getArrayIndex(k), sobj.get(from));
        }
        if ((long)items.length < deleteCount) {
            for (k = start; k < len - deleteCount; ++k) {
                from = k + deleteCount;
                to = k + (long)items.length;
                if (sobj.has(from)) {
                    sobj.set((double)to, sobj.get(from), 32);
                    continue;
                }
                sobj.delete(to, true);
            }
            for (k = len; k > len - deleteCount + (long)items.length; --k) {
                sobj.delete(k - 1L, true);
            }
        } else if ((long)items.length > deleteCount) {
            for (k = len - deleteCount; k > start; --k) {
                from = k + deleteCount - 1L;
                to = k + (long)items.length - 1L;
                if (sobj.has(from)) {
                    Object fromValue = sobj.get(from);
                    sobj.set((double)to, fromValue, 32);
                    continue;
                }
                sobj.delete(to, true);
            }
        }
        k = start;
        int i = 0;
        while (i < items.length) {
            sobj.set((double)k, items[i], 32);
            ++i;
            ++k;
        }
        long newLength = len - deleteCount + (long)items.length;
        sobj.set((Object)"length", (double)newLength, 32);
        return array;
    }

    public static Object unshift(Object self, Object ... items) {
        int j;
        Object obj = Global.toObject(self);
        if (!(obj instanceof ScriptObject)) {
            return ScriptRuntime.UNDEFINED;
        }
        ScriptObject sobj = (ScriptObject)obj;
        long len = JSType.toUint32(sobj.getLength());
        if (items == null) {
            return ScriptRuntime.UNDEFINED;
        }
        if (NativeArray.bulkable(sobj)) {
            sobj.getArray().shiftRight(items.length);
            for (j = 0; j < items.length; ++j) {
                sobj.setArray(sobj.getArray().set(j, items[j], true));
            }
        } else {
            for (long k = len; k > 0L; --k) {
                long from = k - 1L;
                long to = k + (long)items.length - 1L;
                if (sobj.has(from)) {
                    Object fromValue = sobj.get(from);
                    sobj.set((double)to, fromValue, 32);
                    continue;
                }
                sobj.delete(to, true);
            }
            for (j = 0; j < items.length; ++j) {
                sobj.set(j, items[j], 32);
            }
        }
        long newLength = len + (long)items.length;
        sobj.set((Object)"length", (double)newLength, 32);
        return JSType.toNarrowestNumber(newLength);
    }

    public static double indexOf(Object self, Object searchElement, Object fromIndex) {
        try {
            ScriptObject sobj = (ScriptObject)Global.toObject(self);
            long len = JSType.toUint32(sobj.getLength());
            if (len == 0L) {
                return -1.0;
            }
            long n = JSType.toLong(fromIndex);
            if (n >= len) {
                return -1.0;
            }
            for (long k = Math.max(0L, n < 0L ? len - Math.abs(n) : n); k < len; ++k) {
                if (!sobj.has(k) || !ScriptRuntime.EQ_STRICT(sobj.get(k), searchElement)) continue;
                return k;
            }
        }
        catch (ClassCastException | NullPointerException runtimeException) {
            // empty catch block
        }
        return -1.0;
    }

    public static double lastIndexOf(Object self, Object ... args) {
        try {
            long k;
            ScriptObject sobj = (ScriptObject)Global.toObject(self);
            long len = JSType.toUint32(sobj.getLength());
            if (len == 0L) {
                return -1.0;
            }
            Undefined searchElement = args.length > 0 ? args[0] : ScriptRuntime.UNDEFINED;
            long n = args.length > 1 ? JSType.toLong(args[1]) : len - 1L;
            long l = k = n < 0L ? len - Math.abs(n) : Math.min(n, len - 1L);
            while (k >= 0L) {
                if (sobj.has(k) && ScriptRuntime.EQ_STRICT(sobj.get(k), searchElement)) {
                    return k;
                }
                --k;
            }
        }
        catch (ClassCastException | NullPointerException e) {
            throw ECMAErrors.typeError("not.an.object", ScriptRuntime.safeToString(self));
        }
        return -1.0;
    }

    public static boolean every(Object self, Object callbackfn, Object thisArg) {
        return NativeArray.applyEvery(Global.toObject(self), callbackfn, thisArg);
    }

    private static boolean applyEvery(Object self, Object callbackfn, Object thisArg) {
        return (Boolean)new IteratorAction<Boolean>(Global.toObject(self), callbackfn, thisArg, Boolean.valueOf(true)){
            private final MethodHandle everyInvoker = NativeArray.getEVERY_CALLBACK_INVOKER();

            @Override
            protected boolean forEach(Object val, double i) throws Throwable {
                this.result = this.everyInvoker.invokeExact(this.callbackfn, this.thisArg, val, i, this.self);
                return (Boolean)this.result;
            }
        }.apply();
    }

    public static boolean some(Object self, Object callbackfn, Object thisArg) {
        return (Boolean)new IteratorAction<Boolean>(Global.toObject(self), callbackfn, thisArg, Boolean.valueOf(false)){
            private final MethodHandle someInvoker = NativeArray.getSOME_CALLBACK_INVOKER();

            @Override
            protected boolean forEach(Object val, double i) throws Throwable {
                this.result = this.someInvoker.invokeExact(this.callbackfn, this.thisArg, val, i, this.self);
                return !((Boolean)this.result).booleanValue();
            }
        }.apply();
    }

    public static Object forEach(Object self, Object callbackfn, Object thisArg) {
        return new IteratorAction<Object>(Global.toObject(self), callbackfn, thisArg, (Object)ScriptRuntime.UNDEFINED){
            private final MethodHandle forEachInvoker = NativeArray.getFOREACH_CALLBACK_INVOKER();

            @Override
            protected boolean forEach(Object val, double i) throws Throwable {
                this.forEachInvoker.invokeExact(this.callbackfn, this.thisArg, val, i, this.self);
                return true;
            }
        }.apply();
    }

    public static NativeArray map(Object self, Object callbackfn, Object thisArg) {
        return (NativeArray)new IteratorAction<NativeArray>(Global.toObject(self), callbackfn, thisArg, null){
            private final MethodHandle mapInvoker = NativeArray.getMAP_CALLBACK_INVOKER();

            @Override
            protected boolean forEach(Object val, double i) throws Throwable {
                Object r = this.mapInvoker.invokeExact(this.callbackfn, this.thisArg, val, i, this.self);
                ((NativeArray)this.result).defineOwnProperty(ArrayIndex.getArrayIndex(this.index), r);
                return true;
            }

            @Override
            public void applyLoopBegin(ArrayLikeIterator<Object> iter0) {
                this.result = new NativeArray(iter0.getLength());
            }
        }.apply();
    }

    public static NativeArray filter(Object self, Object callbackfn, Object thisArg) {
        return (NativeArray)new IteratorAction<NativeArray>(Global.toObject(self), callbackfn, thisArg, new NativeArray()){
            private long to = 0L;
            private final MethodHandle filterInvoker = NativeArray.getFILTER_CALLBACK_INVOKER();

            @Override
            protected boolean forEach(Object val, double i) throws Throwable {
                if (this.filterInvoker.invokeExact(this.callbackfn, this.thisArg, val, i, this.self)) {
                    ((NativeArray)this.result).defineOwnProperty(ArrayIndex.getArrayIndex(this.to++), val);
                }
                return true;
            }
        }.apply();
    }

    private static Object reduceInner(ArrayLikeIterator<Object> iter, Object self, Object ... args) {
        Undefined initialValue;
        Undefined callbackfn = args.length > 0 ? args[0] : ScriptRuntime.UNDEFINED;
        boolean initialValuePresent = args.length > 1;
        Undefined undefined = initialValue = initialValuePresent ? args[1] : ScriptRuntime.UNDEFINED;
        if (callbackfn == ScriptRuntime.UNDEFINED) {
            throw ECMAErrors.typeError("not.a.function", "undefined");
        }
        if (!initialValuePresent) {
            if (iter.hasNext()) {
                initialValue = iter.next();
            } else {
                throw ECMAErrors.typeError("array.reduce.invalid.init", new String[0]);
            }
        }
        return new IteratorAction<Object>(Global.toObject(self), (Object)callbackfn, (Object)ScriptRuntime.UNDEFINED, (Object)initialValue, iter){
            private final MethodHandle reduceInvoker = NativeArray.getREDUCE_CALLBACK_INVOKER();

            @Override
            protected boolean forEach(Object val, double i) throws Throwable {
                this.result = this.reduceInvoker.invokeExact(this.callbackfn, ScriptRuntime.UNDEFINED, this.result, val, i, this.self);
                return true;
            }
        }.apply();
    }

    public static Object reduce(Object self, Object ... args) {
        return NativeArray.reduceInner(ArrayLikeIterator.arrayLikeIterator(self), self, args);
    }

    public static Object reduceRight(Object self, Object ... args) {
        return NativeArray.reduceInner(ArrayLikeIterator.reverseArrayLikeIterator(self), self, args);
    }

    public static Object entries(Object self) {
        return ArrayIterator.newArrayKeyValueIterator(self);
    }

    public static Object keys(Object self) {
        return ArrayIterator.newArrayKeyIterator(self);
    }

    public static Object values(Object self) {
        return ArrayIterator.newArrayValueIterator(self);
    }

    public static Object getIterator(Object self) {
        return ArrayIterator.newArrayValueIterator(self);
    }

    private static boolean bulkable(ScriptObject self) {
        return self.isArray() && !NativeArray.hasInheritedArrayEntries(self) && !self.isLengthNotWritable();
    }

    private static boolean hasInheritedArrayEntries(ScriptObject self) {
        for (ScriptObject proto = self.getProto(); proto != null; proto = proto.getProto()) {
            if (!proto.hasArrayEntries()) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        return "NativeArray@" + Debug.id(this) + " [" + this.getArray().getClass().getSimpleName() + "]";
    }

    @Override
    public SpecializedFunction.LinkLogic getLinkLogic(Class<? extends SpecializedFunction.LinkLogic> clazz) {
        if (clazz == PushLinkLogic.class) {
            return PushLinkLogic.INSTANCE;
        }
        if (clazz == PopLinkLogic.class) {
            return PopLinkLogic.INSTANCE;
        }
        if (clazz == ConcatLinkLogic.class) {
            return ConcatLinkLogic.INSTANCE;
        }
        return null;
    }

    @Override
    public boolean hasPerInstanceAssumptions() {
        return true;
    }

    private static ContinuousArrayData getContinuousNonEmptyArrayDataCCE(Object self) {
        try {
            ContinuousArrayData data = (ContinuousArrayData)((NativeArray)self).getArray();
            if (data.length() != 0L) {
                return data;
            }
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        throw new ClassCastException();
    }

    private static ContinuousArrayData getContinuousArrayDataCCE(Object self) {
        try {
            return (ContinuousArrayData)((NativeArray)self).getArray();
        }
        catch (NullPointerException e) {
            throw new ClassCastException();
        }
    }

    private static ContinuousArrayData getContinuousArrayDataCCE(Object self, Class<?> elementType) {
        try {
            return (ContinuousArrayData)((NativeArray)self).getArray(elementType);
        }
        catch (NullPointerException e) {
            throw new ClassCastException();
        }
    }

    static {
        NativeArray.$clinit$();
    }

    public static void $clinit$() {
        ArrayList<Property> arrayList = new ArrayList<Property>(1);
        arrayList.add(AccessorProperty.create("length", 6, cfr_ldc_0(), cfr_ldc_1()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findStatic(NativeArray.class, "length", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)Ljava/lang/Object;", null));
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
            return MethodHandles.lookup().findStatic(NativeArray.class, "length", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    private static final class PopLinkLogic
    extends ArrayLinkLogic {
        private static final SpecializedFunction.LinkLogic INSTANCE = new PopLinkLogic();

        private PopLinkLogic() {
        }

        @Override
        public boolean canLink(Object self, CallSiteDescriptor desc, LinkRequest request) {
            ContinuousArrayData data = PopLinkLogic.getContinuousNonEmptyArrayData(self);
            if (data != null) {
                Class<?> elementType = data.getElementType();
                TypeDescriptor.OfField returnType = desc.getMethodType().returnType();
                boolean typeFits = JSType.getAccessorTypeIndex(returnType) >= JSType.getAccessorTypeIndex(elementType);
                return typeFits;
            }
            return false;
        }

        private static ContinuousArrayData getContinuousNonEmptyArrayData(Object self) {
            ContinuousArrayData data = PopLinkLogic.getContinuousArrayData(self);
            if (data != null) {
                return data.length() == 0L ? null : data;
            }
            return null;
        }
    }

    private static final class PushLinkLogic
    extends ArrayLinkLogic {
        private static final SpecializedFunction.LinkLogic INSTANCE = new PushLinkLogic();

        private PushLinkLogic() {
        }

        @Override
        public boolean canLink(Object self, CallSiteDescriptor desc, LinkRequest request) {
            return PushLinkLogic.getContinuousArrayData(self) != null;
        }
    }

    private static final class ConcatLinkLogic
    extends ArrayLinkLogic {
        private static final SpecializedFunction.LinkLogic INSTANCE = new ConcatLinkLogic();

        private ConcatLinkLogic() {
        }

        @Override
        public boolean canLink(Object self, CallSiteDescriptor desc, LinkRequest request) {
            Object[] args = request.getArguments();
            if (args.length != 3) {
                return false;
            }
            ContinuousArrayData selfData = ConcatLinkLogic.getContinuousArrayData(self);
            if (selfData == null) {
                return false;
            }
            Object arg = args[2];
            if (arg instanceof NativeArray) {
                return ConcatLinkLogic.getContinuousArrayData(arg) != null;
            }
            return JSType.isPrimitive(arg);
        }
    }

    private static abstract class ArrayLinkLogic
    extends SpecializedFunction.LinkLogic {
        protected ArrayLinkLogic() {
        }

        protected static ContinuousArrayData getContinuousArrayData(Object self) {
            try {
                return (ContinuousArrayData)((NativeArray)self).getArray();
            }
            catch (Exception e) {
                return null;
            }
        }

        @Override
        public Class<? extends Throwable> getRelinkException() {
            return ClassCastException.class;
        }
    }
}
