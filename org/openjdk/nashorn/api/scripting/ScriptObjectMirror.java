package org.openjdk.nashorn.api.scripting;

import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import javax.script.Bindings;
import org.openjdk.nashorn.api.scripting.AbstractJSObject;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.NashornException;
import org.openjdk.nashorn.api.scripting.ScriptUtils;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.ConsString;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAException;
import org.openjdk.nashorn.internal.runtime.JSONListAdapter;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayData;

public final class ScriptObjectMirror
extends AbstractJSObject
implements Bindings {
    private static final AccessControlContext GET_CONTEXT_ACC_CTXT = ScriptObjectMirror.getContextAccCtxt();
    private final ScriptObject sobj;
    private final Global global;
    private final boolean strict;
    private final boolean jsonCompatible;

    private static AccessControlContext getContextAccCtxt() {
        Permissions perms = new Permissions();
        perms.add(new RuntimePermission("nashorn.getContext"));
        return new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain(null, perms)});
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ScriptObjectMirror) {
            return this.sobj.equals(((ScriptObjectMirror)other).sobj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.sobj.hashCode();
    }

    public String toString() {
        return this.inGlobal(() -> ScriptRuntime.safeToString(this.sobj));
    }

    @Override
    public Object call(Object thiz, Object ... args) {
        Global oldGlobal = Context.getGlobal();
        boolean globalChanged = oldGlobal != this.global;
        try {
            if (globalChanged) {
                Context.setGlobal(this.global);
            }
            if (this.sobj instanceof ScriptFunction) {
                Object[] modArgs = globalChanged ? this.wrapArrayLikeMe(args, oldGlobal) : args;
                Object self = globalChanged ? this.wrapLikeMe(thiz, oldGlobal) : thiz;
                Object object = this.wrapLikeMe(ScriptRuntime.apply((ScriptFunction)this.sobj, ScriptObjectMirror.unwrap(self, this.global), ScriptObjectMirror.unwrapArray(modArgs, this.global)));
                return object;
            }
            try {
                throw new RuntimeException("not a function: " + this.toString());
            }
            catch (NashornException ne) {
                throw ne.initEcmaError(this.global);
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        finally {
            if (globalChanged) {
                Context.setGlobal(oldGlobal);
            }
        }
    }

    @Override
    public Object newObject(Object ... args) {
        Global oldGlobal = Context.getGlobal();
        boolean globalChanged = oldGlobal != this.global;
        try {
            if (globalChanged) {
                Context.setGlobal(this.global);
            }
            if (this.sobj instanceof ScriptFunction) {
                Object[] modArgs = globalChanged ? this.wrapArrayLikeMe(args, oldGlobal) : args;
                Object object = this.wrapLikeMe(ScriptRuntime.construct((ScriptFunction)this.sobj, ScriptObjectMirror.unwrapArray(modArgs, this.global)));
                return object;
            }
            try {
                throw new RuntimeException("not a constructor: " + this.toString());
            }
            catch (NashornException ne) {
                throw ne.initEcmaError(this.global);
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        finally {
            if (globalChanged) {
                Context.setGlobal(oldGlobal);
            }
        }
    }

    @Override
    public Object eval(String s) {
        return this.inGlobal(() -> {
            Context context = AccessController.doPrivileged(Context::getContext, GET_CONTEXT_ACC_CTXT);
            return this.wrapLikeMe(context.eval(this.global, s, this.sobj, null));
        });
    }

    public Object callMember(String functionName, Object ... args) {
        Objects.requireNonNull(functionName);
        Global oldGlobal = Context.getGlobal();
        boolean globalChanged = oldGlobal != this.global;
        try {
            Object val;
            if (globalChanged) {
                Context.setGlobal(this.global);
            }
            if ((val = this.sobj.get(functionName)) instanceof ScriptFunction) {
                Object[] modArgs = globalChanged ? this.wrapArrayLikeMe(args, oldGlobal) : args;
                Object object = this.wrapLikeMe(ScriptRuntime.apply((ScriptFunction)val, this.sobj, ScriptObjectMirror.unwrapArray(modArgs, this.global)));
                return object;
            }
            if (val instanceof JSObject && ((JSObject)val).isFunction()) {
                Object object = ((JSObject)val).call(this.sobj, args);
                return object;
            }
            try {
                throw new NoSuchMethodException("No such function " + functionName);
            }
            catch (NashornException ne) {
                throw ne.initEcmaError(this.global);
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        finally {
            if (globalChanged) {
                Context.setGlobal(oldGlobal);
            }
        }
    }

    @Override
    public Object getMember(String name) {
        Objects.requireNonNull(name);
        return this.inGlobal(() -> this.wrapLikeMe(this.sobj.get(name)));
    }

    @Override
    public Object getSlot(int index) {
        return this.inGlobal(() -> this.wrapLikeMe(this.sobj.get(index)));
    }

    @Override
    public boolean hasMember(String name) {
        Objects.requireNonNull(name);
        return this.inGlobal(() -> this.sobj.has(name));
    }

    @Override
    public boolean hasSlot(int slot) {
        return this.inGlobal(() -> this.sobj.has(slot));
    }

    @Override
    public void removeMember(String name) {
        this.remove(Objects.requireNonNull(name));
    }

    @Override
    public void setMember(String name, Object value) {
        this.put(Objects.requireNonNull(name), value);
    }

    @Override
    public void setSlot(int index, Object value) {
        this.inGlobal(() -> this.sobj.set(index, ScriptObjectMirror.unwrap(value, this.global), this.getCallSiteFlags()));
    }

    public void setIndexedPropertiesToExternalArrayData(ByteBuffer buf) {
        this.inGlobal(() -> this.sobj.setArray(ArrayData.allocate(buf)));
    }

    @Override
    public boolean isInstance(Object instance) {
        if (!(instance instanceof ScriptObjectMirror)) {
            return false;
        }
        ScriptObjectMirror mirror = (ScriptObjectMirror)instance;
        if (this.global != mirror.global) {
            return false;
        }
        return this.inGlobal(() -> this.sobj.isInstance(mirror.sobj));
    }

    @Override
    public String getClassName() {
        return this.sobj.getClassName();
    }

    @Override
    public boolean isFunction() {
        return this.sobj instanceof ScriptFunction;
    }

    @Override
    public boolean isStrictFunction() {
        return this.isFunction() && ((ScriptFunction)this.sobj).isStrict();
    }

    @Override
    public boolean isArray() {
        return this.sobj.isArray();
    }

    @Override
    public void clear() {
        this.inGlobal(() -> this.sobj.clear(this.strict));
    }

    @Override
    public boolean containsKey(Object key) {
        ScriptObjectMirror.checkKey(key);
        return this.inGlobal(() -> this.sobj.containsKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return this.inGlobal(() -> this.sobj.containsValue(ScriptObjectMirror.unwrap(value, this.global)));
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.inGlobal(() -> {
            Iterator<String> iter = this.sobj.propertyIterator();
            LinkedHashSet<AbstractMap.SimpleImmutableEntry<String, Object>> entries = new LinkedHashSet<AbstractMap.SimpleImmutableEntry<String, Object>>();
            while (iter.hasNext()) {
                String key = iter.next();
                Object value = ScriptObjectMirror.translateUndefined(this.wrapLikeMe(this.sobj.get(key)));
                entries.add(new AbstractMap.SimpleImmutableEntry<String, Object>(key, value));
            }
            return Collections.unmodifiableSet(entries);
        });
    }

    @Override
    public Object get(Object key) {
        ScriptObjectMirror.checkKey(key);
        return this.inGlobal(() -> ScriptObjectMirror.translateUndefined(this.wrapLikeMe(this.sobj.get(key))));
    }

    @Override
    public boolean isEmpty() {
        return this.inGlobal(this.sobj::isEmpty);
    }

    @Override
    public Set<String> keySet() {
        return this.inGlobal(() -> {
            Iterator<String> iter = this.sobj.propertyIterator();
            LinkedHashSet<String> keySet = new LinkedHashSet<String>();
            while (iter.hasNext()) {
                keySet.add(iter.next());
            }
            return Collections.unmodifiableSet(keySet);
        });
    }

    @Override
    public Object put(String key, Object value) {
        ScriptObjectMirror.checkKey(key);
        Global oldGlobal = Context.getGlobal();
        boolean globalChanged = oldGlobal != this.global;
        return this.inGlobal(() -> {
            Object modValue = globalChanged ? this.wrapLikeMe(value, oldGlobal) : value;
            return ScriptObjectMirror.translateUndefined(this.wrapLikeMe(this.sobj.put(key, ScriptObjectMirror.unwrap(modValue, this.global), this.strict)));
        });
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        Objects.requireNonNull(map);
        Global oldGlobal = Context.getGlobal();
        boolean globalChanged = oldGlobal != this.global;
        this.inGlobal(() -> {
            for (Map.Entry entry : map.entrySet()) {
                Object value = entry.getValue();
                Object modValue = globalChanged ? this.wrapLikeMe(value, oldGlobal) : value;
                String key = (String)entry.getKey();
                ScriptObjectMirror.checkKey(key);
                this.sobj.set((Object)key, ScriptObjectMirror.unwrap(modValue, this.global), this.getCallSiteFlags());
            }
        });
    }

    @Override
    public Object remove(Object key) {
        ScriptObjectMirror.checkKey(key);
        return this.inGlobal(() -> ScriptObjectMirror.translateUndefined(this.wrapLikeMe(this.sobj.remove(key, this.strict))));
    }

    public boolean delete(Object key) {
        return this.inGlobal(() -> this.sobj.delete(ScriptObjectMirror.unwrap(key, this.global), this.strict));
    }

    @Override
    public int size() {
        return this.inGlobal(this.sobj::size);
    }

    @Override
    public Collection<Object> values() {
        return this.inGlobal(() -> {
            ArrayList<Object> values = new ArrayList<Object>(this.size());
            Iterator<Object> iter = this.sobj.valueIterator();
            while (iter.hasNext()) {
                values.add(ScriptObjectMirror.translateUndefined(this.wrapLikeMe(iter.next())));
            }
            return Collections.unmodifiableList(values);
        });
    }

    public Object getProto() {
        return this.inGlobal(() -> this.wrapLikeMe(this.sobj.getProto()));
    }

    public void setProto(Object proto) {
        this.inGlobal(() -> this.sobj.setPrototypeOf(ScriptObjectMirror.unwrap(proto, this.global)));
    }

    public Object getOwnPropertyDescriptor(String key) {
        return this.inGlobal(() -> this.wrapLikeMe(this.sobj.getOwnPropertyDescriptor(key)));
    }

    public String[] getOwnKeys(boolean all) {
        return this.inGlobal(() -> this.sobj.getOwnKeys(all));
    }

    public ScriptObjectMirror preventExtensions() {
        return this.inGlobal(() -> {
            this.sobj.preventExtensions();
            return this;
        });
    }

    public boolean isExtensible() {
        return this.inGlobal(this.sobj::isExtensible);
    }

    public ScriptObjectMirror seal() {
        return this.inGlobal(() -> {
            this.sobj.seal();
            return this;
        });
    }

    public boolean isSealed() {
        return this.inGlobal(this.sobj::isSealed);
    }

    public ScriptObjectMirror freeze() {
        return this.inGlobal(() -> {
            this.sobj.freeze();
            return this;
        });
    }

    public boolean isFrozen() {
        return this.inGlobal(this.sobj::isFrozen);
    }

    public static boolean isUndefined(Object obj) {
        return obj == ScriptRuntime.UNDEFINED;
    }

    public <T> T to(Class<T> type) {
        return (T)this.inGlobal(() -> type.cast(ScriptUtils.convert(this.sobj, type)));
    }

    public static Object wrap(Object obj, Object homeGlobal) {
        return ScriptObjectMirror.wrap(obj, homeGlobal, false);
    }

    public static Object wrapAsJSONCompatible(Object obj, Object homeGlobal) {
        return ScriptObjectMirror.wrap(obj, homeGlobal, true);
    }

    private static Object wrap(Object obj, Object homeGlobal, boolean jsonCompatible) {
        if (obj instanceof ScriptObject) {
            if (!(homeGlobal instanceof Global)) {
                return obj;
            }
            ScriptObject sobj = (ScriptObject)obj;
            Global global = (Global)homeGlobal;
            ScriptObjectMirror mirror = new ScriptObjectMirror(sobj, global, jsonCompatible);
            if (jsonCompatible && sobj.isArray()) {
                return new JSONListAdapter(mirror, global);
            }
            return mirror;
        }
        if (obj instanceof ConsString) {
            return obj.toString();
        }
        if (jsonCompatible && obj instanceof ScriptObjectMirror) {
            return ((ScriptObjectMirror)obj).asJSONCompatible();
        }
        return obj;
    }

    private Object wrapLikeMe(Object obj, Object homeGlobal) {
        return ScriptObjectMirror.wrap(obj, homeGlobal, this.jsonCompatible);
    }

    private Object wrapLikeMe(Object obj) {
        return this.wrapLikeMe(obj, this.global);
    }

    public static Object unwrap(Object obj, Object homeGlobal) {
        if (obj instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror)obj;
            return mirror.global == homeGlobal ? mirror.sobj : obj;
        }
        if (obj instanceof JSONListAdapter) {
            return ((JSONListAdapter)obj).unwrap(homeGlobal);
        }
        return obj;
    }

    public static Object[] wrapArray(Object[] args, Object homeGlobal) {
        return ScriptObjectMirror.wrapArray(args, homeGlobal, false);
    }

    private static Object[] wrapArray(Object[] args, Object homeGlobal, boolean jsonCompatible) {
        if (args == null || args.length == 0) {
            return args;
        }
        Object[] newArgs = new Object[args.length];
        int index = 0;
        for (Object obj : args) {
            newArgs[index] = ScriptObjectMirror.wrap(obj, homeGlobal, jsonCompatible);
            ++index;
        }
        return newArgs;
    }

    private Object[] wrapArrayLikeMe(Object[] args, Object homeGlobal) {
        return ScriptObjectMirror.wrapArray(args, homeGlobal, this.jsonCompatible);
    }

    public static Object[] unwrapArray(Object[] args, Object homeGlobal) {
        if (args == null || args.length == 0) {
            return args;
        }
        Object[] newArgs = new Object[args.length];
        int index = 0;
        for (Object obj : args) {
            newArgs[index] = ScriptObjectMirror.unwrap(obj, homeGlobal);
            ++index;
        }
        return newArgs;
    }

    public static boolean identical(Object obj1, Object obj2) {
        Object o1 = obj1 instanceof ScriptObjectMirror ? ((ScriptObjectMirror)obj1).sobj : obj1;
        Object o2 = obj2 instanceof ScriptObjectMirror ? ((ScriptObjectMirror)obj2).sobj : obj2;
        return o1 == o2;
    }

    ScriptObjectMirror(ScriptObject sobj, Global global) {
        this(sobj, global, false);
    }

    private ScriptObjectMirror(ScriptObject sobj, Global global, boolean jsonCompatible) {
        assert (sobj != null) : "ScriptObjectMirror on null!";
        assert (global != null) : "home Global is null";
        this.sobj = sobj;
        this.global = global;
        this.strict = global.isStrictContext();
        this.jsonCompatible = jsonCompatible;
    }

    ScriptObject getScriptObject() {
        return this.sobj;
    }

    Global getHomeGlobal() {
        return this.global;
    }

    static Object translateUndefined(Object obj) {
        return obj == ScriptRuntime.UNDEFINED ? null : obj;
    }

    private int getCallSiteFlags() {
        return this.strict ? 32 : 0;
    }

    private void inGlobal(Runnable r) {
        this.inGlobal(() -> {
            r.run();
            return null;
        });
    }

    private <V> V inGlobal(Supplier<V> s) {
        boolean globalChanged;
        Global oldGlobal = Context.getGlobal();
        boolean bl = globalChanged = oldGlobal != this.global;
        if (globalChanged) {
            Context.setGlobal(this.global);
        }
        try {
            V v = s.get();
            return v;
        }
        catch (NashornException ne) {
            throw ne.initEcmaError(this.global);
        }
        finally {
            if (globalChanged) {
                Context.setGlobal(oldGlobal);
            }
        }
    }

    private static void checkKey(Object key) {
        Objects.requireNonNull(key, "key can not be null");
        if (!(key instanceof String)) {
            throw new ClassCastException("key should be a String. It is " + key.getClass().getName() + " instead.");
        }
        if (((String)key).length() == 0) {
            throw new IllegalArgumentException("key can not be empty");
        }
    }

    @Override
    @Deprecated
    public double toNumber() {
        return this.inGlobal(() -> JSType.toNumber(this.sobj));
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        return this.inGlobal(() -> {
            try {
                return this.sobj.getDefaultValue(hint);
            }
            catch (ECMAException e) {
                throw new UnsupportedOperationException(e.getMessage(), e);
            }
        });
    }

    private ScriptObjectMirror asJSONCompatible() {
        if (this.jsonCompatible) {
            return this;
        }
        return new ScriptObjectMirror(this.sobj, this.global, true);
    }
}
