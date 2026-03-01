package org.openjdk.nashorn.internal.lookup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import org.openjdk.nashorn.internal.lookup.MethodHandleFactory;
import org.openjdk.nashorn.internal.lookup.MethodHandleFunctionality;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;

public final class Lookup {
    public static final MethodHandleFunctionality MH = MethodHandleFactory.getFunctionality();
    public static final MethodHandle EMPTY_GETTER = Lookup.findOwnMH("emptyGetter", Object.class, Object.class);
    public static final MethodHandle EMPTY_SETTER = Lookup.findOwnMH("emptySetter", Void.TYPE, Object.class, Object.class);
    public static final MethodHandle TYPE_ERROR_THROWER = Lookup.findOwnMH("typeErrorThrower", Object.class, Object.class);
    public static final MethodType GET_OBJECT_TYPE = MH.type(Object.class, Object.class);
    public static final MethodType SET_OBJECT_TYPE = MH.type(Void.TYPE, Object.class, Object.class);
    public static final MethodType GET_PRIMITIVE_TYPE = MH.type(Long.TYPE, Object.class);
    public static final MethodType SET_PRIMITIVE_TYPE = MH.type(Void.TYPE, Object.class, Long.TYPE);

    private Lookup() {
    }

    public static Object emptyGetter(Object self) {
        return ScriptRuntime.UNDEFINED;
    }

    public static void emptySetter(Object self, Object value) {
    }

    public static MethodHandle emptyGetter(Class<?> type) {
        return Lookup.filterReturnType(EMPTY_GETTER, type);
    }

    public static Object typeErrorThrower(Object self) {
        throw ECMAErrors.typeError("strict.getter.setter.poison", ScriptRuntime.safeToString(self));
    }

    public static MethodHandle filterArgumentType(MethodHandle mh, int n, Class<?> from) {
        TypeDescriptor.OfField to = mh.type().parameterType(n);
        if (from != Integer.TYPE) {
            if (from == Long.TYPE) {
                if (to == Integer.TYPE) {
                    return MH.filterArguments(mh, n, JSType.TO_INT32_L.methodHandle());
                }
            } else if (from == Double.TYPE) {
                if (to == Integer.TYPE) {
                    return MH.filterArguments(mh, n, JSType.TO_INT32_D.methodHandle());
                }
                if (to == Long.TYPE) {
                    return MH.filterArguments(mh, n, JSType.TO_UINT32_D.methodHandle());
                }
            } else if (!from.isPrimitive()) {
                if (to == Integer.TYPE) {
                    return MH.filterArguments(mh, n, JSType.TO_INT32.methodHandle());
                }
                if (to == Long.TYPE) {
                    return MH.filterArguments(mh, n, JSType.TO_UINT32.methodHandle());
                }
                if (to == Double.TYPE) {
                    return MH.filterArguments(mh, n, JSType.TO_NUMBER.methodHandle());
                }
                if (!((Class)to).isPrimitive()) {
                    return mh;
                }
                assert (false) : "unsupported Lookup.filterReturnType type " + from + " -> " + (Class)to;
            }
        }
        return MH.explicitCastArguments(mh, mh.type().changeParameterType(n, from));
    }

    public static MethodHandle filterReturnType(MethodHandle mh, Class<?> type) {
        TypeDescriptor.OfField retType = mh.type().returnType();
        if (retType != Integer.TYPE) {
            if (retType == Long.TYPE) {
                if (type == Integer.TYPE) {
                    return MH.filterReturnValue(mh, JSType.TO_INT32_L.methodHandle());
                }
            } else if (retType == Double.TYPE) {
                if (type == Integer.TYPE) {
                    return MH.filterReturnValue(mh, JSType.TO_INT32_D.methodHandle());
                }
                if (type == Long.TYPE) {
                    return MH.filterReturnValue(mh, JSType.TO_UINT32_D.methodHandle());
                }
            } else if (!((Class)retType).isPrimitive()) {
                if (type == Integer.TYPE) {
                    return MH.filterReturnValue(mh, JSType.TO_INT32.methodHandle());
                }
                if (type == Long.TYPE) {
                    return MH.filterReturnValue(mh, JSType.TO_UINT32.methodHandle());
                }
                if (type == Double.TYPE) {
                    return MH.filterReturnValue(mh, JSType.TO_NUMBER.methodHandle());
                }
                if (!type.isPrimitive()) {
                    return mh;
                }
                assert (false) : "unsupported Lookup.filterReturnType type " + (Class)retType + " -> " + type;
            }
        }
        return MH.explicitCastArguments(mh, mh.type().changeReturnType(type));
    }

    private static MethodHandle findOwnMH(String name, Class<?> rtype, Class<?> ... types) {
        return MH.findStatic(MethodHandles.lookup(), Lookup.class, name, MH.type(rtype, types));
    }
}
