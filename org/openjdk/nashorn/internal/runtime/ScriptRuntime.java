package org.openjdk.nashorn.internal.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.SwitchPoint;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import jdk.dynalink.beans.BeansLinker;
import jdk.dynalink.beans.StaticClass;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.codegen.ApplySpecialization;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.ir.debug.JSONWriter;
import org.openjdk.nashorn.internal.objects.AbstractIterator;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.NativeJava;
import org.openjdk.nashorn.internal.objects.NativeObject;
import org.openjdk.nashorn.internal.parser.Lexer;
import org.openjdk.nashorn.internal.runtime.ConsString;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.FindProperty;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.Undefined;
import org.openjdk.nashorn.internal.runtime.WithObject;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayIndex;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.linker.InvokeByName;

public final class ScriptRuntime {
    public static final Object[] EMPTY_ARRAY = new Object[0];
    public static final Undefined UNDEFINED = Undefined.getUndefined();
    public static final Undefined EMPTY = Undefined.getEmpty();
    public static final CompilerConstants.Call ADD = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "ADD", Object.class, Object.class, Object.class);
    public static final CompilerConstants.Call EQ_STRICT = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "EQ_STRICT", Boolean.TYPE, Object.class, Object.class);
    public static final CompilerConstants.Call OPEN_WITH = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "openWith", ScriptObject.class, ScriptObject.class, Object.class);
    public static final CompilerConstants.Call MERGE_SCOPE = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "mergeScope", ScriptObject.class, ScriptObject.class);
    public static final CompilerConstants.Call TO_PROPERTY_ITERATOR = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "toPropertyIterator", Iterator.class, Object.class);
    public static final CompilerConstants.Call TO_VALUE_ITERATOR = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "toValueIterator", Iterator.class, Object.class);
    public static final CompilerConstants.Call TO_ES6_ITERATOR = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "toES6Iterator", Iterator.class, Object.class);
    public static final CompilerConstants.Call APPLY = CompilerConstants.staticCall(MethodHandles.lookup(), ScriptRuntime.class, "apply", Object.class, ScriptFunction.class, Object.class, Object[].class);
    public static final CompilerConstants.Call THROW_REFERENCE_ERROR = CompilerConstants.staticCall(MethodHandles.lookup(), ScriptRuntime.class, "throwReferenceError", Void.TYPE, String.class);
    public static final CompilerConstants.Call THROW_CONST_TYPE_ERROR = CompilerConstants.staticCall(MethodHandles.lookup(), ScriptRuntime.class, "throwConstTypeError", Void.TYPE, String.class);
    public static final CompilerConstants.Call INVALIDATE_RESERVED_BUILTIN_NAME = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "invalidateReservedBuiltinName", Void.TYPE, String.class);
    public static final CompilerConstants.Call STRICT_FAIL_DELETE = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "strictFailDelete", Boolean.TYPE, String.class);
    public static final CompilerConstants.Call SLOW_DELETE = CompilerConstants.staticCallNoLookup(ScriptRuntime.class, "slowDelete", Boolean.TYPE, ScriptObject.class, String.class);

    private ScriptRuntime() {
    }

    public static int switchTagAsInt(Object tag, int deflt) {
        double d;
        if (tag instanceof Number && JSType.isRepresentableAsInt(d = ((Number)tag).doubleValue())) {
            return (int)d;
        }
        return deflt;
    }

    public static int switchTagAsInt(boolean tag, int deflt) {
        return deflt;
    }

    public static int switchTagAsInt(long tag, int deflt) {
        return JSType.isRepresentableAsInt(tag) ? (int)tag : deflt;
    }

    public static int switchTagAsInt(double tag, int deflt) {
        return JSType.isRepresentableAsInt(tag) ? (int)tag : deflt;
    }

    public static String builtinObjectToString(Object self) {
        String className;
        JSType type = JSType.ofNoFunction(self);
        switch (type) {
            case BOOLEAN: {
                className = "Boolean";
                break;
            }
            case NUMBER: {
                className = "Number";
                break;
            }
            case STRING: {
                className = "String";
                break;
            }
            case NULL: {
                className = "Null";
                break;
            }
            case UNDEFINED: {
                className = "Undefined";
                break;
            }
            case OBJECT: {
                if (self instanceof ScriptObject) {
                    className = ((ScriptObject)self).getClassName();
                    break;
                }
                if (self instanceof JSObject) {
                    className = ((JSObject)self).getClassName();
                    break;
                }
                className = self.getClass().getName();
                break;
            }
            default: {
                className = self.getClass().getName();
            }
        }
        return "[object " + className + "]";
    }

    public static String safeToString(Object obj) {
        return JSType.toStringImpl(obj, true);
    }

    public static Iterator<?> toPropertyIterator(Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).propertyIterator();
        }
        if (obj != null && obj.getClass().isArray()) {
            return new RangeIterator(Array.getLength(obj));
        }
        if (obj instanceof JSObject) {
            return ((JSObject)obj).keySet().iterator();
        }
        if (obj instanceof List) {
            return new RangeIterator(((List)obj).size());
        }
        if (obj instanceof Map) {
            return ((Map)obj).keySet().iterator();
        }
        Object wrapped = Global.instance().wrapAsObject(obj);
        if (wrapped instanceof ScriptObject) {
            return ((ScriptObject)wrapped).propertyIterator();
        }
        return Collections.emptyIterator();
    }

    private static Iterator<?> iteratorForJavaArrayOrList(final Object obj) {
        if (obj != null && obj.getClass().isArray()) {
            final int length = Array.getLength(obj);
            return new Iterator<Object>(){
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return this.index < length;
                }

                @Override
                public Object next() {
                    if (this.index >= length) {
                        throw new NoSuchElementException();
                    }
                    return Array.get(obj, this.index++);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove");
                }
            };
        }
        if (obj instanceof Iterable) {
            return ((Iterable)obj).iterator();
        }
        return null;
    }

    public static Iterator<?> toValueIterator(Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).valueIterator();
        }
        if (obj instanceof JSObject) {
            return ((JSObject)obj).values().iterator();
        }
        Iterator<?> itr = ScriptRuntime.iteratorForJavaArrayOrList(obj);
        if (itr != null) {
            return itr;
        }
        if (obj instanceof Map) {
            return ((Map)obj).values().iterator();
        }
        Object wrapped = Global.instance().wrapAsObject(obj);
        if (wrapped instanceof ScriptObject) {
            return ((ScriptObject)wrapped).valueIterator();
        }
        return Collections.emptyIterator();
    }

    public static Iterator<?> toES6Iterator(final Object obj) {
        if (!(obj instanceof ScriptObject)) {
            Iterator<?> itr = ScriptRuntime.iteratorForJavaArrayOrList(obj);
            if (itr != null) {
                return itr;
            }
            if (obj instanceof Map) {
                return new Iterator<Object>(){
                    private final Iterator<?> iter;
                    {
                        this.iter = ((Map)obj).entrySet().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }

                    @Override
                    public Object next() {
                        Map.Entry next = (Map.Entry)this.iter.next();
                        Object[] keyvalue = new Object[]{next.getKey(), next.getValue()};
                        return NativeJava.from(null, keyvalue);
                    }

                    @Override
                    public void remove() {
                        this.iter.remove();
                    }
                };
            }
        }
        Global global = Global.instance();
        final Object iterator = AbstractIterator.getIterator(Global.toObject(obj), global);
        final InvokeByName nextInvoker = AbstractIterator.getNextInvoker(global);
        final MethodHandle doneInvoker = AbstractIterator.getDoneInvoker(global);
        final MethodHandle valueInvoker = AbstractIterator.getValueInvoker(global);
        return new Iterator<Object>(){
            private Object nextResult = this.nextResult();

            private Object nextResult() {
                try {
                    Object next = nextInvoker.getGetter().invokeExact(iterator);
                    if (Bootstrap.isCallable(next)) {
                        return nextInvoker.getInvoker().invokeExact(next, iterator, null);
                    }
                }
                catch (Error | RuntimeException r) {
                    throw r;
                }
                catch (Throwable t) {
                    throw new RuntimeException(t);
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                if (this.nextResult == null) {
                    return false;
                }
                try {
                    Object done = doneInvoker.invokeExact(this.nextResult);
                    return !JSType.toBoolean(done);
                }
                catch (Error | RuntimeException r) {
                    throw r;
                }
                catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }

            @Override
            public Object next() {
                if (this.nextResult == null) {
                    return Undefined.getUndefined();
                }
                try {
                    Object result = this.nextResult;
                    this.nextResult = this.nextResult();
                    return valueInvoker.invokeExact(result);
                }
                catch (Error | RuntimeException r) {
                    throw r;
                }
                catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }

    public static ScriptObject mergeScope(ScriptObject scope) {
        ScriptObject parentScope = scope.getProto();
        parentScope.addBoundProperties(scope);
        return parentScope;
    }

    public static Object apply(ScriptFunction target, Object self, Object ... args) {
        try {
            return target.invoke(self, args);
        }
        catch (Error | RuntimeException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void throwReferenceError(String name) {
        throw ECMAErrors.referenceError("not.defined", name);
    }

    public static void throwConstTypeError(String name) {
        throw ECMAErrors.typeError("assign.constant", name);
    }

    public static Object construct(ScriptFunction target, Object ... args) {
        try {
            return target.construct(args);
        }
        catch (Error | RuntimeException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static boolean sameValue(Object x, Object y) {
        JSType yType;
        JSType xType = JSType.ofNoFunction(x);
        if (xType != (yType = JSType.ofNoFunction(y))) {
            return false;
        }
        if (xType == JSType.UNDEFINED || xType == JSType.NULL) {
            return true;
        }
        if (xType == JSType.NUMBER) {
            double xVal = ((Number)x).doubleValue();
            double yVal = ((Number)y).doubleValue();
            if (Double.isNaN(xVal) && Double.isNaN(yVal)) {
                return true;
            }
            if (xVal == 0.0 && Double.doubleToLongBits(xVal) != Double.doubleToLongBits(yVal)) {
                return false;
            }
            return xVal == yVal;
        }
        if (xType == JSType.STRING || yType == JSType.BOOLEAN) {
            return x.equals(y);
        }
        return x == y;
    }

    public static String parse(String code, String name, boolean includeLoc) {
        return JSONWriter.parse(Context.getContextTrusted(), code, name, includeLoc);
    }

    public static boolean isJSWhitespace(char ch) {
        return Lexer.isJSWhitespace(ch);
    }

    public static ScriptObject openWith(ScriptObject scope, Object expression) {
        Global global = Context.getGlobal();
        if (expression == UNDEFINED) {
            throw ECMAErrors.typeError(global, "cant.apply.with.to.undefined", new String[0]);
        }
        if (expression == null) {
            throw ECMAErrors.typeError(global, "cant.apply.with.to.null", new String[0]);
        }
        if (expression instanceof ScriptObjectMirror) {
            Object unwrapped = ScriptObjectMirror.unwrap(expression, global);
            if (unwrapped instanceof ScriptObject) {
                return new WithObject(scope, (ScriptObject)unwrapped);
            }
            ScriptObject exprObj = global.newObject();
            NativeObject.bindAllProperties(exprObj, (ScriptObjectMirror)expression);
            return new WithObject(scope, exprObj);
        }
        Object wrappedExpr = JSType.toScriptObject(global, expression);
        if (wrappedExpr instanceof ScriptObject) {
            return new WithObject(scope, (ScriptObject)wrappedExpr);
        }
        throw ECMAErrors.typeError(global, "cant.apply.with.to.non.scriptobject", new String[0]);
    }

    public static Object ADD(Object x, Object y) {
        boolean yIsUndefined;
        boolean xIsNumber = x instanceof Number;
        boolean yIsNumber = y instanceof Number;
        if (xIsNumber && yIsNumber) {
            return ((Number)x).doubleValue() + ((Number)y).doubleValue();
        }
        boolean xIsUndefined = x == UNDEFINED;
        boolean bl = yIsUndefined = y == UNDEFINED;
        if (xIsNumber && yIsUndefined || xIsUndefined && yIsNumber || xIsUndefined && yIsUndefined) {
            return Double.NaN;
        }
        Object xPrim = JSType.toPrimitive(x);
        Object yPrim = JSType.toPrimitive(y);
        if (JSType.isString(xPrim) || JSType.isString(yPrim)) {
            try {
                return new ConsString(JSType.toCharSequence(xPrim), JSType.toCharSequence(yPrim));
            }
            catch (IllegalArgumentException iae) {
                throw ECMAErrors.rangeError(iae, "concat.string.too.big", new String[0]);
            }
        }
        return JSType.toNumber(xPrim) + JSType.toNumber(yPrim);
    }

    public static Object DEBUGGER() {
        return UNDEFINED;
    }

    public static Object NEW(Object clazz, Object ... args) {
        return UNDEFINED;
    }

    public static Object TYPEOF(Object object, Object property) {
        Object obj = object;
        if (property != null) {
            if (obj instanceof ScriptObject) {
                assert (property instanceof String);
                ScriptObject sobj = (ScriptObject)obj;
                FindProperty find = sobj.findProperty(property, true, true, sobj);
                if (Global.isLocationPropertyPlaceholder(obj = find != null ? find.getObjectValue() : sobj.invokeNoSuchProperty(property, false, -1))) {
                    obj = CompilerConstants.__LINE__.name().equals(property) ? Integer.valueOf(0) : "";
                }
            } else if (object instanceof Undefined) {
                obj = ((Undefined)obj).get(property);
            } else {
                if (object == null) {
                    throw ECMAErrors.typeError("cant.get.property", ScriptRuntime.safeToString(property), "null");
                }
                obj = JSType.isPrimitive(obj) ? ((ScriptObject)JSType.toScriptObject(obj)).get(property) : (obj instanceof JSObject ? ((JSObject)obj).getMember(property.toString()) : UNDEFINED);
            }
        }
        return JSType.of(obj).typeName();
    }

    public static Object REFERENCE_ERROR(Object lhs, Object rhs, Object msg) {
        throw ECMAErrors.referenceError("cant.be.used.as.lhs", Objects.toString(msg));
    }

    public static boolean slowDelete(ScriptObject obj, String property) {
        for (ScriptObject sobj = obj; sobj != null && sobj.isScope(); sobj = sobj.getProto()) {
            FindProperty find = sobj.findProperty(property, false);
            if (find == null) continue;
            return sobj.delete(property, false);
        }
        return obj.delete(property, false);
    }

    public static boolean strictFailDelete(String property) {
        throw ECMAErrors.syntaxError("strict.cant.delete", property);
    }

    public static boolean EQ(Object x, Object y) {
        return ScriptRuntime.equals(x, y);
    }

    public static boolean NE(Object x, Object y) {
        return !ScriptRuntime.EQ(x, y);
    }

    private static boolean equals(Object x, Object y) {
        if (x == y && !(x instanceof Number)) {
            return true;
        }
        if (x instanceof ScriptObject && y instanceof ScriptObject) {
            return false;
        }
        if (x instanceof ScriptObjectMirror || y instanceof ScriptObjectMirror) {
            return ScriptObjectMirror.identical(x, y);
        }
        return ScriptRuntime.equalValues(x, y);
    }

    private static boolean equalValues(Object x, Object y) {
        JSType yType;
        JSType xType = JSType.ofNoFunction(x);
        if (xType == (yType = JSType.ofNoFunction(y))) {
            return ScriptRuntime.equalSameTypeValues(x, y, xType);
        }
        return ScriptRuntime.equalDifferentTypeValues(x, y, xType, yType);
    }

    private static boolean equalSameTypeValues(Object x, Object y, JSType type) {
        if (type == JSType.UNDEFINED || type == JSType.NULL) {
            return true;
        }
        if (type == JSType.NUMBER) {
            return ((Number)x).doubleValue() == ((Number)y).doubleValue();
        }
        if (type == JSType.STRING) {
            return x.toString().equals(y.toString());
        }
        if (type == JSType.BOOLEAN) {
            return ((Boolean)x).booleanValue() == ((Boolean)y).booleanValue();
        }
        return x == y;
    }

    private static boolean equalDifferentTypeValues(Object x, Object y, JSType xType, JSType yType) {
        if (ScriptRuntime.isUndefinedAndNull(xType, yType) || ScriptRuntime.isUndefinedAndNull(yType, xType)) {
            return true;
        }
        if (ScriptRuntime.isNumberAndString(xType, yType)) {
            return ScriptRuntime.equalNumberToString(x, y);
        }
        if (ScriptRuntime.isNumberAndString(yType, xType)) {
            return ScriptRuntime.equalNumberToString(y, x);
        }
        if (xType == JSType.BOOLEAN) {
            return ScriptRuntime.equalBooleanToAny(x, y);
        }
        if (yType == JSType.BOOLEAN) {
            return ScriptRuntime.equalBooleanToAny(y, x);
        }
        if (ScriptRuntime.isPrimitiveAndObject(xType, yType)) {
            return ScriptRuntime.equalWrappedPrimitiveToObject(x, y);
        }
        if (ScriptRuntime.isPrimitiveAndObject(yType, xType)) {
            return ScriptRuntime.equalWrappedPrimitiveToObject(y, x);
        }
        return false;
    }

    private static boolean isUndefinedAndNull(JSType xType, JSType yType) {
        return xType == JSType.UNDEFINED && yType == JSType.NULL;
    }

    private static boolean isNumberAndString(JSType xType, JSType yType) {
        return xType == JSType.NUMBER && yType == JSType.STRING;
    }

    private static boolean isPrimitiveAndObject(JSType xType, JSType yType) {
        return (xType == JSType.NUMBER || xType == JSType.STRING || xType == JSType.SYMBOL) && yType == JSType.OBJECT;
    }

    private static boolean equalNumberToString(Object num, Object str) {
        return ((Number)num).doubleValue() == JSType.toNumber(str.toString());
    }

    private static boolean equalBooleanToAny(Object bool, Object any) {
        return ScriptRuntime.equals(JSType.toNumber((Boolean)bool), any);
    }

    private static boolean equalWrappedPrimitiveToObject(Object numOrStr, Object any) {
        return ScriptRuntime.equals(numOrStr, JSType.toPrimitive(any));
    }

    public static boolean EQ_STRICT(Object x, Object y) {
        return ScriptRuntime.strictEquals(x, y);
    }

    public static boolean NE_STRICT(Object x, Object y) {
        return !ScriptRuntime.EQ_STRICT(x, y);
    }

    private static boolean strictEquals(Object x, Object y) {
        JSType yType;
        JSType xType = JSType.ofNoFunction(x);
        if (xType != (yType = JSType.ofNoFunction(y))) {
            return false;
        }
        return ScriptRuntime.equalSameTypeValues(x, y, xType);
    }

    public static boolean IN(Object property, Object obj) {
        JSType rvalType = JSType.ofNoFunction(obj);
        if (rvalType == JSType.OBJECT) {
            if (obj instanceof ScriptObject) {
                return ((ScriptObject)obj).has(property);
            }
            if (obj instanceof JSObject) {
                return ((JSObject)obj).hasMember(Objects.toString(property));
            }
            Object key = JSType.toPropertyKey(property);
            if (obj instanceof StaticClass) {
                Class<?> clazz = ((StaticClass)obj).getRepresentedClass();
                return BeansLinker.getReadableStaticPropertyNames(clazz).contains(Objects.toString(key)) || BeansLinker.getStaticMethodNames(clazz).contains(Objects.toString(key));
            }
            if (obj instanceof Map && ((Map)obj).containsKey(key)) {
                return true;
            }
            int index = ArrayIndex.getArrayIndex(key);
            if (index >= 0) {
                if (obj instanceof List && index < ((List)obj).size()) {
                    return true;
                }
                if (obj.getClass().isArray() && index < Array.getLength(obj)) {
                    return true;
                }
            }
            return BeansLinker.getReadableInstancePropertyNames(obj.getClass()).contains(Objects.toString(key)) || BeansLinker.getInstanceMethodNames(obj.getClass()).contains(Objects.toString(key));
        }
        throw ECMAErrors.typeError("in.with.non.object", rvalType.toString().toLowerCase(Locale.ENGLISH));
    }

    public static boolean INSTANCEOF(Object obj, Object clazz) {
        if (clazz instanceof ScriptFunction) {
            if (obj instanceof ScriptObject) {
                return ((ScriptObject)clazz).isInstance((ScriptObject)obj);
            }
            return false;
        }
        if (clazz instanceof StaticClass) {
            return ((StaticClass)clazz).getRepresentedClass().isInstance(obj);
        }
        if (clazz instanceof JSObject) {
            return ((JSObject)clazz).isInstance(obj);
        }
        if (obj instanceof JSObject) {
            return ((JSObject)obj).isInstanceOf(clazz);
        }
        throw ECMAErrors.typeError("instanceof.on.non.object", new String[0]);
    }

    public static boolean LT(Object x, Object y) {
        Object py;
        Object px = JSType.toPrimitive(x, Number.class);
        return ScriptRuntime.areBothString(px, py = JSType.toPrimitive(y, Number.class)) ? px.toString().compareTo(py.toString()) < 0 : JSType.toNumber(px) < JSType.toNumber(py);
    }

    private static boolean areBothString(Object x, Object y) {
        return JSType.isString(x) && JSType.isString(y);
    }

    public static boolean GT(Object x, Object y) {
        Object py;
        Object px = JSType.toPrimitive(x, Number.class);
        return ScriptRuntime.areBothString(px, py = JSType.toPrimitive(y, Number.class)) ? px.toString().compareTo(py.toString()) > 0 : JSType.toNumber(px) > JSType.toNumber(py);
    }

    public static boolean LE(Object x, Object y) {
        Object py;
        Object px = JSType.toPrimitive(x, Number.class);
        return ScriptRuntime.areBothString(px, py = JSType.toPrimitive(y, Number.class)) ? px.toString().compareTo(py.toString()) <= 0 : JSType.toNumber(px) <= JSType.toNumber(py);
    }

    public static boolean GE(Object x, Object y) {
        Object py;
        Object px = JSType.toPrimitive(x, Number.class);
        return ScriptRuntime.areBothString(px, py = JSType.toPrimitive(y, Number.class)) ? px.toString().compareTo(py.toString()) >= 0 : JSType.toNumber(px) >= JSType.toNumber(py);
    }

    public static void invalidateReservedBuiltinName(String name) {
        Context context = Context.getContextTrusted();
        SwitchPoint sp = context.getBuiltinSwitchPoint(name);
        assert (sp != null);
        context.getLogger(ApplySpecialization.class).info("Overwrote special name '" + name + "' - invalidating switchpoint");
        SwitchPoint.invalidateAll(new SwitchPoint[]{sp});
    }

    public static ScriptObject GET_TEMPLATE_OBJECT(Object rawStrings, Object cookedStrings) {
        ScriptObject template = (ScriptObject)cookedStrings;
        ScriptObject rawObj = (ScriptObject)rawStrings;
        assert (rawObj.getArray().length() == template.getArray().length());
        template.addOwnProperty("raw", 7, rawObj.freeze());
        template.freeze();
        return template;
    }

    private static final class RangeIterator
    implements Iterator<Integer> {
        private final int length;
        private int index;

        RangeIterator(int length) {
            this.length = length;
        }

        @Override
        public boolean hasNext() {
            return this.index < this.length;
        }

        @Override
        public Integer next() {
            return this.index++;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
