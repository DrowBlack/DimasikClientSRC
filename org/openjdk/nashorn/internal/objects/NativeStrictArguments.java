package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayData;

public final class NativeStrictArguments
extends ScriptObject {
    private static final MethodHandle G$LENGTH = NativeStrictArguments.findOwnMH("G$length", Object.class, Object.class);
    private static final MethodHandle S$LENGTH = NativeStrictArguments.findOwnMH("S$length", Void.TYPE, Object.class, Object.class);
    private static final PropertyMap map$;
    private Object length;
    private final Object[] namedArgs;

    static PropertyMap getInitialMap() {
        return map$;
    }

    NativeStrictArguments(Object[] values, int numParams, ScriptObject proto, PropertyMap map) {
        super(proto, map);
        this.setIsArguments();
        ScriptFunction func = Global.instance().getTypeErrorThrower();
        this.initUserAccessors("caller", func, func);
        this.initUserAccessors("callee", func, func);
        this.setArray(ArrayData.allocate(values));
        this.length = values.length;
        this.namedArgs = new Object[numParams];
        if (numParams > values.length) {
            Arrays.fill(this.namedArgs, ScriptRuntime.UNDEFINED);
        }
        System.arraycopy(values, 0, this.namedArgs, 0, Math.min(this.namedArgs.length, values.length));
    }

    @Override
    public String getClassName() {
        return "Arguments";
    }

    @Override
    public Object getArgument(int key) {
        return key >= 0 && key < this.namedArgs.length ? this.namedArgs[key] : ScriptRuntime.UNDEFINED;
    }

    @Override
    public void setArgument(int key, Object value) {
        if (key >= 0 && key < this.namedArgs.length) {
            this.namedArgs[key] = value;
        }
    }

    public static Object G$length(Object self) {
        if (self instanceof NativeStrictArguments) {
            return ((NativeStrictArguments)self).getArgumentsLength();
        }
        return 0;
    }

    public static void S$length(Object self, Object value) {
        if (self instanceof NativeStrictArguments) {
            ((NativeStrictArguments)self).setArgumentsLength(value);
        }
    }

    private Object getArgumentsLength() {
        return this.length;
    }

    private void setArgumentsLength(Object length) {
        this.length = length;
    }

    private static MethodHandle findOwnMH(String name, Class<?> rtype, Class<?> ... types) {
        return Lookup.MH.findStatic(MethodHandles.lookup(), NativeStrictArguments.class, name, Lookup.MH.type(rtype, types));
    }

    static {
        ArrayList<Property> properties = new ArrayList<Property>(1);
        properties.add(AccessorProperty.create("length", 2, G$LENGTH, S$LENGTH));
        PropertyMap map = PropertyMap.newMap(properties);
        int flags = 6;
        map = map.addPropertyNoHistory(map.newUserAccessors("caller", 6));
        map$ = map = map.addPropertyNoHistory(map.newUserAccessors("callee", 6));
    }
}
