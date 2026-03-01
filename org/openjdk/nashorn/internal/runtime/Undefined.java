package org.openjdk.nashorn.internal.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.NamedOperation;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.support.Guards;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.runtime.DefaultPropertyAccess;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.ECMAException;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.linker.NashornCallSiteDescriptor;

public final class Undefined
extends DefaultPropertyAccess {
    private static final Undefined UNDEFINED = new Undefined();
    private static final Undefined EMPTY = new Undefined();
    private static final MethodHandle UNDEFINED_GUARD = Guards.getIdentityGuard(UNDEFINED);
    private static final MethodHandle GET_METHOD = Undefined.findOwnMH("get", Object.class, Object.class);
    private static final MethodHandle SET_METHOD = Lookup.MH.insertArguments(Undefined.findOwnMH("set", Void.TYPE, Object.class, Object.class, Integer.TYPE), 3, 32);
    private static final MethodHandle DELETE_METHOD = Lookup.MH.insertArguments(Undefined.findOwnMH("delete", Boolean.TYPE, Object.class, Boolean.TYPE), 2, false);

    private Undefined() {
    }

    public static Undefined getUndefined() {
        return UNDEFINED;
    }

    public static Undefined getEmpty() {
        return EMPTY;
    }

    public String getClassName() {
        return "Undefined";
    }

    public String toString() {
        return "undefined";
    }

    public static GuardedInvocation lookup(CallSiteDescriptor desc) {
        switch (NashornCallSiteDescriptor.getStandardOperation(desc)) {
            case CALL: 
            case NEW: {
                String name = NashornCallSiteDescriptor.getOperand(desc);
                String msg = name != null ? "not.a.function" : "cant.call.undefined";
                throw ECMAErrors.typeError(msg, name);
            }
            case GET: {
                if (!(desc.getOperation() instanceof NamedOperation)) {
                    return Undefined.findGetIndexMethod(desc);
                }
                return Undefined.findGetMethod(desc);
            }
            case SET: {
                if (!(desc.getOperation() instanceof NamedOperation)) {
                    return Undefined.findSetIndexMethod(desc);
                }
                return Undefined.findSetMethod(desc);
            }
            case REMOVE: {
                if (!(desc.getOperation() instanceof NamedOperation)) {
                    return Undefined.findDeleteIndexMethod(desc);
                }
                return Undefined.findDeleteMethod(desc);
            }
        }
        return null;
    }

    private static ECMAException lookupTypeError(String msg, CallSiteDescriptor desc) {
        String name = NashornCallSiteDescriptor.getOperand(desc);
        return ECMAErrors.typeError(msg, name != null && !name.isEmpty() ? name : null);
    }

    private static GuardedInvocation findGetMethod(CallSiteDescriptor desc) {
        return new GuardedInvocation(Lookup.MH.insertArguments(GET_METHOD, 1, NashornCallSiteDescriptor.getOperand(desc)), UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findGetIndexMethod(CallSiteDescriptor desc) {
        return new GuardedInvocation(GET_METHOD, UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findSetMethod(CallSiteDescriptor desc) {
        return new GuardedInvocation(Lookup.MH.insertArguments(SET_METHOD, 1, NashornCallSiteDescriptor.getOperand(desc)), UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findSetIndexMethod(CallSiteDescriptor desc) {
        return new GuardedInvocation(SET_METHOD, UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findDeleteMethod(CallSiteDescriptor desc) {
        return new GuardedInvocation(Lookup.MH.insertArguments(DELETE_METHOD, 1, NashornCallSiteDescriptor.getOperand(desc)), UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findDeleteIndexMethod(CallSiteDescriptor desc) {
        return new GuardedInvocation(DELETE_METHOD, UNDEFINED_GUARD).asType(desc);
    }

    @Override
    public Object get(Object key) {
        throw ECMAErrors.typeError("cant.read.property.of.undefined", ScriptRuntime.safeToString(key));
    }

    @Override
    public void set(Object key, Object value, int flags) {
        throw ECMAErrors.typeError("cant.set.property.of.undefined", ScriptRuntime.safeToString(key));
    }

    @Override
    public boolean delete(Object key, boolean strict) {
        throw ECMAErrors.typeError("cant.delete.property.of.undefined", ScriptRuntime.safeToString(key));
    }

    @Override
    public boolean has(Object key) {
        return false;
    }

    @Override
    public boolean hasOwnProperty(Object key) {
        return false;
    }

    private static MethodHandle findOwnMH(String name, Class<?> rtype, Class<?> ... types) {
        return Lookup.MH.findVirtual(MethodHandles.lookup(), Undefined.class, name, Lookup.MH.type(rtype, types));
    }
}
