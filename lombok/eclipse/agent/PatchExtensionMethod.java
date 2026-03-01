package lombok.eclipse.agent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.FieldAugment;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.TransformEclipseAST;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.experimental.ExtensionMethod;
import lombok.permit.Permit;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.search.matching.MethodPattern;

public class PatchExtensionMethod {
    private static final FieldAugment<MessageSend, PostponedError> MessageSend_postponedErrors = FieldAugment.augment(MessageSend.class, PostponedError.class, "lombok$postponedErrors");

    public static EclipseNode getTypeNode(TypeDeclaration decl) {
        CompilationUnitDeclaration cud = decl.scope.compilationUnitScope().referenceContext;
        EclipseAST astNode = TransformEclipseAST.getAST(cud, false);
        EclipseNode node = (EclipseNode)astNode.get(decl);
        if (node == null) {
            astNode = TransformEclipseAST.getAST(cud, true);
            node = (EclipseNode)astNode.get(decl);
        }
        return node;
    }

    public static Annotation getAnnotation(Class<? extends java.lang.annotation.Annotation> expectedType, EclipseNode node) {
        TypeDeclaration decl = (TypeDeclaration)node.get();
        if (decl.annotations != null) {
            Annotation[] annotationArray = decl.annotations;
            int n = decl.annotations.length;
            int n2 = 0;
            while (n2 < n) {
                Annotation ann = annotationArray[n2];
                if (EclipseHandlerUtil.typeMatches(expectedType, node, ann.type)) {
                    return ann;
                }
                ++n2;
            }
        }
        return null;
    }

    static EclipseNode upToType(EclipseNode typeNode) {
        EclipseNode node = typeNode;
        while ((node = (EclipseNode)node.up()) != null && node.getKind() != AST.Kind.TYPE) {
        }
        return node;
    }

    static List<Extension> getApplicableExtensionMethods(EclipseNode typeNode, Annotation ann, TypeBinding receiverType) {
        ArrayList<Extension> extensions = new ArrayList<Extension>();
        if (typeNode != null && ann != null && receiverType != null) {
            MethodScope blockScope = ((TypeDeclaration)typeNode.get()).initializerScope;
            EclipseNode annotationNode = (EclipseNode)typeNode.getNodeFor(ann);
            AnnotationValues<ExtensionMethod> annotation = EclipseHandlerUtil.createAnnotation(ExtensionMethod.class, annotationNode);
            boolean suppressBaseMethods = false;
            try {
                suppressBaseMethods = annotation.getInstance().suppressBaseMethods();
            }
            catch (AnnotationValues.AnnotationValueDecodeFail fail) {
                fail.owner.setError(fail.getMessage(), fail.idx);
            }
            for (Object extensionMethodProvider : annotation.getActualExpressions("value")) {
                TypeBinding binding;
                if (!(extensionMethodProvider instanceof ClassLiteralAccess) || (binding = ((ClassLiteralAccess)extensionMethodProvider).type.resolveType((BlockScope)blockScope)) == null || !binding.isClass() && !binding.isEnum()) continue;
                Extension e = new Extension();
                e.extensionMethods = PatchExtensionMethod.getApplicableExtensionMethodsDefinedInProvider(typeNode, (ReferenceBinding)binding, receiverType);
                e.suppressBaseMethods = suppressBaseMethods;
                extensions.add(e);
            }
        }
        return extensions;
    }

