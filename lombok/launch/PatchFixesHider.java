package lombok.launch;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import lombok.eclipse.EcjAugments;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.launch.Main;
import lombok.permit.Permit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.internal.compiler.ISourceElementRequestor;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.core.SourceFieldElementInfo;
import org.eclipse.jdt.internal.core.dom.rewrite.NodeRewriteEvent;
import org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent;
import org.eclipse.jdt.internal.core.dom.rewrite.TokenScanner;
import org.eclipse.jdt.internal.corext.refactoring.SearchResultGroup;
import org.eclipse.jdt.internal.corext.refactoring.code.CallContext;
import org.eclipse.jdt.internal.corext.refactoring.code.SourceProvider;
import org.eclipse.jdt.internal.corext.refactoring.structure.MemberVisibilityAdjustor;
import org.osgi.framework.Bundle;

final class PatchFixesHider {
    PatchFixesHider() {
    }

    public static final class Delegate {
        private static final Method HANDLE_DELEGATE_FOR_TYPE;
        private static final Method ADD_GENERATED_DELEGATE_METHODS;
        public static final Method IS_DELEGATE_SOURCE_METHOD;
        public static final Method RETURN_ELEMENT_INFO;

        static {
            Class<?> shadowedPortal = Util.shadowLoadClass("lombok.eclipse.agent.PatchDelegatePortal");
            HANDLE_DELEGATE_FOR_TYPE = Util.findMethod(shadowedPortal, "handleDelegateForType", Object.class);
            ADD_GENERATED_DELEGATE_METHODS = Util.findMethod(shadowedPortal, "addGeneratedDelegateMethods", Object.class, Object.class);
            Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchDelegate");
            IS_DELEGATE_SOURCE_METHOD = Util.findMethod(shadowed, "isDelegateSourceMethod", Object.class);
            RETURN_ELEMENT_INFO = Util.findMethod(shadowed, "returnElementInfo", Object.class);
        }

        public static boolean handleDelegateForType(Object classScope) {
            return (Boolean)Util.invokeMethod(HANDLE_DELEGATE_FOR_TYPE, classScope);
        }

        public static Object[] addGeneratedDelegateMethods(Object returnValue, Object javaElement) {
            return (Object[])Util.invokeMethod(ADD_GENERATED_DELEGATE_METHODS, returnValue, javaElement);
        }

        public static boolean isDelegateSourceMethod(Object sourceMethod) {
            return (Boolean)Util.invokeMethod(IS_DELEGATE_SOURCE_METHOD, sourceMethod);
        }

        public static Object returnElementInfo(Object delegateSourceMethod) {
            return Util.invokeMethod(RETURN_ELEMENT_INFO, delegateSourceMethod);
        }
    }

    public static final class ExtensionMethod {
        private static final String MESSAGE_SEND_SIG = "org.eclipse.jdt.internal.compiler.ast.MessageSend";
        private static final String TYPE_BINDING_SIG = "org.eclipse.jdt.internal.compiler.lookup.TypeBinding";
        private static final String SCOPE_SIG = "org.eclipse.jdt.internal.compiler.lookup.Scope";
        private static final String BLOCK_SCOPE_SIG = "org.eclipse.jdt.internal.compiler.lookup.BlockScope";
        private static final String TYPE_BINDINGS_SIG = "[Lorg.eclipse.jdt.internal.compiler.lookup.TypeBinding;";
        private static final String PROBLEM_REPORTER_SIG = "org.eclipse.jdt.internal.compiler.problem.ProblemReporter";
        private static final String METHOD_BINDING_SIG = "org.eclipse.jdt.internal.compiler.lookup.MethodBinding";
        private static final String AST_NODE_SIG = "org.eclipse.jdt.internal.compiler.ast.ASTNode";
        private static final Method RESOLVE_TYPE;
        private static final Method ERROR_NO_METHOD_FOR;
        private static final Method INVALID_METHOD;
        private static final Method INVALID_METHOD2;
        private static final Method NON_STATIC_ACCESS_TO_STATIC_METHOD;
        private static final Method MODIFY_METHOD_PATTERN;

        static {
            Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchExtensionMethod");
            RESOLVE_TYPE = Util.findMethod(shadowed, "resolveType", TYPE_BINDING_SIG, MESSAGE_SEND_SIG, BLOCK_SCOPE_SIG);
            ERROR_NO_METHOD_FOR = Util.findMethod(shadowed, "errorNoMethodFor", PROBLEM_REPORTER_SIG, MESSAGE_SEND_SIG, TYPE_BINDING_SIG, TYPE_BINDINGS_SIG);
            INVALID_METHOD = Util.findMethod(shadowed, "invalidMethod", PROBLEM_REPORTER_SIG, MESSAGE_SEND_SIG, METHOD_BINDING_SIG);
            INVALID_METHOD2 = Util.findMethod(shadowed, "invalidMethod", PROBLEM_REPORTER_SIG, MESSAGE_SEND_SIG, METHOD_BINDING_SIG, SCOPE_SIG);
            NON_STATIC_ACCESS_TO_STATIC_METHOD = Util.findMethod(shadowed, "nonStaticAccessToStaticMethod", PROBLEM_REPORTER_SIG, AST_NODE_SIG, METHOD_BINDING_SIG, MESSAGE_SEND_SIG);
            MODIFY_METHOD_PATTERN = Util.findMethod(shadowed, "modifyMethodPattern", Object.class);
        }

        public static Object resolveType(Object resolvedType, Object methodCall, Object scope) {
            return Util.invokeMethod(RESOLVE_TYPE, resolvedType, methodCall, scope);
        }

