package org.openjdk.nashorn.internal.runtime.linker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.StandardOperation;
import jdk.dynalink.beans.StaticClass;
import jdk.dynalink.linker.support.SimpleLinkRequest;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.ECMAException;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.linker.ClassAndLoader;
import org.openjdk.nashorn.internal.runtime.linker.JavaAdapterBytecodeGenerator;
import org.openjdk.nashorn.internal.runtime.linker.JavaAdapterClassLoader;
import org.openjdk.nashorn.internal.runtime.linker.JavaAdapterServices;
import org.openjdk.nashorn.internal.runtime.linker.ReflectionCheckLinker;

public final class JavaAdapterFactory {
    private static final ProtectionDomain MINIMAL_PERMISSION_DOMAIN = JavaAdapterFactory.createMinimalPermissionDomain();
    private static final AccessControlContext CREATE_ADAPTER_INFO_ACC_CTXT = ClassAndLoader.createPermAccCtxt("createClassLoader", "getClassLoader", "accessDeclaredMembers", "accessClassInPackage.org.openjdk.nashorn.internal.runtime");
    private static final ClassValue<Map<List<Class<?>>, AdapterInfo>> ADAPTER_INFO_MAPS = new ClassValue<Map<List<Class<?>>, AdapterInfo>>(){

        @Override
        protected Map<List<Class<?>>, AdapterInfo> computeValue(Class<?> type) {
            return new ConcurrentHashMap();
        }
    };
    private static final ClassValue<Boolean> AUTO_CONVERTIBLE_FROM_FUNCTION = new ClassValue<Boolean>(){

        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                return JavaAdapterFactory.getAdapterInfo(new Class[]{type}).autoConvertibleFromFunction;
            }
            catch (Exception e) {
                return false;
            }
        }
    };

    public static StaticClass getAdapterClassFor(Class<?>[] types, ScriptObject classOverrides, MethodHandles.Lookup lookup) {
        return JavaAdapterFactory.getAdapterClassFor(types, classOverrides, JavaAdapterFactory.getProtectionDomain(lookup));
    }

    private static StaticClass getAdapterClassFor(Class<?>[] types, ScriptObject classOverrides, ProtectionDomain protectionDomain) {
        assert (types != null && types.length > 0);
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            for (Class<?> type : types) {
                Context.checkPackageAccess(type);
                ReflectionCheckLinker.checkReflectionAccess(type, true);
            }
        }
        return JavaAdapterFactory.getAdapterInfo(types).getAdapterClass(classOverrides, protectionDomain);
    }

    private static ProtectionDomain getProtectionDomain(MethodHandles.Lookup lookup) {
        if ((lookup.lookupModes() & 2) == 0) {
            return MINIMAL_PERMISSION_DOMAIN;
        }
        return JavaAdapterFactory.getProtectionDomain(lookup.lookupClass());
    }

    private static ProtectionDomain getProtectionDomain(Class<?> clazz) {
        return AccessController.doPrivileged(clazz::getProtectionDomain);
    }

    public static MethodHandle getConstructor(Class<?> sourceType, Class<?> targetType, MethodHandles.Lookup lookup) throws Exception {
        StaticClass adapterClass = JavaAdapterFactory.getAdapterClassFor(new Class[]{targetType}, null, lookup);
        return Lookup.MH.bindTo(Bootstrap.getLinkerServices().getGuardedInvocation(new SimpleLinkRequest(new CallSiteDescriptor(lookup, StandardOperation.NEW, MethodType.methodType(targetType, StaticClass.class, sourceType)), false, adapterClass, null)).getInvocation(), adapterClass);
    }

    static boolean isAutoConvertibleFromFunction(Class<?> clazz) {
        return AUTO_CONVERTIBLE_FROM_FUNCTION.get(clazz);
    }

    private static AdapterInfo getAdapterInfo(Class<?>[] types) {
        ClassAndLoader definingClassAndLoader = ClassAndLoader.getDefiningClassAndLoader(types);
        Map<List<Class<?>>, AdapterInfo> adapterInfoMap = ADAPTER_INFO_MAPS.get(definingClassAndLoader.getRepresentativeClass());
        return adapterInfoMap.computeIfAbsent(List.of(types), t -> JavaAdapterFactory.createAdapterInfo(t, definingClassAndLoader));
    }

    private static AdapterInfo createAdapterInfo(List<Class<?>> types, ClassAndLoader definingClassAndLoader) {
        Class<?> superClass = null;
        ArrayList interfaces = new ArrayList(types.size());
        HashSet interfacesDedup = new HashSet(Math.max((int)((float)types.size() / 0.75f) + 1, 16));
        for (Class<?> t : types) {
            int mod = t.getModifiers();
            if (!t.isInterface()) {
                if (superClass == t) {
                    throw JavaAdapterFactory.adaptationException(ErrorOutcome.DUPLICATE_TYPE, t.getCanonicalName());
                }
                if (superClass != null) {
                    throw JavaAdapterFactory.adaptationException(ErrorOutcome.MULTIPLE_SUPERCLASSES, t.getCanonicalName() + " and " + superClass.getCanonicalName());
                }
                if (Modifier.isFinal(mod)) {
                    throw JavaAdapterFactory.adaptationException(ErrorOutcome.FINAL_CLASS, t.getCanonicalName());
                }
                superClass = t;
            } else {
                if (interfaces.size() > 65535) {
                    throw JavaAdapterFactory.adaptationException(ErrorOutcome.TOO_MANY_INTERFACES, "65535");
                }
                if (!interfacesDedup.add(t)) {
                    throw JavaAdapterFactory.adaptationException(ErrorOutcome.DUPLICATE_TYPE, t.getCanonicalName());
                }
                interfaces.add(t);
            }
            if (Modifier.isPublic(mod)) continue;
            throw JavaAdapterFactory.adaptationException(ErrorOutcome.NON_PUBLIC_CLASS, t.getCanonicalName());
        }
        Class effectiveSuperClass = superClass == null ? Object.class : superClass;
        return AccessController.doPrivileged(() -> new AdapterInfo(effectiveSuperClass, interfaces, definingClassAndLoader), CREATE_ADAPTER_INFO_ACC_CTXT);
    }

    static ECMAException adaptationException(ErrorOutcome outcome, String ... messageArgs) {
        return ECMAErrors.typeError("extend." + outcome, messageArgs);
    }

    private static ProtectionDomain createMinimalPermissionDomain() {
        Permissions permissions = new Permissions();
        permissions.add(new RuntimePermission("accessClassInPackage.org.openjdk.nashorn.internal.runtime"));
        permissions.add(new RuntimePermission("accessClassInPackage.org.openjdk.nashorn.internal.runtime.linker"));
        return new ProtectionDomain(new CodeSource(null, (CodeSigner[])null), permissions);
    }

    private static class AdapterInfo {
        private static final ClassAndLoader SCRIPT_OBJECT_LOADER = new ClassAndLoader(ScriptFunction.class, true);
        private static final VarHandle INSTANCE_ADAPTERS;
        private final ClassLoader commonLoader;
        private final JavaAdapterClassLoader classAdapterGenerator;
        private final JavaAdapterClassLoader instanceAdapterGenerator;
        private Map<CodeSource, StaticClass> instanceAdapters;
        final boolean autoConvertibleFromFunction;

        AdapterInfo(Class<?> superClass, List<Class<?>> interfaces, ClassAndLoader definingLoader) {
            this.commonLoader = AdapterInfo.findCommonLoader(definingLoader);
            JavaAdapterBytecodeGenerator gen = new JavaAdapterBytecodeGenerator(superClass, interfaces, this.commonLoader, false);
            this.autoConvertibleFromFunction = gen.isAutoConvertibleFromFunction();
            this.instanceAdapterGenerator = gen.createAdapterClassLoader();
            this.classAdapterGenerator = new JavaAdapterBytecodeGenerator(superClass, interfaces, this.commonLoader, true).createAdapterClassLoader();
        }

        StaticClass getAdapterClass(ScriptObject classOverrides, ProtectionDomain protectionDomain) {
            return classOverrides == null ? this.getInstanceAdapterClass(protectionDomain) : this.getClassAdapterClass(classOverrides, protectionDomain);
        }

        private StaticClass getInstanceAdapterClass(ProtectionDomain protectionDomain) {
            Map<CodeSource, StaticClass> ia;
            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource == null) {
                codeSource = MINIMAL_PERMISSION_DOMAIN.getCodeSource();
            }
            if ((ia = this.instanceAdapters) == null) {
                ConcurrentHashMap nia = new ConcurrentHashMap();
                Map xia = INSTANCE_ADAPTERS.compareAndExchange(this, null, nia);
                ia = xia == null ? nia : xia;
            }
            return ia.computeIfAbsent(codeSource, cs -> {
                ProtectionDomain effectiveDomain = cs.equals(MINIMAL_PERMISSION_DOMAIN.getCodeSource()) ? MINIMAL_PERMISSION_DOMAIN : protectionDomain;
                return this.instanceAdapterGenerator.generateClass(this.commonLoader, effectiveDomain);
            });
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private StaticClass getClassAdapterClass(ScriptObject classOverrides, ProtectionDomain protectionDomain) {
            JavaAdapterServices.setClassOverrides(classOverrides);
            try {
                StaticClass staticClass = this.classAdapterGenerator.generateClass(this.commonLoader, protectionDomain);
                return staticClass;
            }
            finally {
                JavaAdapterServices.setClassOverrides(null);
            }
        }

        private static ClassLoader findCommonLoader(ClassAndLoader classAndLoader) {
            if (classAndLoader.canSee(SCRIPT_OBJECT_LOADER)) {
                return classAndLoader.getLoader();
            }
            if (SCRIPT_OBJECT_LOADER.canSee(classAndLoader)) {
                return SCRIPT_OBJECT_LOADER.getLoader();
            }
            throw JavaAdapterFactory.adaptationException(ErrorOutcome.NO_COMMON_LOADER, classAndLoader.getRepresentativeClass().getCanonicalName());
        }

        static {
            try {
                INSTANCE_ADAPTERS = MethodHandles.lookup().findVarHandle(AdapterInfo.class, "instanceAdapters", Map.class);
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static enum ErrorOutcome {
        FINAL_CLASS,
        NON_PUBLIC_CLASS,
        NO_ACCESSIBLE_CONSTRUCTOR,
        MULTIPLE_SUPERCLASSES,
        DUPLICATE_TYPE,
        TOO_MANY_INTERFACES,
        NO_COMMON_LOADER,
        FINAL_FINALIZER;

    }
}
