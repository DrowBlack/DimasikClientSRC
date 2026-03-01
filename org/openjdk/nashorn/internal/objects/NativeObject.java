package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.Operation;
import jdk.dynalink.StandardNamespace;
import jdk.dynalink.StandardOperation;
import jdk.dynalink.beans.BeansLinker;
import jdk.dynalink.beans.StaticClass;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.GuardingDynamicLinker;
import jdk.dynalink.linker.LinkRequest;
import jdk.dynalink.linker.TypeBasedGuardingDynamicLinker;
import jdk.dynalink.linker.support.SimpleLinkRequest;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.NativeArray;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.ECMAException;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayData;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayIndex;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.linker.InvokeByName;
import org.openjdk.nashorn.internal.runtime.linker.NashornBeansLinker;

public final class NativeObject {
    public static final MethodHandle GET__PROTO__ = NativeObject.findOwnMH("get__proto__", ScriptObject.class, Object.class);
    public static final MethodHandle SET__PROTO__ = NativeObject.findOwnMH("set__proto__", Object.class, Object.class, Object.class);
    private static final Object TO_STRING = new Object();
    private static final Operation GET_METHOD = StandardOperation.GET.withNamespace(StandardNamespace.METHOD);
    private static final Operation GET_PROPERTY = StandardOperation.GET.withNamespace(StandardNamespace.PROPERTY);
    private static final Operation SET_PROPERTY = StandardOperation.SET.withNamespace(StandardNamespace.PROPERTY);
    private static final MethodType MIRROR_GETTER_TYPE = MethodType.methodType(Object.class, ScriptObjectMirror.class);
    private static final MethodType MIRROR_SETTER_TYPE = MethodType.methodType(Object.class, ScriptObjectMirror.class, Object.class);
    private static PropertyMap $nasgenmap$;

    private static InvokeByName getTO_STRING() {
        return Global.instance().getInvokeByName(TO_STRING, () -> new InvokeByName("toString", ScriptObject.class));
    }

    private static ScriptObject get__proto__(Object self) {
        ScriptObject sobj = Global.checkObject(Global.toObject(self));
        return sobj.getProto();
    }

    private static Object set__proto__(Object self, Object proto) {
        Global.checkObjectCoercible(self);
        if (!(self instanceof ScriptObject)) {
            return ScriptRuntime.UNDEFINED;
        }
        ScriptObject sobj = (ScriptObject)self;
        if (proto == null || proto instanceof ScriptObject) {
            sobj.setPrototypeOf(proto);
        }
        return ScriptRuntime.UNDEFINED;
    }

    private NativeObject() {
        throw new UnsupportedOperationException();
    }

    private static ECMAException notAnObject(Object obj) {
        return ECMAErrors.typeError("not.an.object", ScriptRuntime.safeToString(obj));
    }

    public static ScriptObject setIndexedPropertiesToExternalArrayData(Object self, Object obj, Object buf) {
        Global.checkObject(obj);
        ScriptObject sobj = (ScriptObject)obj;
        if (!(buf instanceof ByteBuffer)) {
            throw ECMAErrors.typeError("not.a.bytebuffer", "setIndexedPropertiesToExternalArrayData's buf argument");
        }
        sobj.setArray(ArrayData.allocate((ByteBuffer)buf));
        return sobj;
    }

