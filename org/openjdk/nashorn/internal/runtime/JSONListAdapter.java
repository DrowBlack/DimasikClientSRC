package org.openjdk.nashorn.internal.runtime;

import java.util.Collection;
import java.util.Set;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.ListAdapter;

public final class JSONListAdapter
extends ListAdapter
implements JSObject {
    public JSONListAdapter(JSObject obj, Global global) {
        super(obj, global);
    }

    public Object unwrap(Object homeGlobal) {
        Object unwrapped = ScriptObjectMirror.unwrap(this.obj, homeGlobal);
        return unwrapped != this.obj ? unwrapped : this;
    }

    @Override
    public Object call(Object thiz, Object ... args) {
        return this.obj.call(thiz, args);
    }

    @Override
    public Object newObject(Object ... args) {
        return this.obj.newObject(args);
    }

    @Override
    public Object eval(String s) {
        return this.obj.eval(s);
    }

    @Override
    public Object getMember(String name) {
        return this.obj.getMember(name);
    }

    @Override
    public Object getSlot(int index) {
        return this.obj.getSlot(index);
    }

    @Override
    public boolean hasMember(String name) {
        return this.obj.hasMember(name);
    }

    @Override
    public boolean hasSlot(int slot) {
        return this.obj.hasSlot(slot);
    }

    @Override
    public void removeMember(String name) {
        this.obj.removeMember(name);
    }

    @Override
    public void setMember(String name, Object value) {
        this.obj.setMember(name, value);
    }

    @Override
    public void setSlot(int index, Object value) {
        this.obj.setSlot(index, value);
    }

    @Override
    public Set<String> keySet() {
        return this.obj.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.obj.values();
    }

    @Override
    public boolean isInstance(Object instance) {
        return this.obj.isInstance(instance);
    }

    @Override
    public boolean isInstanceOf(Object clazz) {
        return this.obj.isInstanceOf(clazz);
    }

    @Override
    public String getClassName() {
        return this.obj.getClassName();
    }

    @Override
    public boolean isFunction() {
        return this.obj.isFunction();
    }

    @Override
    public boolean isStrictFunction() {
        return this.obj.isStrictFunction();
    }

    @Override
    public boolean isArray() {
        return this.obj.isArray();
    }

    @Override
    @Deprecated
    public double toNumber() {
        return this.obj.toNumber();
    }

    @Override
    public Object getDefaultValue(Class<?> hint) throws UnsupportedOperationException {
        return this.obj.getDefaultValue(hint);
    }
}
