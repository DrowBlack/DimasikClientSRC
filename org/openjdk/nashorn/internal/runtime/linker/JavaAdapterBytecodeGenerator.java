package org.openjdk.nashorn.internal.runtime.linker;

import java.lang.annotation.Annotation;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.api.scripting.ScriptUtils;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.linker.ClassAndLoader;
import org.openjdk.nashorn.internal.runtime.linker.JavaAdapterClassLoader;
import org.openjdk.nashorn.internal.runtime.linker.JavaAdapterFactory;
import org.openjdk.nashorn.internal.runtime.linker.JavaAdapterServices;
import org.openjdk.nashorn.internal.runtime.linker.NameCodec;

final class JavaAdapterBytecodeGenerator {
    private static final String GLOBAL_FIELD_NAME = "global";
    private static final String DELEGATE_FIELD_NAME = "delegate";
    private static final String IS_FUNCTION_FIELD_NAME = "isFunction";
    private static final String CALL_THIS_FIELD_NAME = "callThis";
    private static final String INIT = "<init>";
    private static final String CLASS_INIT = "<clinit>";
    private static final Type OBJECT_TYPE = Type.getType(Object.class);
    private static final Type SCRIPT_OBJECT_TYPE = Type.getType(ScriptObject.class);
    private static final Type SCRIPT_FUNCTION_TYPE = Type.getType(ScriptFunction.class);
    private static final Type SCRIPT_OBJECT_MIRROR_TYPE = Type.getType(ScriptObjectMirror.class);
    private static final CompilerConstants.Call CHECK_FUNCTION = JavaAdapterBytecodeGenerator.lookupServiceMethod("checkFunction", ScriptFunction.class, Object.class, String.class);
    private static final CompilerConstants.Call EXPORT_RETURN_VALUE = JavaAdapterBytecodeGenerator.lookupServiceMethod("exportReturnValue", Object.class, Object.class);
    private static final CompilerConstants.Call GET_CALL_THIS = JavaAdapterBytecodeGenerator.lookupServiceMethod("getCallThis", Object.class, ScriptFunction.class, Object.class);
    private static final CompilerConstants.Call GET_CLASS_OVERRIDES = JavaAdapterBytecodeGenerator.lookupServiceMethod("getClassOverrides", ScriptObject.class, new Class[0]);
    private static final CompilerConstants.Call GET_NON_NULL_GLOBAL = JavaAdapterBytecodeGenerator.lookupServiceMethod("getNonNullGlobal", ScriptObject.class, new Class[0]);
    private static final CompilerConstants.Call HAS_OWN_TO_STRING = JavaAdapterBytecodeGenerator.lookupServiceMethod("hasOwnToString", Boolean.TYPE, ScriptObject.class);
    private static final CompilerConstants.Call INVOKE_NO_PERMISSIONS = JavaAdapterBytecodeGenerator.lookupServiceMethod("invokeNoPermissions", Void.TYPE, MethodHandle.class, Object.class);
    private static final CompilerConstants.Call NOT_AN_OBJECT = JavaAdapterBytecodeGenerator.lookupServiceMethod("notAnObject", Void.TYPE, Object.class);
    private static final CompilerConstants.Call SET_GLOBAL = JavaAdapterBytecodeGenerator.lookupServiceMethod("setGlobal", Runnable.class, ScriptObject.class);
    private static final CompilerConstants.Call TO_CHAR_PRIMITIVE = JavaAdapterBytecodeGenerator.lookupServiceMethod("toCharPrimitive", Character.TYPE, Object.class);
    private static final CompilerConstants.Call UNSUPPORTED = JavaAdapterBytecodeGenerator.lookupServiceMethod("unsupported", UnsupportedOperationException.class, new Class[0]);
    private static final CompilerConstants.Call WRAP_THROWABLE = JavaAdapterBytecodeGenerator.lookupServiceMethod("wrapThrowable", RuntimeException.class, Throwable.class);
    private static final CompilerConstants.Call UNWRAP_MIRROR = JavaAdapterBytecodeGenerator.lookupServiceMethod("unwrapMirror", ScriptObject.class, Object.class, Boolean.TYPE);
    private static final CompilerConstants.Call UNWRAP = CompilerConstants.staticCallNoLookup(ScriptUtils.class, "unwrap", Object.class, Object.class);
    private static final CompilerConstants.Call CHAR_VALUE_OF = CompilerConstants.staticCallNoLookup(Character.class, "valueOf", Character.class, Character.TYPE);
    private static final CompilerConstants.Call DOUBLE_VALUE_OF = CompilerConstants.staticCallNoLookup(Double.class, "valueOf", Double.class, Double.TYPE);
    private static final CompilerConstants.Call LONG_VALUE_OF = CompilerConstants.staticCallNoLookup(Long.class, "valueOf", Long.class, Long.TYPE);
    private static final CompilerConstants.Call RUN = CompilerConstants.interfaceCallNoLookup(Runnable.class, "run", Void.TYPE, new Class[0]);
    private static final Handle BOOTSTRAP_HANDLE = new Handle(6, Type.getInternalName(JavaAdapterServices.class), "bootstrap", MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, Integer.TYPE).toMethodDescriptorString(), false);
    private static final Handle CREATE_ARRAY_BOOTSTRAP_HANDLE = new Handle(6, Type.getInternalName(JavaAdapterServices.class), "createArrayBootstrap", MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class).toMethodDescriptorString(), false);
    private static final String SCRIPT_OBJECT_TYPE_DESCRIPTOR = SCRIPT_OBJECT_TYPE.getDescriptor();
    private static final String OBJECT_TYPE_DESCRIPTOR = OBJECT_TYPE.getDescriptor();
    private static final String BOOLEAN_TYPE_DESCRIPTOR = Type.BOOLEAN_TYPE.getDescriptor();
    private static final String RUNTIME_EXCEPTION_TYPE_NAME = Type.getInternalName(RuntimeException.class);
    private static final String ERROR_TYPE_NAME = Type.getInternalName(Error.class);
    private static final String THROWABLE_TYPE_NAME = Type.getInternalName(Throwable.class);
    private static final String GET_METHOD_PROPERTY_METHOD_DESCRIPTOR = Type.getMethodDescriptor(OBJECT_TYPE, SCRIPT_OBJECT_TYPE);
    private static final String VOID_METHOD_DESCRIPTOR = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]);
    private static final String ADAPTER_PACKAGE_INTERNAL = "org/openjdk/nashorn/javaadapters/";
    private static final int MAX_GENERATED_TYPE_NAME_LENGTH = 255;
    static final String SUPER_PREFIX = "super$";
    private static final String FINALIZER_DELEGATE_NAME = "$$nashornFinalizerDelegate";
    private static final String FINALIZER_DELEGATE_METHOD_DESCRIPTOR = Type.getMethodDescriptor(Type.VOID_TYPE, OBJECT_TYPE);
    private static final String CALLER_SENSITIVE_CLASS_NAME = "jdk.internal.reflect.CallerSensitive";
    private static final Collection<MethodInfo> EXCLUDED = JavaAdapterBytecodeGenerator.getExcludedMethods();
    private final Class<?> superClass;
    private final List<Class<?>> interfaces;
    private final ClassLoader commonLoader;
    private final boolean classOverride;
    private final String superClassName;
    private final String generatedClassName;
    private final Set<String> abstractMethodNames = new HashSet<String>();
    private final String samName;
    private final Set<MethodInfo> finalMethods = new HashSet<MethodInfo>(EXCLUDED);
    private final Set<MethodInfo> methodInfos = new HashSet<MethodInfo>();
    private final boolean autoConvertibleFromFunction;
    private boolean hasExplicitFinalizer = false;
    private final ClassWriter cw;
    private static final AccessControlContext GET_DECLARED_MEMBERS_ACC_CTXT = ClassAndLoader.createPermAccCtxt("accessDeclaredMembers");

    JavaAdapterBytecodeGenerator(Class<?> superClass, List<Class<?>> interfaces, ClassLoader commonLoader, boolean classOverride) {
        assert (superClass != null && !superClass.isInterface());
        assert (interfaces != null);
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.classOverride = classOverride;
        this.commonLoader = commonLoader;
        this.cw = new ClassWriter(3){

            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                return JavaAdapterBytecodeGenerator.this.getCommonSuperClass(type1, type2);
            }
        };
        this.superClassName = Type.getInternalName(superClass);
        this.generatedClassName = JavaAdapterBytecodeGenerator.getGeneratedClassName(superClass, interfaces);
        this.cw.visit(52, 33, this.generatedClassName, null, this.superClassName, JavaAdapterBytecodeGenerator.getInternalTypeNames(interfaces));
        this.generateField(GLOBAL_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        this.generateField(DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        this.gatherMethods(superClass);
        this.gatherMethods(interfaces);
        if (this.abstractMethodNames.size() == 1) {
            this.samName = this.abstractMethodNames.iterator().next();
            this.generateField(CALL_THIS_FIELD_NAME, OBJECT_TYPE_DESCRIPTOR);
            this.generateField(IS_FUNCTION_FIELD_NAME, BOOLEAN_TYPE_DESCRIPTOR);
        } else {
            this.samName = null;
        }
        if (classOverride) {
            this.generateClassInit();
        }
        this.autoConvertibleFromFunction = this.generateConstructors();
        this.generateMethods();
        this.generateSuperMethods();
        if (this.hasExplicitFinalizer) {
            this.generateFinalizerMethods();
        }
        this.cw.visitEnd();
    }

    private void generateField(String name, String fieldDesc) {
        this.cw.visitField(0x12 | (this.classOverride ? 8 : 0), name, fieldDesc, null, null).visitEnd();
    }

    JavaAdapterClassLoader createAdapterClassLoader() {
        return new JavaAdapterClassLoader(this.generatedClassName, this.cw.toByteArray());
    }

    boolean isAutoConvertibleFromFunction() {
        return this.autoConvertibleFromFunction;
    }

    private static String getGeneratedClassName(Class<?> superType, List<Class<?>> interfaces) {
        Class<?> namingType = superType == Object.class ? (interfaces.isEmpty() ? Object.class : interfaces.get(0)) : superType;
        String namingTypeName = Type.getInternalName(namingType);
        StringBuilder buf = new StringBuilder();
        buf.append(ADAPTER_PACKAGE_INTERNAL).append(namingTypeName.replace('/', '_'));
        Iterator<Class<?>> it = interfaces.iterator();
        if (superType == Object.class && it.hasNext()) {
            it.next();
        }
        while (it.hasNext()) {
            buf.append("$$").append(it.next().getSimpleName());
        }
        return buf.substring(0, Math.min(255, buf.length()));
    }

    private static String[] getInternalTypeNames(List<Class<?>> classes) {
        int interfaceCount = classes.size();
        String[] interfaceNames = new String[interfaceCount];
        for (int i = 0; i < interfaceCount; ++i) {
            interfaceNames[i] = Type.getInternalName(classes.get(i));
        }
        return interfaceNames;
    }

    private void generateClassInit() {
        InstructionAdapter mv = new InstructionAdapter(this.cw.visitMethod(8, CLASS_INIT, VOID_METHOD_DESCRIPTOR, null, null));
        GET_NON_NULL_GLOBAL.invoke(mv);
        mv.putstatic(this.generatedClassName, GLOBAL_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        GET_CLASS_OVERRIDES.invoke(mv);
        if (this.samName != null) {
            mv.dup();
            mv.instanceOf(SCRIPT_FUNCTION_TYPE);
            mv.dup();
            mv.putstatic(this.generatedClassName, IS_FUNCTION_FIELD_NAME, BOOLEAN_TYPE_DESCRIPTOR);
            Label notFunction = new Label();
            mv.ifeq(notFunction);
            mv.dup();
            mv.checkcast(SCRIPT_FUNCTION_TYPE);
            this.emitInitCallThis(mv);
            mv.visitLabel(notFunction);
        }
        mv.putstatic(this.generatedClassName, DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        JavaAdapterBytecodeGenerator.endInitMethod(mv);
    }

    private void emitInitCallThis(InstructionAdapter mv) {
        this.loadField(mv, GLOBAL_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        GET_CALL_THIS.invoke(mv);
        if (this.classOverride) {
            mv.putstatic(this.generatedClassName, CALL_THIS_FIELD_NAME, OBJECT_TYPE_DESCRIPTOR);
        } else {
            mv.putfield(this.generatedClassName, CALL_THIS_FIELD_NAME, OBJECT_TYPE_DESCRIPTOR);
        }
    }

    private boolean generateConstructors() {
        boolean gotCtor = false;
        boolean canBeAutoConverted = false;
        for (Constructor<?> ctor : this.superClass.getDeclaredConstructors()) {
            int modifier = ctor.getModifiers();
            if ((modifier & 5) == 0 || JavaAdapterBytecodeGenerator.isCallerSensitive(ctor)) continue;
            canBeAutoConverted = this.generateConstructors(ctor) | canBeAutoConverted;
            gotCtor = true;
        }
        if (!gotCtor) {
            throw JavaAdapterFactory.adaptationException(JavaAdapterFactory.ErrorOutcome.NO_ACCESSIBLE_CONSTRUCTOR, this.superClass.getCanonicalName());
        }
        return canBeAutoConverted;
    }

    private boolean generateConstructors(Constructor<?> ctor) {
        if (this.classOverride) {
            this.generateDelegatingConstructor(ctor);
            return false;
        }
        this.generateOverridingConstructor(ctor, false);
        if (this.samName == null) {
            return false;
        }
        this.generateOverridingConstructor(ctor, true);
        return ctor.getParameterTypes().length == 0;
    }

    private void generateDelegatingConstructor(Constructor<?> ctor) {
        Type originalCtorType = Type.getType(ctor);
        Type[] argTypes = originalCtorType.getArgumentTypes();
        InstructionAdapter mv = new InstructionAdapter(this.cw.visitMethod(1 | (ctor.isVarArgs() ? 128 : 0), INIT, Type.getMethodDescriptor(originalCtorType.getReturnType(), argTypes), null, null));
        mv.visitCode();
        this.emitSuperConstructorCall(mv, originalCtorType.getDescriptor());
        JavaAdapterBytecodeGenerator.endInitMethod(mv);
    }

    private void generateOverridingConstructor(Constructor<?> ctor, boolean fromFunction) {
        Type extraArgumentType;
        Type originalCtorType = Type.getType(ctor);
        Type[] originalArgTypes = originalCtorType.getArgumentTypes();
        int argLen = originalArgTypes.length;
        Type[] newArgTypes = new Type[argLen + 1];
        newArgTypes[argLen] = extraArgumentType = fromFunction ? SCRIPT_FUNCTION_TYPE : SCRIPT_OBJECT_TYPE;
        System.arraycopy(originalArgTypes, 0, newArgTypes, 0, argLen);
        InstructionAdapter mv = new InstructionAdapter(this.cw.visitMethod(1, INIT, Type.getMethodDescriptor(originalCtorType.getReturnType(), newArgTypes), null, null));
        mv.visitCode();
        int extraArgOffset = this.emitSuperConstructorCall(mv, originalCtorType.getDescriptor());
        mv.visitVarInsn(25, 0);
        GET_NON_NULL_GLOBAL.invoke(mv);
        mv.putfield(this.generatedClassName, GLOBAL_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, extraArgOffset);
        mv.putfield(this.generatedClassName, DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        if (fromFunction) {
            mv.visitVarInsn(25, 0);
            mv.iconst(1);
            mv.putfield(this.generatedClassName, IS_FUNCTION_FIELD_NAME, BOOLEAN_TYPE_DESCRIPTOR);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, extraArgOffset);
            this.emitInitCallThis(mv);
        }
        JavaAdapterBytecodeGenerator.endInitMethod(mv);
        if (!fromFunction) {
            newArgTypes[argLen] = OBJECT_TYPE;
            InstructionAdapter mv2 = new InstructionAdapter(this.cw.visitMethod(1, INIT, Type.getMethodDescriptor(originalCtorType.getReturnType(), newArgTypes), null, null));
            this.generateOverridingConstructorWithObjectParam(mv2, originalCtorType.getDescriptor());
        }
    }

    private void generateOverridingConstructorWithObjectParam(InstructionAdapter mv, String ctorDescriptor) {
        mv.visitCode();
        int extraArgOffset = this.emitSuperConstructorCall(mv, ctorDescriptor);
        mv.visitVarInsn(25, extraArgOffset);
        mv.instanceOf(SCRIPT_OBJECT_MIRROR_TYPE);
        Label notMirror = new Label();
        mv.ifeq(notMirror);
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, extraArgOffset);
        mv.iconst(0);
        UNWRAP_MIRROR.invoke(mv);
        mv.putfield(this.generatedClassName, DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, extraArgOffset);
        mv.iconst(1);
        UNWRAP_MIRROR.invoke(mv);
        mv.putfield(this.generatedClassName, GLOBAL_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        Label done = new Label();
        if (this.samName != null) {
            mv.visitVarInsn(25, 0);
            mv.getfield(this.generatedClassName, DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
            mv.instanceOf(SCRIPT_FUNCTION_TYPE);
            mv.ifeq(done);
            mv.visitVarInsn(25, 0);
            mv.iconst(1);
            mv.putfield(this.generatedClassName, IS_FUNCTION_FIELD_NAME, BOOLEAN_TYPE_DESCRIPTOR);
            mv.visitVarInsn(25, 0);
            mv.dup();
            mv.getfield(this.generatedClassName, DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
            mv.checkcast(SCRIPT_FUNCTION_TYPE);
            this.emitInitCallThis(mv);
            mv.goTo(done);
        }
        mv.visitLabel(notMirror);
        mv.visitVarInsn(25, extraArgOffset);
        NOT_AN_OBJECT.invoke(mv);
        mv.visitLabel(done);
        JavaAdapterBytecodeGenerator.endInitMethod(mv);
    }

    private static void endInitMethod(InstructionAdapter mv) {
        mv.visitInsn(177);
        JavaAdapterBytecodeGenerator.endMethod(mv);
    }

    private static void endMethod(InstructionAdapter mv) {
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void generateMethods() {
        for (MethodInfo mi : this.methodInfos) {
            this.generateMethod(mi);
        }
    }

    private void generateMethod(MethodInfo mi) {
        Label throwableHandler;
        Method method = mi.method;
        Class<?>[] exceptions = method.getExceptionTypes();
        String[] exceptionNames = JavaAdapterBytecodeGenerator.getExceptionNames(exceptions);
        MethodType type = mi.type;
        String methodDesc = type.toMethodDescriptorString();
        String name = mi.getName();
        Type asmType = Type.getMethodType(methodDesc);
        Type[] asmArgTypes = asmType.getArgumentTypes();
        InstructionAdapter mv = new InstructionAdapter(this.cw.visitMethod(JavaAdapterBytecodeGenerator.getAccessModifiers(method), name, methodDesc, null, exceptionNames));
        mv.visitCode();
        TypeDescriptor.OfField returnType = type.returnType();
        Type asmReturnType = Type.getType(returnType);
        int nextLocalVar = 1;
        for (Type t : asmArgTypes) {
            nextLocalVar += t.getSize();
        }
        int globalRestoringRunnableVar = nextLocalVar++;
        this.loadField(mv, GLOBAL_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        SET_GLOBAL.invoke(mv);
        mv.visitVarInsn(58, globalRestoringRunnableVar);
        Label tryBlockStart = new Label();
        mv.visitLabel(tryBlockStart);
        Label callCallee = new Label();
        Label defaultBehavior = new Label();
        if (this.samName != null) {
            this.loadField(mv, IS_FUNCTION_FIELD_NAME, BOOLEAN_TYPE_DESCRIPTOR);
            if (name.equals(this.samName)) {
                Label notFunction = new Label();
                mv.ifeq(notFunction);
                this.loadField(mv, DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
                this.loadField(mv, CALL_THIS_FIELD_NAME, OBJECT_TYPE_DESCRIPTOR);
                mv.goTo(callCallee);
                mv.visitLabel(notFunction);
            } else {
                mv.ifne(defaultBehavior);
            }
        }
        if (name.equals("toString")) {
            this.loadField(mv, DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
            HAS_OWN_TO_STRING.invoke(mv);
            mv.ifeq(defaultBehavior);
        }
        this.loadField(mv, DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        mv.ifnull(defaultBehavior);
        this.loadField(mv, DELEGATE_FIELD_NAME, SCRIPT_OBJECT_TYPE_DESCRIPTOR);
        mv.dup();
        String encodedName = NameCodec.encode(name);
        mv.visitInvokeDynamicInsn(encodedName, GET_METHOD_PROPERTY_METHOD_DESCRIPTOR, BOOTSTRAP_HANDLE, 2);
        mv.visitLdcInsn(name);
        CHECK_FUNCTION.invoke(mv);
        Label hasFunction = new Label();
        mv.dup();
        mv.ifnonnull(hasFunction);
        mv.pop2();
        mv.visitLabel(defaultBehavior);
        Runnable emitFinally = () -> JavaAdapterBytecodeGenerator.emitFinally(mv, globalRestoringRunnableVar);
        Label normalFinally = new Label();
        if (Modifier.isAbstract(method.getModifiers())) {
            UNSUPPORTED.invoke(mv);
            mv.athrow();
        } else {
            this.emitSuperCall(mv, method.getDeclaringClass(), name, methodDesc);
            mv.goTo(normalFinally);
        }
        mv.visitLabel(hasFunction);
        mv.swap();
        mv.visitLabel(callCallee);
        int varOffset = 1;
        boolean isVarArgCall = JavaAdapterBytecodeGenerator.getParamListLengthInSlots(asmArgTypes) > 253;
        for (Type t : asmArgTypes) {
            mv.load(varOffset, t);
            JavaAdapterBytecodeGenerator.convertParam(mv, t, isVarArgCall);
            varOffset += t.getSize();
        }
        if (isVarArgCall) {
            mv.visitInvokeDynamicInsn(NameCodec.EMPTY_NAME, JavaAdapterBytecodeGenerator.getArrayCreatorMethodType(type).toMethodDescriptorString(), CREATE_ARRAY_BOOTSTRAP_HANDLE, new Object[0]);
        }
        mv.visitInvokeDynamicInsn(encodedName, JavaAdapterBytecodeGenerator.getCallMethodType(isVarArgCall, type).toMethodDescriptorString(), BOOTSTRAP_HANDLE, 8);
        JavaAdapterBytecodeGenerator.convertReturnValue(mv, returnType);
        mv.visitLabel(normalFinally);
        emitFinally.run();
        mv.areturn(asmReturnType);
        boolean throwableDeclared = JavaAdapterBytecodeGenerator.isThrowableDeclared(exceptions);
        if (!throwableDeclared) {
            throwableHandler = new Label();
            mv.visitLabel(throwableHandler);
            WRAP_THROWABLE.invoke(mv);
        } else {
            throwableHandler = null;
        }
        Label rethrowHandler = new Label();
        mv.visitLabel(rethrowHandler);
        emitFinally.run();
        mv.athrow();
        if (throwableDeclared) {
            mv.visitTryCatchBlock(tryBlockStart, normalFinally, rethrowHandler, THROWABLE_TYPE_NAME);
            assert (throwableHandler == null);
        } else {
            mv.visitTryCatchBlock(tryBlockStart, normalFinally, rethrowHandler, RUNTIME_EXCEPTION_TYPE_NAME);
            mv.visitTryCatchBlock(tryBlockStart, normalFinally, rethrowHandler, ERROR_TYPE_NAME);
            for (String excName : exceptionNames) {
                mv.visitTryCatchBlock(tryBlockStart, normalFinally, rethrowHandler, excName);
            }
            mv.visitTryCatchBlock(tryBlockStart, normalFinally, throwableHandler, THROWABLE_TYPE_NAME);
        }
        JavaAdapterBytecodeGenerator.endMethod(mv);
    }

    private static MethodType getCallMethodType(boolean isVarArgCall, MethodType type) {
        Class[] callParamTypes;
        if (isVarArgCall) {
            callParamTypes = new Class[]{Object.class, Object.class, Object[].class};
        } else {
            Class<?>[] origParamTypes = type.parameterArray();
            callParamTypes = new Class[origParamTypes.length + 2];
            callParamTypes[0] = Object.class;
            callParamTypes[1] = Object.class;
            for (int i = 0; i < origParamTypes.length; ++i) {
                callParamTypes[i + 2] = JavaAdapterBytecodeGenerator.getNashornParamType(origParamTypes[i], false);
            }
        }
        return MethodType.methodType(JavaAdapterBytecodeGenerator.getNashornReturnType(type.returnType()), callParamTypes);
    }

    private static MethodType getArrayCreatorMethodType(MethodType type) {
        Class<?>[] callParamTypes = type.parameterArray();
        for (int i = 0; i < callParamTypes.length; ++i) {
            callParamTypes[i] = JavaAdapterBytecodeGenerator.getNashornParamType(callParamTypes[i], true);
        }
        return MethodType.methodType(Object[].class, callParamTypes);
    }

    private static Class<?> getNashornParamType(Class<?> clazz, boolean varArg) {
        if (clazz == Byte.TYPE || clazz == Short.TYPE) {
            return Integer.TYPE;
        }
        if (clazz == Float.TYPE) {
            return varArg ? Object.class : Double.TYPE;
        }
        if (!clazz.isPrimitive() || clazz == Long.TYPE || clazz == Character.TYPE) {
            return Object.class;
        }
        return clazz;
    }

    private static Class<?> getNashornReturnType(Class<?> clazz) {
        if (clazz == Byte.TYPE || clazz == Short.TYPE) {
            return Integer.TYPE;
        }
        if (clazz == Float.TYPE) {
            return Double.TYPE;
        }
        if (clazz == Void.TYPE || clazz == Character.TYPE) {
            return Object.class;
        }
        return clazz;
    }

    private void loadField(InstructionAdapter mv, String name, String desc) {
        if (this.classOverride) {
            mv.getstatic(this.generatedClassName, name, desc);
        } else {
            mv.visitVarInsn(25, 0);
            mv.getfield(this.generatedClassName, name, desc);
        }
    }

    private static void convertReturnValue(InstructionAdapter mv, Class<?> origReturnType) {
        if (origReturnType == Void.TYPE) {
            mv.pop();
        } else if (origReturnType == Object.class) {
            EXPORT_RETURN_VALUE.invoke(mv);
        } else if (origReturnType == Byte.TYPE) {
            mv.visitInsn(145);
        } else if (origReturnType == Short.TYPE) {
            mv.visitInsn(147);
        } else if (origReturnType == Float.TYPE) {
            mv.visitInsn(144);
        } else if (origReturnType == Character.TYPE) {
            TO_CHAR_PRIMITIVE.invoke(mv);
        }
    }

    private static void convertParam(InstructionAdapter mv, Type t, boolean varArg) {
        switch (t.getSort()) {
            case 2: {
                CHAR_VALUE_OF.invoke(mv);
                break;
            }
            case 6: {
                mv.visitInsn(141);
                if (!varArg) break;
                DOUBLE_VALUE_OF.invoke(mv);
                break;
            }
            case 7: {
                LONG_VALUE_OF.invoke(mv);
                break;
            }
            case 10: {
                if (!t.equals(OBJECT_TYPE)) break;
                UNWRAP.invoke(mv);
            }
        }
    }

    private static int getParamListLengthInSlots(Type[] paramTypes) {
        int len = paramTypes.length;
        for (Type t : paramTypes) {
            int sort = t.getSort();
            if (sort != 6 && sort != 8) continue;
            ++len;
        }
        return len;
    }

    private static void emitFinally(InstructionAdapter mv, int globalRestoringRunnableVar) {
        mv.visitVarInsn(25, globalRestoringRunnableVar);
        RUN.invoke(mv);
    }

    private static boolean isThrowableDeclared(Class<?>[] exceptions) {
        for (Class<?> exception : exceptions) {
            if (exception != Throwable.class) continue;
            return true;
        }
        return false;
    }

    private void generateSuperMethods() {
        for (MethodInfo mi : this.methodInfos) {
            if (Modifier.isAbstract(mi.method.getModifiers())) continue;
            this.generateSuperMethod(mi);
        }
    }

    private void generateSuperMethod(MethodInfo mi) {
        Method method = mi.method;
        String methodDesc = mi.type.toMethodDescriptorString();
        String name = mi.getName();
        InstructionAdapter mv = new InstructionAdapter(this.cw.visitMethod(JavaAdapterBytecodeGenerator.getAccessModifiers(method), SUPER_PREFIX + name, methodDesc, null, JavaAdapterBytecodeGenerator.getExceptionNames(method.getExceptionTypes())));
        mv.visitCode();
        this.emitSuperCall(mv, method.getDeclaringClass(), name, methodDesc);
        mv.areturn(Type.getType(mi.type.returnType()));
        JavaAdapterBytecodeGenerator.endMethod(mv);
    }

    private Class<?> findInvokespecialOwnerFor(Class<?> cl) {
        assert (Modifier.isInterface(cl.getModifiers())) : cl + " is not an interface";
        if (cl.isAssignableFrom(this.superClass)) {
            return this.superClass;
        }
        for (Class<?> iface : this.interfaces) {
            if (!cl.isAssignableFrom(iface)) continue;
            return iface;
        }
        throw new AssertionError((Object)("can't find the class/interface that extends " + cl));
    }

    private int emitSuperConstructorCall(InstructionAdapter mv, String methodDesc) {
        return this.emitSuperCall(mv, null, INIT, methodDesc, true);
    }

    private void emitSuperCall(InstructionAdapter mv, Class<?> owner, String name, String methodDesc) {
        this.emitSuperCall(mv, owner, name, methodDesc, false);
    }

    private int emitSuperCall(InstructionAdapter mv, Class<?> owner, String name, String methodDesc, boolean constructor) {
        mv.visitVarInsn(25, 0);
        int nextParam = 1;
        Type methodType = Type.getMethodType(methodDesc);
        for (Type t : methodType.getArgumentTypes()) {
            mv.load(nextParam, t);
            nextParam += t.getSize();
        }
        if (!constructor && Modifier.isInterface(owner.getModifiers())) {
            Class<?> superType = this.findInvokespecialOwnerFor(owner);
            mv.visitMethodInsn(183, Type.getInternalName(superType), name, methodDesc, Modifier.isInterface(superType.getModifiers()));
        } else {
            mv.invokespecial(this.superClassName, name, methodDesc, false);
        }
        return nextParam;
    }

    private void generateFinalizerMethods() {
        this.generateFinalizerDelegate();
        this.generateFinalizerOverride();
    }

    private void generateFinalizerDelegate() {
        InstructionAdapter mv = new InstructionAdapter(this.cw.visitMethod(10, FINALIZER_DELEGATE_NAME, FINALIZER_DELEGATE_METHOD_DESCRIPTOR, null, null));
        mv.visitVarInsn(25, 0);
        mv.checkcast(Type.getType("L" + this.generatedClassName + ";"));
        mv.invokespecial(this.superClassName, "finalize", VOID_METHOD_DESCRIPTOR, false);
        mv.visitInsn(177);
        JavaAdapterBytecodeGenerator.endMethod(mv);
    }

    private void generateFinalizerOverride() {
        InstructionAdapter mv = new InstructionAdapter(this.cw.visitMethod(1, "finalize", VOID_METHOD_DESCRIPTOR, null, null));
        mv.aconst(new Handle(6, this.generatedClassName, FINALIZER_DELEGATE_NAME, FINALIZER_DELEGATE_METHOD_DESCRIPTOR, false));
        mv.visitVarInsn(25, 0);
        INVOKE_NO_PERMISSIONS.invoke(mv);
        mv.visitInsn(177);
        JavaAdapterBytecodeGenerator.endMethod(mv);
    }

    private static String[] getExceptionNames(Class<?>[] exceptions) {
        String[] exceptionNames = new String[exceptions.length];
        for (int i = 0; i < exceptions.length; ++i) {
            exceptionNames[i] = Type.getInternalName(exceptions[i]);
        }
        return exceptionNames;
    }

    private static int getAccessModifiers(Method method) {
        return 1 | (method.isVarArgs() ? 128 : 0);
    }

    private void gatherMethods(Class<?> type) {
        if (Modifier.isPublic(type.getModifiers())) {
            Method[] typeMethods = type.isInterface() ? type.getMethods() : type.getDeclaredMethods();
            for (GenericDeclaration genericDeclaration : typeMethods) {
                int m;
                String name = ((Method)genericDeclaration).getName();
                if (name.startsWith(SUPER_PREFIX) || Modifier.isStatic(m = ((Method)genericDeclaration).getModifiers()) || !Modifier.isPublic(m) && !Modifier.isProtected(m)) continue;
                if (name.equals("finalize") && ((Method)genericDeclaration).getParameterCount() == 0) {
                    if (type == Object.class) continue;
                    this.hasExplicitFinalizer = true;
                    if (!Modifier.isFinal(m)) continue;
                    throw JavaAdapterFactory.adaptationException(JavaAdapterFactory.ErrorOutcome.FINAL_FINALIZER, type.getCanonicalName());
                }
                MethodInfo mi = new MethodInfo((Method)genericDeclaration);
                if (Modifier.isFinal(m) || JavaAdapterBytecodeGenerator.isCallerSensitive((Executable)genericDeclaration)) {
                    this.finalMethods.add(mi);
                    continue;
                }
                if (this.finalMethods.contains(mi) || !this.methodInfos.add(mi) || !Modifier.isAbstract(m)) continue;
                this.abstractMethodNames.add(mi.getName());
            }
        }
        if (!type.isInterface()) {
            Class<?> superType = type.getSuperclass();
            if (superType != null) {
                this.gatherMethods(superType);
            }
            for (GenericDeclaration genericDeclaration : type.getInterfaces()) {
                this.gatherMethods((Class<?>)genericDeclaration);
            }
        }
    }

    private void gatherMethods(List<Class<?>> classes) {
        for (Class<?> c : classes) {
            this.gatherMethods(c);
        }
    }

    private static Collection<MethodInfo> getExcludedMethods() {
        return AccessController.doPrivileged(() -> {
            try {
                return List.of(new MethodInfo(Object.class, "finalize", new Class[0]), new MethodInfo(Object.class, "clone", new Class[0]));
            }
            catch (NoSuchMethodException e) {
                throw new AssertionError((Object)e);
            }
        }, GET_DECLARED_MEMBERS_ACC_CTXT);
    }

    private String getCommonSuperClass(String type1, String type2) {
        try {
            Class<?> c1 = Class.forName(type1.replace('/', '.'), false, this.commonLoader);
            Class<?> c2 = Class.forName(type2.replace('/', '.'), false, this.commonLoader);
            if (c1.isAssignableFrom(c2)) {
                return type1;
            }
            if (c2.isAssignableFrom(c1)) {
                return type2;
            }
            if (c1.isInterface() || c2.isInterface()) {
                return OBJECT_TYPE.getInternalName();
            }
            return JavaAdapterBytecodeGenerator.assignableSuperClass(c1, c2).getName().replace('.', '/');
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> assignableSuperClass(Class<?> c1, Class<?> c2) {
        Class<?> superClass = c1.getSuperclass();
        return superClass.isAssignableFrom(c2) ? superClass : JavaAdapterBytecodeGenerator.assignableSuperClass(superClass, c2);
    }

    private static boolean isCallerSensitive(Executable e) {
        for (Annotation ann : e.getAnnotations()) {
            if (!CALLER_SENSITIVE_CLASS_NAME.equals(ann.annotationType().getName())) continue;
            return true;
        }
        return false;
    }

    private static CompilerConstants.Call lookupServiceMethod(String name, Class<?> rtype, Class<?> ... ptypes) {
        return CompilerConstants.staticCallNoLookup(JavaAdapterServices.class, name, rtype, ptypes);
    }

    private static class MethodInfo {
        private final Method method;
        private final MethodType type;

        private MethodInfo(Class<?> clazz, String name, Class<?> ... argTypes) throws NoSuchMethodException {
            this(clazz.getDeclaredMethod(name, argTypes));
        }

        private MethodInfo(Method method) {
            this.method = method;
            this.type = Lookup.MH.type(method.getReturnType(), method.getParameterTypes());
        }

        public boolean equals(Object obj) {
            return obj instanceof MethodInfo && this.equals((MethodInfo)obj);
        }

        private boolean equals(MethodInfo other) {
            return this.getName().equals(other.getName()) && this.type.equals((Object)other.type);
        }

        String getName() {
            return this.method.getName();
        }

        public int hashCode() {
            return this.getName().hashCode() ^ this.type.hashCode();
        }
    }
}