        public static void errorNoMethodFor(Object problemReporter, Object messageSend, Object recType, Object params) {
            Util.invokeMethod(ERROR_NO_METHOD_FOR, problemReporter, messageSend, recType, params);
        }

        public static void invalidMethod(Object problemReporter, Object messageSend, Object method) {
            Util.invokeMethod(INVALID_METHOD, problemReporter, messageSend, method);
        }

        public static void invalidMethod(Object problemReporter, Object messageSend, Object method, Object scope) {
            Util.invokeMethod(INVALID_METHOD2, problemReporter, messageSend, method, scope);
        }

        public static void nonStaticAccessToStaticMethod(Object problemReporter, Object location, Object method, Object messageSend) {
            Util.invokeMethod(NON_STATIC_ACCESS_TO_STATIC_METHOD, problemReporter, location, method, messageSend);
        }

        public static Object modifyMethodPattern(Object original) {
            return Util.invokeMethod(MODIFY_METHOD_PATTERN, original);
        }
    }

    public static class FieldInitializer {
        public static final Field INFO_STACK = Permit.permissiveGetField(CompilationUnitStructureRequestor.class, "infoStack");
        public static final Field FIELD_INFO = Permit.permissiveGetField(CompilationUnitStructureRequestor.class, "$fieldInfo");
        public static final Field SOURCE_FIELD_ELEMENT_INFO = Permit.permissiveGetField(CompilationUnitStructureRequestor.class, "$sourceFieldElementInfo");
        public static final Field INITIALIZATION_SOURCE = Permit.permissiveGetField(SourceFieldElementInfo.class, "initializationSource");
        public static final Field NODE = Permit.permissiveGetField(ISourceElementRequestor.FieldInfo.class, "node");
        public static final boolean INITIALIZED = INFO_STACK != null && FIELD_INFO != null && SOURCE_FIELD_ELEMENT_INFO != null && INITIALIZATION_SOURCE != null && NODE != null;

        public static boolean storeFieldInfo(CompilationUnitStructureRequestor compilationUnitStructureRequestor) {
            try {
                if (INITIALIZED) {
                    Stack infoStack = (Stack)Permit.get(INFO_STACK, compilationUnitStructureRequestor);
                    Object fieldInfo = infoStack.peek();
                    Permit.set(FIELD_INFO, compilationUnitStructureRequestor, fieldInfo);
                }
            }
            catch (Exception exception) {}
            return false;
        }

        public static void storeSourceFieldElementInfo(SourceFieldElementInfo fieldInfo, CompilationUnitStructureRequestor compilationUnitStructureRequestor) {
            try {
                if (INITIALIZED) {
                    Permit.set(SOURCE_FIELD_ELEMENT_INFO, compilationUnitStructureRequestor, fieldInfo);
                }
            }
            catch (Exception exception) {}
        }

        public static void overwriteInitializer(CompilationUnitStructureRequestor compilationUnitStructureRequestor) {
            try {
                if (INITIALIZED) {
                    AbstractVariableDeclaration node;
                    ISourceElementRequestor.FieldInfo fieldInfo = (ISourceElementRequestor.FieldInfo)Permit.get(FIELD_INFO, compilationUnitStructureRequestor);
                    Permit.set(FIELD_INFO, compilationUnitStructureRequestor, null);
                    SourceFieldElementInfo sourceFieldElementInfo = (SourceFieldElementInfo)Permit.get(SOURCE_FIELD_ELEMENT_INFO, compilationUnitStructureRequestor);
                    Permit.set(SOURCE_FIELD_ELEMENT_INFO, compilationUnitStructureRequestor, null);
                    if (sourceFieldElementInfo.getInitializationSource() != null && PatchFixes.isGenerated((org.eclipse.jdt.internal.compiler.ast.ASTNode)(node = (AbstractVariableDeclaration)Permit.get(NODE, fieldInfo)))) {
                        Permit.set(INITIALIZATION_SOURCE, sourceFieldElementInfo, node.initialization.toString().toCharArray());
                    }
                }
            }
            catch (Exception exception) {}
        }
    }

    public static final class Javadoc {
        private static final Method GET_HTML;

        static {
            Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchJavadoc");
            GET_HTML = Util.findMethod(shadowed, "getHTMLContentFromSource", Object.class, String.class, Object.class);
        }

        public static String getHTMLContentFromSource(String original, IJavaElement member) {
            return (String)Util.invokeMethod(GET_HTML, null, original, member);
        }

        public static String getHTMLContentFromSource(String original, Object instance, IJavaElement member) {
            return (String)Util.invokeMethod(GET_HTML, instance, original, member);
        }
    }

    public static final class LombokDeps {
        public static final Method ADD_LOMBOK_NOTES;
        public static final Method POST_COMPILER_BYTES_STRING;
        public static final Method POST_COMPILER_OUTPUTSTREAM;
        public static final Method POST_COMPILER_BUFFEREDOUTPUTSTREAM_STRING_STRING;

        static {
            Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchFixesShadowLoaded");
            ADD_LOMBOK_NOTES = Util.findMethod(shadowed, "addLombokNotesToEclipseAboutDialog", String.class, String.class);
            POST_COMPILER_BYTES_STRING = Util.findMethod(shadowed, "runPostCompiler", byte[].class, String.class);
            POST_COMPILER_OUTPUTSTREAM = Util.findMethod(shadowed, "runPostCompiler", OutputStream.class);
            POST_COMPILER_BUFFEREDOUTPUTSTREAM_STRING_STRING = Util.findMethod(shadowed, "runPostCompiler", BufferedOutputStream.class, String.class, String.class);
        }