    private static List<MethodBinding> getApplicableExtensionMethodsDefinedInProvider(EclipseNode typeNode, ReferenceBinding extensionMethodProviderBinding, TypeBinding receiverType) {
        ArrayList<MethodBinding> extensionMethods = new ArrayList<MethodBinding>();
        MethodBinding[] methodBindingArray = extensionMethodProviderBinding.methods();
        int n = methodBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            TypeBinding firstArgType;
            MethodBinding method = methodBindingArray[n2];
            if (method.isStatic() && method.isPublic() && method.parameters != null && method.parameters.length != 0 && (!receiverType.isProvablyDistinct(firstArgType = method.parameters[0]) || receiverType.isCompatibleWith(firstArgType.erasure()))) {
                extensionMethods.add(method);
            }
            ++n2;
        }
        return extensionMethods;
    }

    public static void errorNoMethodFor(ProblemReporter problemReporter, MessageSend messageSend, TypeBinding recType, TypeBinding[] params) {
        MessageSend_postponedErrors.set(messageSend, new PostponedNoMethodError(problemReporter, messageSend, recType, params));
    }

    public static void invalidMethod(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method) {
        MessageSend_postponedErrors.set(messageSend, new PostponedInvalidMethodError(problemReporter, messageSend, method, null));
    }

    public static void invalidMethod(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method, Scope scope) {
        MessageSend_postponedErrors.set(messageSend, new PostponedInvalidMethodError(problemReporter, messageSend, method, scope));
    }

    public static void nonStaticAccessToStaticMethod(ProblemReporter problemReporter, ASTNode location, MethodBinding method, MessageSend messageSend) {
        MessageSend_postponedErrors.set(messageSend, new PostponedNonStaticAccessToStaticMethodError(problemReporter, location, method));
    }

    public static TypeBinding resolveType(TypeBinding resolvedType, MessageSend methodCall, BlockScope scope) {
        PostponedError error;
        Binding binding;
        ArrayList<Extension> extensions = new ArrayList<Extension>();
        TypeDeclaration decl = scope.classScope().referenceContext;
        EclipseNode owningType = null;
        EclipseNode typeNode = PatchExtensionMethod.getTypeNode(decl);
        while (typeNode != null) {
            Annotation ann = PatchExtensionMethod.getAnnotation(ExtensionMethod.class, typeNode);
            if (ann != null) {
                extensions.addAll(0, PatchExtensionMethod.getApplicableExtensionMethods(typeNode, ann, methodCall.receiver.resolvedType));
                if (owningType == null) {
                    owningType = typeNode;
                }
            }
            typeNode = PatchExtensionMethod.upToType(typeNode);
        }
        boolean skip = false;
        if (methodCall.receiver instanceof ThisReference && (((ThisReference)methodCall.receiver).bits & 4) != 0) {
            skip = true;
        }
        if (methodCall.receiver instanceof SuperReference) {
            skip = true;
        }
        if (methodCall.receiver instanceof NameReference && (binding = ((NameReference)methodCall.receiver).binding) instanceof TypeBinding) {
            skip = true;
        }
        if (Reflection.argumentsHaveErrors != null) {
            try {
                if (((Boolean)Reflection.argumentsHaveErrors.get(methodCall)).booleanValue()) {
                    skip = true;
                }
            }
            catch (IllegalAccessException illegalAccessException) {}
        }
        if (!skip) {
            for (Extension extension : extensions) {
                if (!extension.suppressBaseMethods && !(methodCall.binding instanceof ProblemMethodBinding)) continue;
                for (MethodBinding extensionMethod : extension.extensionMethods) {
                    MethodBinding fixedBinding;
                    if (!Arrays.equals(methodCall.selector, extensionMethod.selector)) continue;
                    MessageSend_postponedErrors.clear(methodCall);
                    if (methodCall.receiver instanceof ThisReference) {
                        methodCall.receiver.bits &= 0xFFFFFFFB;
                    }
                    ArrayList<Expression> arguments = new ArrayList<Expression>();
                    arguments.add(methodCall.receiver);
                    if (methodCall.arguments != null) {
                        arguments.addAll(Arrays.asList(methodCall.arguments));
                    }
                    Expression[] originalArgs = methodCall.arguments;
                    methodCall.arguments = arguments.toArray(new Expression[0]);
                    ArrayList<TypeBinding> argumentTypes = new ArrayList<TypeBinding>();
                    for (Expression argument : arguments) {
                        TypeBinding argumentType = argument.resolvedType;
                        if (argumentType == null && PatchExtensionMethod.requiresPolyBinding(argument)) {
                            argumentType = Reflection.getPolyTypeBinding(argument);
                        }
                        if (argumentType == null) {
                            argumentType = TypeBinding.NULL;
                        }
                        argumentTypes.add(argumentType);
                    }
                    if (methodCall.receiver instanceof MessageSend && Reflection.inferenceContexts != null) {
                        try {
                            Permit.set(Reflection.inferenceContexts, methodCall.receiver, null);
                        }
                        catch (IllegalAccessException illegalAccessException) {}
                    }
                    if ((fixedBinding = scope.getMethod((TypeBinding)extensionMethod.declaringClass, methodCall.selector, argumentTypes.toArray(new TypeBinding[0]), (InvocationSite)methodCall)) instanceof ProblemMethodBinding) {
                        methodCall.arguments = originalArgs;
                        if (fixedBinding.declaringClass == null) {
                            fixedBinding = new ProblemMethodBinding(fixedBinding.selector, fixedBinding.parameters, extensionMethod.declaringClass, fixedBinding.problemId());
                        }
                        PostponedInvalidMethodError.invoke(scope.problemReporter(), methodCall, fixedBinding, (Scope)scope);
                    } else {
                        boolean isVarargs = fixedBinding.isVarargs();
                        int i = 0;
                        int iend = arguments.size();
                        while (i < iend) {
                            Expression arg = (Expression)arguments.get(i);
                            TypeBinding[] parameters = fixedBinding.parameters;
                            TypeBinding param = isVarargs && i >= parameters.length - 1 ? parameters[parameters.length - 1].leafComponentType() : parameters[i];
                            if (PatchExtensionMethod.requiresPolyBinding(arg)) {
                                arg.setExpectedType(param);
                                arg.resolveType(scope);
                            }
                            if (arg.resolvedType != null) {
                                arg.computeConversion((Scope)scope, param, arg.resolvedType);
                            }
                            ++i;
                        }
                        methodCall.receiver = PatchExtensionMethod.createNameRef((TypeBinding)extensionMethod.declaringClass, (ASTNode)methodCall);
                        methodCall.actualReceiverType = extensionMethod.declaringClass;
                        methodCall.binding = fixedBinding;
                        methodCall.resolvedType = methodCall.binding.returnType;
                        methodCall.statementEnd = methodCall.sourceEnd;
                        if (Reflection.argumentTypes != null) {
                            try {
                                Reflection.argumentTypes.set(methodCall, argumentTypes.toArray(new TypeBinding[0]));
                            }
                            catch (IllegalAccessException illegalAccessException) {}
                        }
                    }
                    return methodCall.resolvedType;
                }
            }
        }
        if ((error = MessageSend_postponedErrors.get(methodCall)) != null) {
            error.fire();
        }
        MessageSend_postponedErrors.clear(methodCall);
        return resolvedType;
    }

    public static Object modifyMethodPattern(Object original) {
        if (original != null && original instanceof MethodPattern) {
            MethodPattern methodPattern = (MethodPattern)original;
            if (methodPattern.parameterCount > 0) {
                methodPattern.varargs = true;
            }
        }
        return original;
    }

    private static boolean requiresPolyBinding(Expression argument) {
        return Reflection.isFunctionalExpression(argument) || argument instanceof ConditionalExpression && Reflection.isPolyExpression(argument);
    }

    private static NameReference createNameRef(TypeBinding typeBinding, ASTNode source) {
        long p = (long)source.sourceStart << 32 | (long)source.sourceEnd;
        char[] pkg = typeBinding.qualifiedPackageName();
        char[] basename = typeBinding.qualifiedSourceName();
        StringBuilder sb = new StringBuilder();
        if (pkg != null) {
            sb.append(pkg);
        }
        if (sb.length() > 0) {
            sb.append(".");
        }
        sb.append(basename);
        String tName = sb.toString();
        if (tName.indexOf(46) == -1) {
            return new SingleNameReference(basename, p);
        }
        String[] in = tName.split("\\.");
        char[][] sources = new char[in.length][];
        int i = 0;
        while (i < in.length) {
            sources[i] = in[i].toCharArray();
            ++i;
        }
        long[] poss = new long[in.length];
        Arrays.fill(poss, p);
        return new QualifiedNameReference((char[][])sources, poss, source.sourceStart, source.sourceEnd);
    }

    static class Extension {
        List<MethodBinding> extensionMethods;
        boolean suppressBaseMethods;

        Extension() {
        }
    }

    private static interface PostponedError {
        public void fire();
    }

    private static class PostponedInvalidMethodError
    implements PostponedError {
        private final ProblemReporter problemReporter;
        private final WeakReference<MessageSend> messageSendRef;
        private final MethodBinding method;
        private final Scope scope;
        private static final Method shortMethod = PostponedInvalidMethodError.getMethod("invalidMethod", MessageSend.class, MethodBinding.class);
        private static final Method longMethod = PostponedInvalidMethodError.getMethod("invalidMethod", MessageSend.class, MethodBinding.class, Scope.class);
        private static Throwable initProblem;

        private static Method getMethod(String name, Class<?> ... types) {
            try {
                return Permit.getMethod(ProblemReporter.class, name, types);
            }
            catch (Exception e) {
                initProblem = e;
                return null;
            }
        }

        PostponedInvalidMethodError(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method, Scope scope) {
            this.problemReporter = problemReporter;
            this.messageSendRef = new WeakReference<MessageSend>(messageSend);
            this.method = method;
            this.scope = scope;
        }

        static void invoke(ProblemReporter problemReporter, MessageSend messageSend, MethodBinding method, Scope scope) {
            if (messageSend != null) {
                try {
                    if (shortMethod != null) {
                        Permit.invoke(initProblem, shortMethod, (Object)problemReporter, messageSend, method);
                    } else if (longMethod != null) {
                        Permit.invoke(initProblem, longMethod, (Object)problemReporter, messageSend, method, scope);
                    } else {
                        Permit.reportReflectionProblem(initProblem, "method named 'invalidMethod' not found in ProblemReporter.class");
                    }
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                catch (InvocationTargetException e) {
                    Throwable t = e.getCause();
                    if (t instanceof Error) {
                        throw (Error)t;
                    }
                    if (t instanceof RuntimeException) {
                        throw (RuntimeException)t;
                    }
                    throw new RuntimeException(t);
                }
            }
        }

        @Override
        public void fire() {
            MessageSend messageSend = (MessageSend)this.messageSendRef.get();
            PostponedInvalidMethodError.invoke(this.problemReporter, messageSend, this.method, this.scope);
        }
    }

    private static class PostponedNoMethodError
    implements PostponedError {
        private final ProblemReporter problemReporter;
        private final WeakReference<MessageSend> messageSendRef;
        private final TypeBinding recType;
        private final TypeBinding[] params;

        PostponedNoMethodError(ProblemReporter problemReporter, MessageSend messageSend, TypeBinding recType, TypeBinding[] params) {
            this.problemReporter = problemReporter;
            this.messageSendRef = new WeakReference<MessageSend>(messageSend);
            this.recType = recType;
            this.params = params;
        }

        @Override
        public void fire() {
            MessageSend messageSend = (MessageSend)this.messageSendRef.get();
            if (messageSend != null) {
                this.problemReporter.errorNoMethodFor(messageSend, this.recType, this.params);
            }
        }
    }

    private static class PostponedNonStaticAccessToStaticMethodError
    implements PostponedError {
        private final ProblemReporter problemReporter;
        private ASTNode location;
        private MethodBinding method;
        private ReferenceContext referenceContext;

        PostponedNonStaticAccessToStaticMethodError(ProblemReporter problemReporter, ASTNode location, MethodBinding method) {
            this.problemReporter = problemReporter;
            this.location = location;
            this.method = method;
            this.referenceContext = problemReporter.referenceContext;
        }

        @Override
        public void fire() {
            this.problemReporter.referenceContext = this.referenceContext;
            this.problemReporter.nonStaticAccessToStaticMethod(this.location, this.method);
        }
    }

    private static final class Reflection {
        public static final Field argumentTypes = Permit.permissiveGetField(MessageSend.class, "argumentTypes");
        public static final Field argumentsHaveErrors = Permit.permissiveGetField(MessageSend.class, "argumentsHaveErrors");
        public static final Field inferenceContexts = Permit.permissiveGetField(MessageSend.class, "inferenceContexts");
        private static final Method isPolyExpression = Permit.permissiveGetMethod(Expression.class, "isPolyExpression", new Class[0]);
        private static final Class<?> functionalExpression;
        private static final Constructor<?> polyTypeBindingConstructor;

        static {
            Class<?> a = null;
            Constructor<?> b = null;
            try {
                a = Class.forName("org.eclipse.jdt.internal.compiler.ast.FunctionalExpression");
            }
            catch (Exception exception) {}
            try {
                b = Permit.getConstructor(Class.forName("org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding"), Expression.class);
            }
            catch (Exception exception) {}
            functionalExpression = a;
            polyTypeBindingConstructor = b;
        }

        private Reflection() {
        }

        public static boolean isFunctionalExpression(Expression expression) {
            if (functionalExpression == null) {
                return false;
            }
            return functionalExpression.isInstance(expression);
        }

        public static boolean isPolyExpression(Expression expression) {
            if (isPolyExpression == null) {
                return false;
            }
            try {
                return (Boolean)isPolyExpression.invoke((Object)expression, new Object[0]);
            }
            catch (Exception exception) {
                return false;
            }
        }

        public static TypeBinding getPolyTypeBinding(Expression expression) {
            if (polyTypeBindingConstructor == null) {
                return null;
            }
            try {
                return (TypeBinding)polyTypeBindingConstructor.newInstance(expression);
            }
            catch (Exception exception) {
                return null;
            }
        }
    }
}
