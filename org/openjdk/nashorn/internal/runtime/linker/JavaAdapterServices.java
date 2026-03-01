package org.openjdk.nashorn.internal.runtime.linker;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.Objects;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.linker.JavaArgumentConverters;
import org.openjdk.nashorn.internal.runtime.linker.NashornBeansLinker;

public final class JavaAdapterServices {
    private static final ThreadLocal<ScriptObject> classOverrides = new ThreadLocal();
    private static final MethodHandle NO_PERMISSIONS_INVOKER = JavaAdapterServices.createNoPermissionsInvoker();

    private JavaAdapterServices() {
    }

    public static Object getCallThis(ScriptFunction delegate, Object global) {
        return delegate.isStrict() ? ScriptRuntime.UNDEFINED : global;
    }

    public static void notAnObject(Object obj) {
        throw ECMAErrors.typeError("not.an.object", ScriptRuntime.safeToString(obj));
    }

    public static ScriptFunction checkFunction(Object callee, String name) {
        if (callee instanceof ScriptFunction) {
            return (ScriptFunction)callee;
        }
        if (JSType.nullOrUndefined(callee)) {
            return null;
        }
        throw ECMAErrors.typeError("not.a.function.value", name, ScriptRuntime.safeToString(callee));
    }

    public static ScriptObject getClassOverrides() {
        ScriptObject overrides = classOverrides.get();
        assert (overrides != null);
        return overrides;
    }

    public static void invokeNoPermissions(MethodHandle method, Object arg) throws Throwable {
        NO_PERMISSIONS_INVOKER.invokeExact(method, arg);
    }

    public static Runnable setGlobal(ScriptObject adapterGlobal) {
        Global currentGlobal = Context.getGlobal();
        if (adapterGlobal != currentGlobal) {
            Context.setGlobal(adapterGlobal);
            return () -> Context.setGlobal(currentGlobal);
        }
        return () -> {};
    }

    public static ScriptObject getNonNullGlobal() {
        return Objects.requireNonNull(Context.getGlobal(), "Current global is null");
    }

    public static boolean hasOwnToString(ScriptObject sobj) {
        return sobj.getMap().findProperty("toString") != null;
    }

    public static ScriptObject unwrapMirror(Object mirror, boolean getGlobal) {
        assert (mirror instanceof ScriptObjectMirror);
        try {
            Field field = getGlobal ? MirrorFieldHolder.GLOBAL_FIELD : MirrorFieldHolder.SOBJ_FIELD;
            return (ScriptObject)field.get(mirror);
        }
        catch (IllegalAccessException x) {
            throw new RuntimeException(x);
        }
    }

    public static CallSite bootstrap(MethodHandles.Lookup lookup, String opDesc, MethodType type, int flags) {
        return Bootstrap.bootstrap(lookup, opDesc, type, flags);
    }

    static void setClassOverrides(ScriptObject overrides) {
        classOverrides.set(overrides);
    }

    private static MethodHandle createNoPermissionsInvoker() {
        String className = "NoPermissionsInvoker";
        ClassWriter cw = new ClassWriter(3);
        cw.visit(51, 49, "NoPermissionsInvoker", null, "java/lang/Object", null);
        Type objectType = Type.getType(Object.class);
        Type methodHandleType = Type.getType(MethodHandle.class);
        InstructionAdapter mv = new InstructionAdapter(cw.visitMethod(9, "invoke", Type.getMethodDescriptor(Type.VOID_TYPE, methodHandleType, objectType), null, null));
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.invokevirtual(methodHandleType.getInternalName(), "invokeExact", Type.getMethodDescriptor(Type.VOID_TYPE, objectType), false);
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
        final byte[] bytes = cw.toByteArray();
        ClassLoader loader = AccessController.doPrivileged(() -> new SecureClassLoader(null){

            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if (name.equals("NoPermissionsInvoker")) {
                    return this.defineClass(name, bytes, 0, bytes.length, new ProtectionDomain(new CodeSource(null, (CodeSigner[])null), new Permissions()));
                }
                throw new ClassNotFoundException(name);
            }
        });
        try {
            return MethodHandles.publicLookup().findStatic(Class.forName("NoPermissionsInvoker", true, loader), "invoke", MethodType.methodType(Void.TYPE, MethodHandle.class, Object.class));
        }
        catch (ReflectiveOperationException e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }

    public static Object exportReturnValue(Object obj) {
        return NashornBeansLinker.exportArgument(obj, true);
    }

    public static char toCharPrimitive(Object obj) {
        return JavaArgumentConverters.toCharPrimitive(obj);
    }

    public static RuntimeException wrapThrowable(Throwable t) {
        return new RuntimeException(t);
    }

    public static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException();
    }

    public static CallSite createArrayBootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
        return new ConstantCallSite(MethodHandles.identity(Object[].class).asCollector(Object[].class, type.parameterCount()).asType(type));
    }

    private static class MirrorFieldHolder {
        private static final Field SOBJ_FIELD = MirrorFieldHolder.getMirrorField("sobj");
        private static final Field GLOBAL_FIELD = MirrorFieldHolder.getMirrorField("global");

        private MirrorFieldHolder() {
        }

        private static Field getMirrorField(String fieldName) {
            try {
                Field field = ScriptObjectMirror.class.getDeclaredField(fieldName);
                AccessController.doPrivileged(() -> {
                    field.setAccessible(true);
                    return null;
                });
                return field;
            }
            catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
