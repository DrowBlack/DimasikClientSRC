package org.openjdk.nashorn.internal.runtime.linker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SwitchPoint;
import java.lang.invoke.TypeDescriptor;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.LinkRequest;
import jdk.dynalink.linker.support.Guards;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.FindProperty;
import org.openjdk.nashorn.internal.runtime.GlobalConstants;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.linker.NashornCallSiteDescriptor;
import org.openjdk.nashorn.internal.runtime.linker.NashornGuards;

public final class PrimitiveLookup {
    private static final MethodHandle PRIMITIVE_SETTER = PrimitiveLookup.findOwnMH("primitiveSetter", Lookup.MH.type(Void.TYPE, ScriptObject.class, Object.class, Object.class, Boolean.TYPE, Object.class));

    private PrimitiveLookup() {
    }

    public static GuardedInvocation lookupPrimitive(LinkRequest request, Class<?> receiverClass, ScriptObject wrappedReceiver, MethodHandle wrapFilter, MethodHandle protoFilter) {
        return PrimitiveLookup.lookupPrimitive(request, Guards.getInstanceOfGuard(receiverClass), wrappedReceiver, wrapFilter, protoFilter);
    }

    public static GuardedInvocation lookupPrimitive(LinkRequest request, MethodHandle guard, ScriptObject wrappedReceiver, MethodHandle wrapFilter, MethodHandle protoFilter) {
        CallSiteDescriptor desc = request.getCallSiteDescriptor();
        String name = NashornCallSiteDescriptor.getOperand(desc);
        FindProperty find = name != null ? wrappedReceiver.findProperty(name, true) : null;
        switch (NashornCallSiteDescriptor.getStandardOperation(desc)) {
            case GET: {
                ScriptObject proto;
                GuardedInvocation link;
                if (name == null) break;
                if (find == null) {
                    return null;
                }
                SwitchPoint sp = find.getProperty().getBuiltinSwitchPoint();
                if (sp instanceof Context.BuiltinSwitchPoint && !sp.hasBeenInvalidated()) {
                    return new GuardedInvocation(GlobalConstants.staticConstantGetter(find.getObjectValue()), guard, sp, null);
                }
                if (!find.isInheritedOrdinaryProperty() || (link = (proto = wrappedReceiver.getProto()).lookup(desc, request)) == null) break;
                MethodHandle invocation = link.getInvocation();
                MethodHandle adaptedInvocation = Lookup.MH.asType(invocation, invocation.type().changeParameterType(0, Object.class));
                MethodHandle method = Lookup.MH.filterArguments(adaptedInvocation, 0, protoFilter);
                MethodHandle protoGuard = Lookup.MH.filterArguments(link.getGuard(), 0, protoFilter);
                return new GuardedInvocation(method, NashornGuards.combineGuards(guard, protoGuard));
            }
            case SET: {
                return PrimitiveLookup.getPrimitiveSetter(name, guard, wrapFilter, NashornCallSiteDescriptor.isStrict(desc));
            }
        }
        GuardedInvocation link = wrappedReceiver.lookup(desc, request);
        if (link != null) {
            MethodHandle method = link.getInvocation();
            TypeDescriptor.OfField receiverType = method.type().parameterType(0);
            if (receiverType != Object.class) {
                MethodType wrapType = wrapFilter.type();
                assert (((Class)receiverType).isAssignableFrom((Class<?>)wrapType.returnType()));
                method = Lookup.MH.filterArguments(method, 0, Lookup.MH.asType(wrapFilter, wrapType.changeReturnType((Class<?>)receiverType)));
            }
            return new GuardedInvocation(method, guard, link.getSwitchPoints(), null);
        }
        return null;
    }

    private static GuardedInvocation getPrimitiveSetter(String name, MethodHandle guard, MethodHandle wrapFilter, boolean isStrict) {
        MethodHandle target;
        MethodHandle filter = Lookup.MH.asType(wrapFilter, wrapFilter.type().changeReturnType(ScriptObject.class));
        if (name == null) {
            filter = Lookup.MH.dropArguments(filter, 1, Object.class, Object.class);
            target = Lookup.MH.insertArguments(PRIMITIVE_SETTER, 3, isStrict);
        } else {
            filter = Lookup.MH.dropArguments(filter, 1, Object.class);
            target = Lookup.MH.insertArguments(PRIMITIVE_SETTER, 2, name, isStrict);
        }
        return new GuardedInvocation(Lookup.MH.foldArguments(target, filter), guard);
    }

    private static void primitiveSetter(ScriptObject wrappedSelf, Object self, Object key, boolean strict, Object value) {
        String name = JSType.toString(key);
        FindProperty find = wrappedSelf.findProperty(name, true);
        if (find == null || !find.getProperty().isAccessorProperty() || !find.getProperty().hasNativeSetter()) {
            if (strict) {
                if (find == null || !find.getProperty().isAccessorProperty()) {
                    throw ECMAErrors.typeError("property.not.writable", name, ScriptRuntime.safeToString(self));
                }
                throw ECMAErrors.typeError("property.has.no.setter", name, ScriptRuntime.safeToString(self));
            }
            return;
        }
        find.setValue(value, strict);
    }

    private static MethodHandle findOwnMH(String name, MethodType type) {
        return Lookup.MH.findStatic(MethodHandles.lookup(), PrimitiveLookup.class, name, type);
    }
}