        public static String addLombokNotesToEclipseAboutDialog(String origReturnValue, String key) {
            try {
                return (String)Util.invokeMethod(ADD_LOMBOK_NOTES, origReturnValue, key);
            }
            catch (Throwable throwable) {
                return origReturnValue;
            }
        }

        public static byte[] runPostCompiler(byte[] bytes, String fileName) {
            return (byte[])Util.invokeMethod(POST_COMPILER_BYTES_STRING, bytes, fileName);
        }

        public static OutputStream runPostCompiler(OutputStream out) throws IOException {
            return (OutputStream)Util.invokeMethod(POST_COMPILER_OUTPUTSTREAM, out);
        }

        public static BufferedOutputStream runPostCompiler(BufferedOutputStream out, String path, String name) throws IOException {
            return (BufferedOutputStream)Util.invokeMethod(POST_COMPILER_BUFFEREDOUTPUTSTREAM_STRING_STRING, out, path, name);
        }
    }

    public static final class ModuleClassLoading {
        public static void parserClinit() {
            ClassLoader jdtCoreClassLoader = Util.findJdtCoreClassLoader(Parser.class.getClassLoader());
            ClassLoader currentClassLoader = ModuleClassLoading.class.getClassLoader();
            Util.prependToClassLoader(currentClassLoader, jdtCoreClassLoader);
        }
    }

    public static final class PatchFixes {
        public static final int ALREADY_PROCESSED_FLAG = 0x800000;

        public static boolean isGenerated(ASTNode node) {
            boolean result = false;
            try {
                result = (Boolean)node.getClass().getField("$isGenerated").get(node);
                if (!result && node.getParent() != null && node.getParent() instanceof QualifiedName) {
                    result = PatchFixes.isGenerated(node.getParent());
                }
            }
            catch (Exception exception) {}
            return result;
        }

        public static boolean isGenerated(org.eclipse.jdt.internal.compiler.ast.ASTNode node) {
            boolean result = false;
            try {
                result = node.getClass().getField("$generatedBy").get(node) != null;
            }
            catch (Exception exception) {}
            return result;
        }

        public static boolean isGenerated(IMember member) {
            boolean result = false;
            try {
                result = member.getNameRange().getLength() <= 0 || member.getNameRange().equals(member.getSourceRange());
            }
            catch (JavaModelException javaModelException) {}
            return result;
        }

        public static boolean isBlockedVisitorAndGenerated(ASTNode node, ASTVisitor visitor) {
            if (visitor == null) {
                return false;
            }
            String className = visitor.getClass().getName();
            if (!(className.startsWith("org.eclipse.jdt.internal.corext.fix") || className.startsWith("org.eclipse.jdt.internal.ui.fix") || className.startsWith("org.eclipse.jdt.ls.core.internal.semantictokens.SemanticTokensVisitor"))) {
                return false;
            }
            if (className.equals("org.eclipse.jdt.internal.corext.fix.VariableDeclarationFixCore$WrittenNamesFinder")) {
                return false;
            }
            return PatchFixes.isGenerated(node);
        }

        public static boolean isListRewriteOnGeneratedNode(ListRewrite rewrite) {
            return PatchFixes.isGenerated(rewrite.getParent());
        }

        public static boolean returnFalse(Object object) {
            return false;
        }

        public static boolean returnTrue(Object object) {
            return true;
        }

        public static List removeGeneratedNodes(List list) {
            try {
                ArrayList realNodes = new ArrayList(list.size());
                for (Object node : list) {
                    if (PatchFixes.isGenerated((ASTNode)node)) continue;
                    realNodes.add(node);
                }
                return realNodes;
            }
            catch (Exception exception) {
                return list;
            }
        }

        public static String getRealMethodDeclarationSource(String original, Object processor, MethodDeclaration declaration) throws Exception {
            if (!PatchFixes.isGenerated((ASTNode)declaration)) {
                return original;
            }
            ArrayList<org.eclipse.jdt.core.dom.Annotation> annotations = new ArrayList<org.eclipse.jdt.core.dom.Annotation>();
            for (Object modifier : declaration.modifiers()) {
                org.eclipse.jdt.core.dom.Annotation annotation;
                String qualifiedAnnotationName;
                if (!(modifier instanceof org.eclipse.jdt.core.dom.Annotation) || "java.lang.Override".equals(qualifiedAnnotationName = (annotation = (org.eclipse.jdt.core.dom.Annotation)modifier).resolveTypeBinding().getQualifiedName()) || "java.lang.SuppressWarnings".equals(qualifiedAnnotationName)) continue;
                annotations.add(annotation);
            }
            StringBuilder signature = new StringBuilder();
            PatchFixes.addAnnotations(annotations, signature);
            try {
                if (((Boolean)processor.getClass().getDeclaredField("fPublic").get(processor)).booleanValue()) {
                    signature.append("public ");
                }
                if (((Boolean)processor.getClass().getDeclaredField("fAbstract").get(processor)).booleanValue()) {
                    signature.append("abstract ");
                }
            }
            catch (Throwable throwable) {}
            signature.append(declaration.getReturnType2().toString()).append(" ").append(declaration.getName().getFullyQualifiedName()).append("(");
            boolean first = true;
            for (Object parameter : declaration.parameters()) {
                if (!first) {
                    signature.append(", ");
                }
                first = false;
                signature.append(parameter);
            }
            signature.append(");");
            return signature.toString();
        }

