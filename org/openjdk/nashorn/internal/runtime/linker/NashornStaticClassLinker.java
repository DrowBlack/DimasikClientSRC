package org.openjdk.nashorn.internal.runtime.linker;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.NamedOperation;
import jdk.dynalink.StandardOperation;
import jdk.dynalink.beans.BeansLinker;
import jdk.dynalink.beans.StaticClass;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.GuardingDynamicLinker;
import jdk.dynalink.linker.LinkRequest;
import jdk.dynalink.linker.LinkerServices;
import jdk.dynalink.linker.TypeBasedGuardingDynamicLinker;
import jdk.dynalink.linker.support.Guards;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.linker.JavaAdapterFactory;
import org.openjdk.nashorn.internal.runtime.linker.NashornBeansLinker;
import org.openjdk.nashorn.internal.runtime.linker.NashornCallSiteDescriptor;
import org.openjdk.nashorn.internal.runtime.linker.NashornLinker;

final class NashornStaticClassLinker
implements TypeBasedGuardingDynamicLinker {
    private final GuardingDynamicLinker staticClassLinker;

    NashornStaticClassLinker(BeansLinker beansLinker) {
        this.staticClassLinker = beansLinker.getLinkerForClass(StaticClass.class);
    }

    @Override
    public boolean canLinkType(Class<?> type) {
        return type == StaticClass.class;
    }

    @Override
    public GuardedInvocation getGuardedInvocation(LinkRequest request, LinkerServices linkerServices) throws Exception {
        Object self = request.getReceiver();
        if (self == null || self.getClass() != StaticClass.class) {
            return null;
        }
        Class<?> receiverClass = ((StaticClass)self).getRepresentedClass();
        Bootstrap.checkReflectionAccess(receiverClass, true);
        CallSiteDescriptor desc = request.getCallSiteDescriptor();
        if (NamedOperation.getBaseOperation(desc.getOperation()) == StandardOperation.NEW) {
            if (!Modifier.isPublic(receiverClass.getModifiers())) {
                throw ECMAErrors.typeError("new.on.nonpublic.javatype", receiverClass.getName());
            }
            Context.checkPackageAccess(receiverClass);
            if (NashornLinker.isAbstractClass(receiverClass)) {
                Object[] args = request.getArguments();
                MethodHandles.Lookup lookup = NashornCallSiteDescriptor.getLookupInternal(request.getCallSiteDescriptor());
                args[0] = JavaAdapterFactory.getAdapterClassFor(new Class[]{receiverClass}, null, lookup);
                LinkRequest adapterRequest = request.replaceArguments(request.getCallSiteDescriptor(), args);
                GuardedInvocation gi = NashornStaticClassLinker.checkNullConstructor(this.delegate(linkerServices, adapterRequest), receiverClass);
                return gi.replaceMethods(gi.getInvocation(), Guards.getIdentityGuard(self));
            }
            return NashornStaticClassLinker.checkNullConstructor(this.delegate(linkerServices, request), receiverClass);
        }
        return this.delegate(linkerServices, request);
    }

    private GuardedInvocation delegate(LinkerServices linkerServices, LinkRequest request) throws Exception {
        return NashornBeansLinker.getGuardedInvocation(this.staticClassLinker, request, linkerServices);
    }

    private static GuardedInvocation checkNullConstructor(GuardedInvocation ctorInvocation, Class<?> receiverClass) {
        if (ctorInvocation == null) {
            throw ECMAErrors.typeError("no.constructor.matches.args", receiverClass.getName());
        }
        return ctorInvocation;
    }
}