    public static Object getPrototypeOf(Object self, Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).getProto();
        }
        if (obj instanceof ScriptObjectMirror) {
            return ((ScriptObjectMirror)obj).getProto();
        }
        JSType type = JSType.of(obj);
        if (type == JSType.OBJECT) {
            return null;
        }
        throw NativeObject.notAnObject(obj);
    }

    public static Object setPrototypeOf(Object self, Object obj, Object proto) {
        Global.checkObjectCoercible(obj);
        if (obj instanceof ScriptObject) {
            ((ScriptObject)obj).setPrototypeOf(proto);
        } else if (obj instanceof ScriptObjectMirror) {
            ((ScriptObjectMirror)obj).setProto(proto);
        }
        return obj;
    }

    public static Object getOwnPropertyDescriptor(Object self, Object obj, Object prop) {
        if (obj instanceof ScriptObject) {
            String key = JSType.toString(prop);
            ScriptObject sobj = (ScriptObject)obj;
            return sobj.getOwnPropertyDescriptor(key);
        }
        if (obj instanceof ScriptObjectMirror) {
            String key = JSType.toString(prop);
            ScriptObjectMirror sobjMirror = (ScriptObjectMirror)obj;
            return sobjMirror.getOwnPropertyDescriptor(key);
        }
        throw NativeObject.notAnObject(obj);
    }

    public static ScriptObject getOwnPropertyNames(Object self, Object obj) {
        if (obj instanceof ScriptObject) {
            return new NativeArray(((ScriptObject)obj).getOwnKeys(true));
        }
        if (obj instanceof ScriptObjectMirror) {
            return new NativeArray(((ScriptObjectMirror)obj).getOwnKeys(true));
        }
        Global global = Global.instance();
        if (global.isES6()) {
            Object obj2 = JSType.toScriptObject(global, obj);
            if (obj2 instanceof ScriptObject) {
                return new NativeArray(((ScriptObject)obj2).getOwnKeys(true));
            }
            return new NativeArray();
        }
        throw NativeObject.notAnObject(obj);
    }

    public static ScriptObject getOwnPropertySymbols(Object self, Object obj) {
        Object obj2 = JSType.toScriptObject(obj);
        if (obj2 instanceof ScriptObject) {
            return new NativeArray(((ScriptObject)obj2).getOwnSymbols(true));
        }
        return new NativeArray();
    }

    public static ScriptObject create(Object self, Object proto, Object props) {
        if (proto != null) {
            Global.checkObject(proto);
        }
        ScriptObject newObj = Global.newEmptyInstance();
        newObj.setProto((ScriptObject)proto);
        if (props != ScriptRuntime.UNDEFINED) {
            NativeObject.defineProperties(self, newObj, props);
        }
        return newObj;
    }

    public static ScriptObject defineProperty(Object self, Object obj, Object prop, Object attr) {
        ScriptObject sobj = Global.checkObject(obj);
        sobj.defineOwnProperty(JSType.toPropertyKey(prop), attr, true);
        return sobj;
    }

    public static ScriptObject defineProperties(Object self, Object obj, Object props) {
        ScriptObject sobj = Global.checkObject(obj);
        Object propsObj = Global.toObject(props);
        if (propsObj instanceof ScriptObject) {
            String[] keys;
            for (String key : keys = ((ScriptObject)propsObj).getOwnKeys(false)) {
                String prop = JSType.toString(key);
                sobj.defineOwnProperty(prop, ((ScriptObject)propsObj).get(prop), true);
            }
        }
        return sobj;
    }

    public static Object seal(Object self, Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).seal();
        }
        if (obj instanceof ScriptObjectMirror) {
            return ((ScriptObjectMirror)obj).seal();
        }
        if (NativeObject.isES6()) {
            return obj;
        }
        throw NativeObject.notAnObject(obj);
    }

    public static Object freeze(Object self, Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).freeze();
        }
        if (obj instanceof ScriptObjectMirror) {
            return ((ScriptObjectMirror)obj).freeze();
        }
        if (NativeObject.isES6()) {
            return obj;
        }
        throw NativeObject.notAnObject(obj);
    }

    private static boolean isES6() {
        return Global.instance().isES6();
    }

    public static Object preventExtensions(Object self, Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).preventExtensions();
        }
        if (obj instanceof ScriptObjectMirror) {
            return ((ScriptObjectMirror)obj).preventExtensions();
        }
        if (NativeObject.isES6()) {
            return obj;
        }
        throw NativeObject.notAnObject(obj);
    }

    public static boolean isSealed(Object self, Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).isSealed();
        }
        if (obj instanceof ScriptObjectMirror) {
            return ((ScriptObjectMirror)obj).isSealed();
        }
        if (NativeObject.isES6()) {
            return true;
        }
        throw NativeObject.notAnObject(obj);
    }

    public static boolean isFrozen(Object self, Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).isFrozen();
        }
        if (obj instanceof ScriptObjectMirror) {
            return ((ScriptObjectMirror)obj).isFrozen();
        }
        if (NativeObject.isES6()) {
            return true;
        }
        throw NativeObject.notAnObject(obj);
    }

    public static boolean isExtensible(Object self, Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).isExtensible();
        }
        if (obj instanceof ScriptObjectMirror) {
            return ((ScriptObjectMirror)obj).isExtensible();
        }
        if (NativeObject.isES6()) {
            return false;
        }
        throw NativeObject.notAnObject(obj);
    }

    public static ScriptObject keys(Object self, Object obj) {
        if (obj instanceof ScriptObject) {
            ScriptObject sobj = (ScriptObject)obj;
            return new NativeArray(sobj.getOwnKeys(false));
        }
        if (obj instanceof ScriptObjectMirror) {
            ScriptObjectMirror sobjMirror = (ScriptObjectMirror)obj;
            return new NativeArray(sobjMirror.getOwnKeys(false));
        }
        throw NativeObject.notAnObject(obj);
    }

    public static Object construct(boolean newObj, Object self, Object value) {
        JSType type = JSType.ofNoFunction(value);
        if (newObj || type == JSType.NULL || type == JSType.UNDEFINED) {
            switch (type) {
                case BOOLEAN: 
                case NUMBER: 
                case STRING: 
                case SYMBOL: {
                    return Global.toObject(value);
                }
                case OBJECT: {
                    return value;
                }
            }
            return Global.newEmptyInstance();
        }
        return Global.toObject(value);
    }

    public static String toString(Object self) {
        return ScriptRuntime.builtinObjectToString(self);
    }

    public static Object toLocaleString(Object self) {
        Object obj = JSType.toScriptObject(self);
        if (obj instanceof ScriptObject) {
            InvokeByName toStringInvoker = NativeObject.getTO_STRING();
            ScriptObject sobj = (ScriptObject)obj;
            try {
                Object toString = toStringInvoker.getGetter().invokeExact(sobj);
                if (Bootstrap.isCallable(toString)) {
                    return toStringInvoker.getInvoker().invokeExact(toString, sobj);
                }
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
            throw ECMAErrors.typeError("not.a.function", "toString");
        }
        return ScriptRuntime.builtinObjectToString(self);
    }

    public static Object valueOf(Object self) {
        return Global.toObject(self);
    }

    public static boolean hasOwnProperty(Object self, Object v) {
        Object key = JSType.toPrimitive(v, String.class);
        Object obj = Global.toObject(self);
        return obj instanceof ScriptObject && ((ScriptObject)obj).hasOwnProperty(key);
    }

    public static boolean isPrototypeOf(Object self, Object v) {
        if (!(v instanceof ScriptObject)) {
            return false;
        }
        Object obj = Global.toObject(self);
        ScriptObject proto = (ScriptObject)v;
        do {
            if ((proto = proto.getProto()) != obj) continue;
            return true;
        } while (proto != null);
        return false;
    }

    public static boolean propertyIsEnumerable(Object self, Object v) {
        String str = JSType.toString(v);
        Object obj = Global.toObject(self);
        if (obj instanceof ScriptObject) {
            ScriptObject sobj = (ScriptObject)obj;
            Property property = sobj.getProperty(str);
            if (property != null) {
                return property.isEnumerable();
            }
            return sobj.getArray().has(ArrayIndex.getArrayIndex(v));
        }
        return false;
    }

    public static Object bindProperties(Object self, Object target, Object source) {
        ScriptObject targetObj = Global.checkObject(target);
        Global.checkObjectCoercible(source);
        if (source instanceof ScriptObject) {
            ScriptObject sourceObj = (ScriptObject)source;
            PropertyMap sourceMap = sourceObj.getMap();
            Property[] properties = sourceMap.getProperties();
            ArrayList<Property> propList = new ArrayList<Property>();
            for (Property prop : properties) {
                if (!prop.isEnumerable()) continue;
                Object value = sourceObj.get(prop.getKey());
                prop.setType(Object.class);
                prop.setValue(sourceObj, sourceObj, value, false);
                propList.add(prop);
            }
            if (!propList.isEmpty()) {
                targetObj.addBoundProperties(sourceObj, propList.toArray(new Property[0]));
            }
        } else if (source instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror)source;
            String[] keys = mirror.getOwnKeys(false);
            if (keys.length == 0) {
                return target;
            }
            AccessorProperty[] props = new AccessorProperty[keys.length];
            for (int idx = 0; idx < keys.length; ++idx) {
                props[idx] = NativeObject.createAccessorProperty(keys[idx]);
            }
            targetObj.addBoundProperties(source, props);
        } else if (source instanceof StaticClass) {
            Class<?> clazz = ((StaticClass)source).getRepresentedClass();
            Bootstrap.checkReflectionAccess(clazz, true);
            NativeObject.bindBeanProperties(targetObj, source, BeansLinker.getReadableStaticPropertyNames(clazz), BeansLinker.getWritableStaticPropertyNames(clazz), BeansLinker.getStaticMethodNames(clazz));
        } else {
            Class<?> clazz = source.getClass();
            Bootstrap.checkReflectionAccess(clazz, false);
            NativeObject.bindBeanProperties(targetObj, source, BeansLinker.getReadableInstancePropertyNames(clazz), BeansLinker.getWritableInstancePropertyNames(clazz), BeansLinker.getInstanceMethodNames(clazz));
        }
        return target;
    }

    private static AccessorProperty createAccessorProperty(String name) {
        MethodHandle getter = Bootstrap.createDynamicInvoker(name, 2, MIRROR_GETTER_TYPE);
        MethodHandle setter = Bootstrap.createDynamicInvoker(name, 4, MIRROR_SETTER_TYPE);
        return AccessorProperty.create(name, 0, getter, setter);
    }

    public static Object bindAllProperties(ScriptObject target, ScriptObjectMirror source) {
        Set<String> keys = source.keySet();
        AccessorProperty[] props = new AccessorProperty[keys.size()];
        int idx = 0;
        for (String name : keys) {
            props[idx] = NativeObject.createAccessorProperty(name);
            ++idx;
        }
        target.addBoundProperties(source, props);
        return target;
    }

    private static void bindBeanProperties(ScriptObject targetObj, Object source, Collection<String> readablePropertyNames, Collection<String> writablePropertyNames, Collection<String> methodNames) {
        HashSet<String> propertyNames = new HashSet<String>(readablePropertyNames);
        propertyNames.addAll(writablePropertyNames);
        Class<?> clazz = source.getClass();
        MethodType getterType = MethodType.methodType(Object.class, clazz);
        MethodType setterType = MethodType.methodType(Object.class, clazz, Object.class);
        TypeBasedGuardingDynamicLinker linker = Bootstrap.getBeanLinkerForClass(clazz);
        ArrayList<AccessorProperty> properties = new ArrayList<AccessorProperty>(propertyNames.size() + methodNames.size());
        for (String methodName : methodNames) {
            MethodHandle method;
            try {
                method = NativeObject.getBeanOperation(linker, GET_METHOD, methodName, getterType, source);
            }
            catch (IllegalAccessError e) {
                continue;
            }
            properties.add(AccessorProperty.create(methodName, 1, NativeObject.getBoundBeanMethodGetter(source, method), Lookup.EMPTY_SETTER));
        }
        for (String propertyName : propertyNames) {
            MethodHandle setter;
            boolean isWritable;
            MethodHandle getter;
            if (readablePropertyNames.contains(propertyName)) {
                try {
                    getter = NativeObject.getBeanOperation(linker, GET_PROPERTY, propertyName, getterType, source);
                }
                catch (IllegalAccessError e) {
                    getter = Lookup.EMPTY_GETTER;
                }
            } else {
                getter = Lookup.EMPTY_GETTER;
            }
            if (isWritable = writablePropertyNames.contains(propertyName)) {
                try {
                    setter = NativeObject.getBeanOperation(linker, SET_PROPERTY, propertyName, setterType, source);
                }
                catch (IllegalAccessError e) {
                    setter = Lookup.EMPTY_SETTER;
                }
            } else {
                setter = Lookup.EMPTY_SETTER;
            }
            if (getter == Lookup.EMPTY_GETTER && setter == Lookup.EMPTY_SETTER) continue;
            properties.add(AccessorProperty.create(propertyName, isWritable ? 0 : 1, getter, setter));
        }
        targetObj.addBoundProperties(source, properties.toArray(new AccessorProperty[0]));
    }

    private static MethodHandle getBoundBeanMethodGetter(Object source, MethodHandle methodGetter) {
        try {
            return MethodHandles.dropArguments(MethodHandles.constant(Object.class, Bootstrap.bindCallable(methodGetter.invoke(source), source, null)), 0, new Class[]{Object.class});
        }
        catch (Error | RuntimeException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static MethodHandle getBeanOperation(GuardingDynamicLinker linker, Operation operation, String name, MethodType methodType, Object source) {
        GuardedInvocation inv;
        try {
            inv = NashornBeansLinker.getGuardedInvocation(linker, NativeObject.createLinkRequest(operation.named(name), methodType, source), Bootstrap.getLinkerServices());
            assert (NativeObject.passesGuard(source, inv.getGuard()));
        }
        catch (Error | RuntimeException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
        assert (inv.getSwitchPoints() == null);
        return inv.getInvocation();
    }

    private static boolean passesGuard(Object obj, MethodHandle guard) throws Throwable {
        return guard == null || guard.invoke(obj);
    }

    private static LinkRequest createLinkRequest(Operation operation, MethodType methodType, Object source) {
        return new SimpleLinkRequest(new CallSiteDescriptor(MethodHandles.publicLookup(), operation, methodType), false, source);
    }

    private static MethodHandle findOwnMH(String name, Class<?> rtype, Class<?> ... types) {
        return Lookup.MH.findStatic(MethodHandles.lookup(), NativeObject.class, name, Lookup.MH.type(rtype, types));
    }

    static {
        NativeObject.$clinit$();
    }

    public static void $clinit$() {
        $nasgenmap$ = PropertyMap.newMap(Collections.EMPTY_LIST);
    }
}