        public static void addAnnotations(List<org.eclipse.jdt.core.dom.Annotation> annotations, StringBuilder signature) {
            for (org.eclipse.jdt.core.dom.Annotation annotation : annotations) {
                ArrayList<String> values = new ArrayList<String>();
                if (annotation.isSingleMemberAnnotation()) {
                    SingleMemberAnnotation smAnn = (SingleMemberAnnotation)annotation;
                    values.add(smAnn.getValue().toString());
                } else if (annotation.isNormalAnnotation()) {
                    NormalAnnotation normalAnn = (NormalAnnotation)annotation;
                    for (Object value : normalAnn.values()) {
                        values.add(value.toString());
                    }
                }
                signature.append("@").append(annotation.getTypeName().getFullyQualifiedName());
                if (!values.isEmpty()) {
                    signature.append("(");
                    boolean first = true;
                    for (String string : values) {
                        if (!first) {
                            signature.append(", ");
                        }
                        first = false;
                        signature.append('\"').append(string).append('\"');
                    }
                    signature.append(")");
                }
                signature.append(" ");
            }
        }

        public static MethodDeclaration getRealMethodDeclarationNode(MethodDeclaration original, IMethod sourceMethod, CompilationUnit cuUnit) throws JavaModelException {
            if (!PatchFixes.isGenerated((ASTNode)original)) {
                return original;
            }
            IType declaringType = sourceMethod.getDeclaringType();
            Stack<IType> typeStack = new Stack<IType>();
            while (declaringType != null) {
                typeStack.push(declaringType);
                declaringType = declaringType.getDeclaringType();
            }
            IType rootType = (IType)typeStack.pop();
            AbstractTypeDeclaration typeDeclaration = PatchFixes.findTypeDeclaration(rootType, cuUnit.types());
            while (!typeStack.isEmpty() && typeDeclaration != null) {
                typeDeclaration = PatchFixes.findTypeDeclaration((IType)typeStack.pop(), typeDeclaration.bodyDeclarations());
            }
            String targetMethodName = sourceMethod.getElementName();
            ArrayList<String> targetMethodParameterTypes = new ArrayList<String>();
            String[] stringArray = sourceMethod.getParameterTypes();
            int n = stringArray.length;
            int n2 = 0;
            while (n2 < n) {
                String parameterType = stringArray[n2];
                targetMethodParameterTypes.add(Signature.toString((String)parameterType));
                ++n2;
            }
            if (typeStack.isEmpty() && typeDeclaration != null) {
                for (Object declaration : typeDeclaration.bodyDeclarations()) {
                    MethodDeclaration methodDeclaration;
                    if (!(declaration instanceof MethodDeclaration) || !(methodDeclaration = (MethodDeclaration)declaration).getName().toString().equals(targetMethodName) || methodDeclaration.parameters().size() != targetMethodParameterTypes.size() || !PatchFixes.isGenerated((ASTNode)methodDeclaration)) continue;
                    boolean parameterTypesEquals = true;
                    int i = 0;
                    while (i < methodDeclaration.parameters().size()) {
                        SingleVariableDeclaration variableDeclaration = (SingleVariableDeclaration)methodDeclaration.parameters().get(i);
                        if (!variableDeclaration.getType().toString().equals(targetMethodParameterTypes.get(i))) {
                            parameterTypesEquals = false;
                            break;
                        }
                        ++i;
                    }
                    if (!parameterTypesEquals) continue;
                    return methodDeclaration;
                }
            }
            return original;
        }

        public static AbstractTypeDeclaration findTypeDeclaration(IType searchType, List<?> nodes) {
            for (Object object : nodes) {
                AbstractTypeDeclaration typeDeclaration;
                if (!(object instanceof AbstractTypeDeclaration) || !(typeDeclaration = (AbstractTypeDeclaration)object).getName().toString().equals(searchType.getElementName())) continue;
                return typeDeclaration;
            }
            return null;
        }

        public static int getSourceEndFixed(int sourceEnd, org.eclipse.jdt.internal.compiler.ast.ASTNode node) throws Exception {
            org.eclipse.jdt.internal.compiler.ast.ASTNode object;
            if (sourceEnd == -1 && (object = (org.eclipse.jdt.internal.compiler.ast.ASTNode)node.getClass().getField("$generatedBy").get(node)) != null) {
                return object.sourceEnd;
            }
            return sourceEnd;
        }

        public static int fixRetrieveStartingCatchPosition(int original, int start) {
            return original == -1 ? start : original;
        }

        public static int fixRetrieveIdentifierEndPosition(int original, int start, int end) {
            if (original == -1) {
                return end;
            }
            if (original < start) {
                return end;
            }
            return original;
        }

        public static int fixRetrieveEllipsisStartPosition(int original, int end) {
            return original == -1 ? end : original;
        }

        public static int fixRetrieveStartBlockPosition(int original, int start) {
            return original == -1 ? start : original;
        }

        public static int fixRetrieveRightBraceOrSemiColonPosition(int original, int end) {
            return original == -1 ? end : original;
        }

        public static int fixRetrieveRightBraceOrSemiColonPosition(int retVal, AbstractMethodDeclaration amd) {
            boolean isGenerated;
            if (retVal != -1 || amd == null) {
                return retVal;
            }
            boolean bl = isGenerated = EcjAugments.ASTNode_generatedBy.get((org.eclipse.jdt.internal.compiler.ast.ASTNode)amd) != null;
            if (isGenerated) {
                return amd.declarationSourceEnd;
            }
            return -1;
        }

        public static int fixRetrieveRightBraceOrSemiColonPosition(int retVal, FieldDeclaration fd) {
            boolean isGenerated;
            if (retVal != -1 || fd == null) {
                return retVal;
            }
            boolean bl = isGenerated = EcjAugments.ASTNode_generatedBy.get((org.eclipse.jdt.internal.compiler.ast.ASTNode)fd) != null;
            if (isGenerated) {
                return fd.declarationSourceEnd;
            }
            return -1;
        }

