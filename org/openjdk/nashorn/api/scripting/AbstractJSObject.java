package org.openjdk.nashorn.api.scripting;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.openjdk.nashorn.api.scripting.JSObject;

public abstract class AbstractJSObject
implements JSObject {
    @Override
    public Object call(Object thiz, Object ... args) {
        throw new UnsupportedOperationException("call");
    }

    @Override
    public Object newObject(Object ... args) {
        throw new UnsupportedOperationException("newObject");
    }

    @Override
    public Object eval(String s) {
        throw new UnsupportedOperationException("eval");
    }

    @Override
    public Object getMember(String name) {
        Objects.requireNonNull(name);
        return null;
    }

    @Override
    public Object getSlot(int index) {
        return null;
    }

    @Override
    public boolean hasMember(String name) {
        Objects.requireNonNull(name);
        return false;
    }

    @Override
    public boolean hasSlot(int slot) {
        return false;
    }

    @Override
    public void removeMember(String name) {
        Objects.requireNonNull(name);
    }

    @Override
    public void setMember(String name, Object value) {
        Objects.requireNonNull(name);
    }

    @Override
    public void setSlot(int index, Object value) {
    }

    @Override
    public Set<String> keySet() {
        return Collections.emptySet();
    }

    @Override
    public Collection<Object> values() {
        return Collections.emptySet();
    }

    @Override
    public boolean isInstance(Object instance) {
        return false;
    }

    @Override
    public boolean isInstanceOf(Object clazz) {
        if (clazz instanceof JSObject) {
            return ((JSObject)clazz).isInstance(this);
        }
        return false;
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isStrictFunction() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    @Deprecated
    public double toNumber() {
        return Double.NaN;
    }

    @Deprecated
    public static Object getDefaultValue(JSObject jsobj, Class<?> hint) {
        return jsobj.getDefaultValue(hint);
    }
}
