package org.openjdk.nashorn.internal.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;

public class PrototypeObject
extends ScriptObject {
    private static final PropertyMap map$;
    private Object constructor;
    private static final MethodHandle GET_CONSTRUCTOR;
    private static final MethodHandle SET_CONSTRUCTOR;

    private PrototypeObject(Global global, PropertyMap map) {
        super(global.getObjectPrototype(), map != map$ ? map.addAll(map$) : map$);
    }

    protected PrototypeObject() {
        this(Global.instance(), map$);
    }

    protected PrototypeObject(PropertyMap map) {
        this(Global.instance(), map);
    }

    protected PrototypeObject(ScriptFunction func) {
        this(Global.instance(), map$);
        this.constructor = func;
    }

    public static Object getConstructor(Object self) {
        return self instanceof PrototypeObject ? ((PrototypeObject)self).getConstructor() : ScriptRuntime.UNDEFINED;
    }

    public static void setConstructor(Object self, Object constructor) {
        if (self instanceof PrototypeObject) {
            ((PrototypeObject)self).setConstructor(constructor);
        }
    }

    private Object getConstructor() {
        return this.constructor;
    }

    private void setConstructor(Object constructor) {
        this.constructor = constructor;
    }

    private static MethodHandle findOwnMH(String name, Class<?> rtype, Class<?> ... types) {
        return Lookup.MH.findStatic(MethodHandles.lookup(), PrototypeObject.class, name, Lookup.MH.type(rtype, types));
    }

    static {
        GET_CONSTRUCTOR = PrototypeObject.findOwnMH("getConstructor", Object.class, Object.class);
        SET_CONSTRUCTOR = PrototypeObject.findOwnMH("setConstructor", Void.TYPE, Object.class, Object.class);
        ArrayList<Property> properties = new ArrayList<Property>(1);
        properties.add(AccessorProperty.create("constructor", 2, GET_CONSTRUCTOR, SET_CONSTRUCTOR));
        map$ = PropertyMap.newMap(properties);
    }
}