        public static int fixRetrieveProperRightBracketPosition(int retVal, Type type) {
            if (retVal != -1 || type == null) {
                return retVal;
            }
            if (PatchFixes.isGenerated((ASTNode)type)) {
                return type.getStartPosition() + type.getLength() - 1;
            }
            return -1;
        }

        public static boolean checkBit24(Object node) throws Exception {
            int bits = (Integer)node.getClass().getField("bits").get(node);
            return (bits & 0x800000) != 0;
        }

        public static boolean skipRewritingGeneratedNodes(ASTNode node) throws Exception {
            return (Boolean)node.getClass().getField("$isGenerated").get(node);
        }

        public static void setIsGeneratedFlag(ASTNode domNode, org.eclipse.jdt.internal.compiler.ast.ASTNode internalNode) throws Exception {
            boolean isGenerated;
            if (internalNode == null || domNode == null) {
                return;
            }
            boolean bl = isGenerated = EcjAugments.ASTNode_generatedBy.get(internalNode) != null;
            if (isGenerated) {
                domNode.getClass().getField("$isGenerated").set(domNode, true);
            }
        }

        public static void setIsGeneratedFlagForName(Name name, Object internalNode) throws Exception {
            if (internalNode instanceof org.eclipse.jdt.internal.compiler.ast.ASTNode) {
                boolean isGenerated;
                boolean bl = isGenerated = EcjAugments.ASTNode_generatedBy.get((org.eclipse.jdt.internal.compiler.ast.ASTNode)internalNode) != null;
                if (isGenerated) {
                    name.getClass().getField("$isGenerated").set(name, true);
                }
            }
        }

        public static RewriteEvent[] listRewriteHandleGeneratedMethods(RewriteEvent parent) {
            RewriteEvent[] children = parent.getChildren();
            ArrayList<Object> newChildren = new ArrayList<Object>();
            ArrayList<NodeRewriteEvent> modifiedChildren = new ArrayList<NodeRewriteEvent>();
            int i = 0;
            while (i < children.length) {
                RewriteEvent child = children[i];
                boolean isGenerated = PatchFixes.isGenerated((ASTNode)child.getOriginalValue());
                if (isGenerated) {
                    boolean isReplacedOrRemoved = child.getChangeKind() == 4 || child.getChangeKind() == 2;
                    boolean convertingFromMethod = child.getOriginalValue() instanceof MethodDeclaration;
                    if (isReplacedOrRemoved && convertingFromMethod && child.getNewValue() != null) {
                        modifiedChildren.add(new NodeRewriteEvent(null, child.getNewValue()));
                    }
                } else {
                    newChildren.add(child);
                }
                ++i;
            }
            newChildren.addAll(modifiedChildren);
            return newChildren.toArray(new RewriteEvent[0]);
        }

        public static int getTokenEndOffsetFixed(TokenScanner scanner, int token, int startOffset, Object domNode) throws CoreException {
            boolean isGenerated = false;
            try {
                isGenerated = (Boolean)domNode.getClass().getField("$isGenerated").get(domNode);
            }
            catch (Exception exception) {}
            if (isGenerated) {
                return -1;
            }
            return scanner.getTokenEndOffset(token, startOffset);
        }

        public static IMethod[] removeGeneratedMethods(IMethod[] methods) throws Exception {
            ArrayList<IMethod> result = new ArrayList<IMethod>();
            IMethod[] iMethodArray = methods;
            int n = methods.length;
            int n2 = 0;
            while (n2 < n) {
                IMethod m = iMethodArray[n2];
                if (!PatchFixes.isGenerated((IMember)m)) {
                    result.add(m);
                }
                ++n2;
            }
            return result.size() == methods.length ? methods : result.toArray(new IMethod[0]);
        }

        public static SearchResultGroup[] createFakeSearchResult(SearchResultGroup[] returnValue, Object processor) throws Exception {
            Field declaredField;
            if ((returnValue == null || returnValue.length == 0) && (declaredField = processor.getClass().getDeclaredField("fField")) != null) {
                declaredField.setAccessible(true);
                SourceField fField = (SourceField)declaredField.get(processor);
                IAnnotation dataAnnotation = fField.getDeclaringType().getAnnotation("Data");
                if (dataAnnotation != null) {
                    return new SearchResultGroup[]{new SearchResultGroup(null, new SearchMatch[1])};
                }
            }
            return returnValue;
        }

        public static SimpleName[] removeGeneratedSimpleNames(SimpleName[] in) throws Exception {
            Field f = SimpleName.class.getField("$isGenerated");
            int count = 0;
            int i = 0;
            while (i < in.length) {
                if (in[i] == null || !((Boolean)f.get(in[i])).booleanValue()) {
                    ++count;
                }
                ++i;
            }
            if (count == in.length) {
                return in;
            }
            SimpleName[] newSimpleNames = new SimpleName[count];
            count = 0;
            int i2 = 0;
            while (i2 < in.length) {
                if (in[i2] == null || !((Boolean)f.get(in[i2])).booleanValue()) {
                    newSimpleNames[count++] = in[i2];
                }
                ++i2;
            }
            return newSimpleNames;
        }

        public static Name[] removeGeneratedNames(Name[] in) throws Exception {
            Field f = Name.class.getField("$isGenerated");
            int count = 0;
            int i = 0;
            while (i < in.length) {
                if (in[i] == null || !((Boolean)f.get(in[i])).booleanValue()) {
                    ++count;
                }
                ++i;
            }
            if (count == in.length) {
                return in;
            }
            Name[] newNames = new Name[count];
            count = 0;
            int i2 = 0;
            while (i2 < in.length) {
                if (in[i2] == null || !((Boolean)f.get(in[i2])).booleanValue()) {
                    newNames[count++] = in[i2];
                }
                ++i2;
            }
            return newNames;
        }

        public static Annotation[] convertAnnotations(Annotation[] out, IAnnotatable annotatable) {
            IAnnotation[] in;
            try {
                in = annotatable.getAnnotations();
            }
            catch (Exception exception) {
                return out;
            }
            if (out == null) {
                return null;
            }
            int toWrite = 0;
            int idx = 0;
            while (idx < out.length) {
                String oName = new String(out[idx].type.getLastToken());
                boolean found = false;
                IAnnotation[] iAnnotationArray = in;
                int n = in.length;
                int n2 = 0;
                while (n2 < n) {
                    IAnnotation i = iAnnotationArray[n2];
                    String name = i.getElementName();
                    int li = name.lastIndexOf(46);
                    if (li > -1) {
                        name = name.substring(li + 1);
                    }
                    if (name.equals(oName)) {
                        found = true;
                        break;
                    }
                    ++n2;
                }
                if (!found) {
                    out[idx] = null;
                } else {
                    ++toWrite;
                }
                ++idx;
            }
            Annotation[] replace = out;
            if (toWrite < out.length) {
                replace = new Annotation[toWrite];
                int idx2 = 0;
                int i = 0;
                while (i < out.length) {
                    if (out[i] != null) {
                        replace[idx2++] = out[i];
                    }
                    ++i;
                }
            }
            return replace;
        }

        public static String getRealNodeSource(String original, org.eclipse.jdt.internal.compiler.ast.ASTNode node) {
            if (!PatchFixes.isGenerated(node)) {
                return original;
            }
            return node.toString();
        }

        public static String getRealNodeSource(String original, ASTNode node) throws Exception {
            if (!PatchFixes.isGenerated(node)) {
                return original;
            }
            return node.toString();
        }

        public static boolean skipRewriteVisibility(MemberVisibilityAdjustor.IncomingMemberVisibilityAdjustment adjustment) {
            return PatchFixes.isGenerated(adjustment.getMember());
        }

        public static String[] getRealCodeBlocks(String[] blocks, SourceProvider sourceProvider, CallContext callContext) {
            MethodDeclaration methodDeclaration = sourceProvider.getDeclaration();
            if (!PatchFixes.isGenerated((ASTNode)methodDeclaration)) {
                return blocks;
            }
            try {
                AST ast = methodDeclaration.getAST();
                List parameters = methodDeclaration.parameters();
                int i = 0;
                while (i < parameters.size()) {
                    SingleVariableDeclaration param = (SingleVariableDeclaration)parameters.get(i);
                    Object data = param.getProperty("org.eclipse.jdt.internal.corext.refactoring.code.ParameterData");
                    List names = (List)Permit.get(Permit.permissiveGetField(data.getClass(), "fReferences"), data);
                    for (SimpleName simpleName : names) {
                        ASTNode copy = ASTNode.copySubtree((AST)ast, (ASTNode)callContext.arguments[i]);
                        simpleName.getParent().setStructuralProperty(simpleName.getLocationInParent(), (Object)copy);
                    }
                    ++i;
                }
                StringBuilder sb = new StringBuilder();
                for (Object statement : methodDeclaration.getBody().statements()) {
                    if (callContext.callMode != 41 && statement instanceof ReturnStatement) {
                        ReturnStatement returnStatement = (ReturnStatement)statement;
                        sb.append(returnStatement.getExpression());
                        continue;
                    }
                    sb.append(statement);
                }
                return new String[]{sb.toString().trim()};
            }
            catch (Throwable throwable) {
                return blocks;
            }
        }
    }

    public static class Tests {
        public static StringBuffer printMethod(AbstractMethodDeclaration methodDeclaration, int tab, StringBuffer output, TypeDeclaration type) {
            return (StringBuffer)Tests.printMethod(methodDeclaration, tab, (Object)output, type);
        }

        public static StringBuilder printMethod(AbstractMethodDeclaration methodDeclaration, int tab, StringBuilder output, TypeDeclaration type) {
            return (StringBuilder)Tests.printMethod(methodDeclaration, tab, (Object)output, type);
        }

        public static Object printMethod(AbstractMethodDeclaration methodDeclaration, int tab, Object output, TypeDeclaration type) {
            String signature;
            String rawJavadoc;
            Map<String, String> docs = EcjAugments.CompilationUnit_javadoc.get(methodDeclaration.compilationResult.compilationUnit);
            Method printIndent = Permit.permissiveGetMethod(org.eclipse.jdt.internal.compiler.ast.ASTNode.class, "printIndent", Integer.TYPE, output.getClass());
            if (docs != null && (rawJavadoc = docs.get(signature = EclipseHandlerUtil.getSignature(type, methodDeclaration))) != null) {
                String[] stringArray = rawJavadoc.split("\r?\n");
                int n = stringArray.length;
                int n2 = 0;
                while (n2 < n) {
                    String line = stringArray[n2];
                    try {
                        Appendable sb = (Appendable)Permit.invoke(printIndent, null, tab, output);
                        sb.append(line).append("\n");
                    }
                    catch (Throwable throwable) {}
                    ++n2;
                }
            }
            Method printMethodDeclaration = Permit.permissiveGetMethod(AbstractMethodDeclaration.class, "print", Integer.TYPE, output.getClass());
            Permit.invokeSneaky(printMethodDeclaration, methodDeclaration, tab, output);
            return output;
        }

        public static Object getBundle(Object original, Class<?> c) {
            Bundle[] bundles;
            if (original != null) {
                return original;
            }
            CodeSource codeSource = c.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                return null;
            }
            String jar = codeSource.getLocation().getFile();
            String bundleName = jar.substring(jar.lastIndexOf("/") + 1, jar.indexOf("_"));
            Bundle[] bundleArray = bundles = EclipseStarter.getSystemBundleContext().getBundles();
            int n = bundles.length;
            int n2 = 0;
            while (n2 < n) {
                Bundle bundle = bundleArray[n2];
                if (bundleName.equals(bundle.getSymbolicName())) {
                    return bundle;
                }
                ++n2;
            }
            return null;
        }

        public static boolean isImplicitCanonicalConstructor(AbstractMethodDeclaration method, Object parameter) {
            return (method.bits & 0x200) != 0 && (method.bits & 0x400) != 0;
        }

        public static StringBuffer returnStringBuffer(Object p1, StringBuffer buffer) {
            return buffer;
        }

        public static StringBuilder returnStringBuilder(Object p1, StringBuilder buffer) {
            return buffer;
        }
    }

    public static final class Transform {
        private static Method TRANSFORM;
        private static Method TRANSFORM_SWAPPED;

        private static synchronized void init(ClassLoader prepend) {
            if (TRANSFORM != null) {
                return;
            }
            Main.prependClassLoader(prepend);
            ClassLoader currentClassLoader = Transform.class.getClassLoader();
            Util.prependToClassLoader(currentClassLoader, prepend);
            Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.TransformEclipseAST");
            TRANSFORM = Util.findMethodAnyArgs(shadowed, "transform");
            TRANSFORM_SWAPPED = Util.findMethodAnyArgs(shadowed, "transform_swapped");
        }

        public static void transform(Object parser, Object ast) throws IOException {
            Transform.init(parser.getClass().getClassLoader());
            Util.invokeMethod(TRANSFORM, parser, ast);
        }

        public static void transform_swapped(Object ast, Object parser) throws IOException {
            Transform.init(parser.getClass().getClassLoader());
            Util.invokeMethod(TRANSFORM_SWAPPED, ast, parser);
        }
    }

    public static final class Util {
        private static ClassLoader shadowLoader;

        public static ClassLoader getShadowLoader() {
            if (shadowLoader == null) {
                try {
                    Class.forName("lombok.core.LombokNode");
                    shadowLoader = Util.class.getClassLoader();
                }
                catch (ClassNotFoundException classNotFoundException) {
                    shadowLoader = Main.getShadowClassLoader();
                }
            }
            return shadowLoader;
        }

        public static Class<?> shadowLoadClass(String name) {
            try {
                return Class.forName(name, true, Util.getShadowLoader());
            }
            catch (ClassNotFoundException e) {
                throw Util.sneakyThrow(e);
            }
        }

        public static Method findMethod(Class<?> type, String name, Class<?> ... parameterTypes) {
            try {
                return type.getDeclaredMethod(name, parameterTypes);
            }
            catch (NoSuchMethodException e) {
                throw Util.sneakyThrow(e);
            }
        }

        public static Method findMethod(Class<?> type, String name, String ... parameterTypes) {
            Method[] methodArray = type.getDeclaredMethods();
            int n = methodArray.length;
            int n2 = 0;
            while (n2 < n) {
                Method m = methodArray[n2];
                if (name.equals(m.getName()) && Util.sameTypes(m.getParameterTypes(), parameterTypes)) {
                    return m;
                }
                ++n2;
            }
            throw Util.sneakyThrow(new NoSuchMethodException(String.valueOf(type.getName()) + "::" + name));
        }

        public static Method findMethodAnyArgs(Class<?> type, String name) {
            Method[] methodArray = type.getDeclaredMethods();
            int n = methodArray.length;
            int n2 = 0;
            while (n2 < n) {
                Method m = methodArray[n2];
                if (name.equals(m.getName())) {
                    return m;
                }
                ++n2;
            }
            throw Util.sneakyThrow(new NoSuchMethodException(String.valueOf(type.getName()) + "::" + name));
        }

        public static Object invokeMethod(Method method, Object ... args) {
            try {
                return method.invoke(null, args);
            }
            catch (IllegalAccessException e) {
                throw Util.sneakyThrow(e);
            }
            catch (InvocationTargetException e) {
                throw Util.sneakyThrow(e.getCause());
            }
        }

        private static RuntimeException sneakyThrow(Throwable t) {
            if (t == null) {
                throw new NullPointerException("t");
            }
            Util.sneakyThrow0(t);
            return null;
        }

        private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
            throw t;
        }

        private static boolean sameTypes(Class<?>[] types, String[] typeNames) {
            if (types.length != typeNames.length) {
                return false;
            }
            int i = 0;
            while (i < types.length) {
                if (!types[i].getName().equals(typeNames[i])) {
                    return false;
                }
                ++i;
            }
            return true;
        }

        private static void prependToClassLoader(ClassLoader currentClassLoader, ClassLoader prepend) {
            try {
                Method prependParentMethod = Permit.getMethod(currentClassLoader.getClass(), "prependParent", ClassLoader.class);
                Permit.invoke(prependParentMethod, currentClassLoader, prepend);
            }
            catch (Throwable throwable) {}
        }

        private static ClassLoader findJdtCoreClassLoader(ClassLoader classLoader) {
            try {
                Object[] bundles;
                Method getBundleMethod = Permit.getMethod(classLoader.getClass(), "getBundle", new Class[0]);
                Object bundle = Permit.invoke(getBundleMethod, classLoader, new Object[0]);
                Method getBundleContextMethod = Permit.getMethod(bundle.getClass(), "getBundleContext", new Class[0]);
                Object bundleContext = Permit.invoke(getBundleContextMethod, bundle, new Object[0]);
                Method getBundlesMethod = Permit.getMethod(bundleContext.getClass(), "getBundles", new Class[0]);
                Object[] objectArray = bundles = (Object[])Permit.invoke(getBundlesMethod, bundleContext, new Object[0]);
                int n = bundles.length;
                int n2 = 0;
                while (n2 < n) {
                    Object searchBundle = objectArray[n2];
                    if (searchBundle.toString().startsWith("org.eclipse.jdt.core_")) {
                        Method getModuleClassLoaderMethod = Permit.getMethod(searchBundle.getClass(), "getModuleClassLoader", Boolean.TYPE);
                        return (ClassLoader)Permit.invoke(getModuleClassLoaderMethod, searchBundle, false);
                    }
                    ++n2;
                }
            }
            catch (Throwable throwable) {}
            return null;
        }
    }

    public static final class Val {
        private static final String BLOCK_SCOPE_SIG = "org.eclipse.jdt.internal.compiler.lookup.BlockScope";
        private static final String LOCAL_DECLARATION_SIG = "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration";
        private static final String FOREACH_STATEMENT_SIG = "org.eclipse.jdt.internal.compiler.ast.ForeachStatement";
        private static final Method HANDLE_VAL_FOR_LOCAL_DECLARATION;
        private static final Method HANDLE_VAL_FOR_FOR_EACH;

        static {
            Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchVal");
            HANDLE_VAL_FOR_LOCAL_DECLARATION = Util.findMethod(shadowed, "handleValForLocalDeclaration", LOCAL_DECLARATION_SIG, BLOCK_SCOPE_SIG);
            HANDLE_VAL_FOR_FOR_EACH = Util.findMethod(shadowed, "handleValForForEach", FOREACH_STATEMENT_SIG, BLOCK_SCOPE_SIG);
        }

        public static boolean handleValForLocalDeclaration(Object local, Object scope) {
            return (Boolean)Util.invokeMethod(HANDLE_VAL_FOR_LOCAL_DECLARATION, local, scope);
        }

        public static boolean handleValForForEach(Object forEach, Object scope) {
            return (Boolean)Util.invokeMethod(HANDLE_VAL_FOR_FOR_EACH, forEach, scope);
        }

        public static TypeBinding skipResolveInitializerIfAlreadyCalled(Expression expr, BlockScope scope) {
            if (expr.resolvedType != null) {
                return expr.resolvedType;
            }
            try {
                return expr.resolveType(scope);
            }
            catch (NullPointerException nullPointerException) {
                return null;
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                return null;
            }
        }

        public static TypeBinding skipResolveInitializerIfAlreadyCalled2(Expression expr, BlockScope scope, LocalDeclaration decl) {
            if (decl != null && LocalDeclaration.class.equals(decl.getClass()) && expr.resolvedType != null) {
                return expr.resolvedType;
            }
            try {
                return expr.resolveType(scope);
            }
            catch (NullPointerException nullPointerException) {
                return null;
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                return null;
            }
        }
    }

    public static final class ValPortal {
        private static final Method COPY_INITIALIZATION_OF_FOR_EACH_ITERABLE;
        private static final Method COPY_INITIALIZATION_OF_LOCAL_DECLARATION;
        private static final Method ADD_FINAL_AND_VAL_ANNOTATION_TO_VARIABLE_DECLARATION_STATEMENT;
        private static final Method ADD_FINAL_AND_VAL_ANNOTATION_TO_SINGLE_VARIABLE_DECLARATION;

        static {
            Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchValEclipsePortal");
            COPY_INITIALIZATION_OF_FOR_EACH_ITERABLE = Util.findMethod(shadowed, "copyInitializationOfForEachIterable", Object.class);
            COPY_INITIALIZATION_OF_LOCAL_DECLARATION = Util.findMethod(shadowed, "copyInitializationOfLocalDeclaration", Object.class);
            ADD_FINAL_AND_VAL_ANNOTATION_TO_VARIABLE_DECLARATION_STATEMENT = Util.findMethod(shadowed, "addFinalAndValAnnotationToVariableDeclarationStatement", Object.class, Object.class, Object.class);
            ADD_FINAL_AND_VAL_ANNOTATION_TO_SINGLE_VARIABLE_DECLARATION = Util.findMethod(shadowed, "addFinalAndValAnnotationToSingleVariableDeclaration", Object.class, Object.class, Object.class);
        }

        public static void copyInitializationOfForEachIterable(Object parser) {
            Util.invokeMethod(COPY_INITIALIZATION_OF_FOR_EACH_ITERABLE, parser);
        }

        public static void copyInitializationOfLocalDeclaration(Object parser) {
            Util.invokeMethod(COPY_INITIALIZATION_OF_LOCAL_DECLARATION, parser);
        }

        public static void addFinalAndValAnnotationToVariableDeclarationStatement(Object converter, Object out, Object in) {
            Util.invokeMethod(ADD_FINAL_AND_VAL_ANNOTATION_TO_VARIABLE_DECLARATION_STATEMENT, converter, out, in);
        }

        public static void addFinalAndValAnnotationToSingleVariableDeclaration(Object converter, Object out, Object in) {
            Util.invokeMethod(ADD_FINAL_AND_VAL_ANNOTATION_TO_SINGLE_VARIABLE_DECLARATION, converter, out, in);
        }
    }
}
