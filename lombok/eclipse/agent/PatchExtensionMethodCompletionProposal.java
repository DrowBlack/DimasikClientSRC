package lombok.eclipse.agent;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.agent.ExtensionMethodCompletionProposal;
import lombok.eclipse.agent.PatchExtensionMethod;
import lombok.experimental.ExtensionMethod;
import lombok.permit.Permit;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.codeassist.InternalCompletionProposal;
import org.eclipse.jdt.internal.codeassist.InternalExtendedCompletionContext;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberAccess;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedNameReference;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.core.SearchableEnvironment;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;

public class PatchExtensionMethodCompletionProposal {
    public static IJavaCompletionProposal[] getJavaCompletionProposals(IJavaCompletionProposal[] javaCompletionProposals, CompletionProposalCollector completionProposalCollector) {
        ArrayList<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>(Arrays.asList(javaCompletionProposals));
        if (PatchExtensionMethodCompletionProposal.canExtendCodeAssist()) {
            for (PatchExtensionMethod.Extension extension : PatchExtensionMethodCompletionProposal.getExtensionMethods(completionProposalCollector)) {
                for (MethodBinding method : extension.extensionMethods) {
                    if (!PatchExtensionMethodCompletionProposal.isMatchingProposal(method, completionProposalCollector)) continue;
                    ExtensionMethodCompletionProposal newProposal = new ExtensionMethodCompletionProposal(0);
                    PatchExtensionMethodCompletionProposal.copyNameLookupAndCompletionEngine(completionProposalCollector, newProposal);
                    ASTNode node = PatchExtensionMethodCompletionProposal.getAssistNode(completionProposalCollector);
                    newProposal.setMethodBinding(method, node);
                    PatchExtensionMethodCompletionProposal.createAndAddJavaCompletionProposal(completionProposalCollector, (CompletionProposal)newProposal, proposals);
                }
            }
        }
        return proposals.toArray(new IJavaCompletionProposal[0]);
    }

    private static List<PatchExtensionMethod.Extension> getExtensionMethods(CompletionProposalCollector completionProposalCollector) {
        ArrayList<PatchExtensionMethod.Extension> extensions = new ArrayList<PatchExtensionMethod.Extension>();
        ClassScope classScope = PatchExtensionMethodCompletionProposal.getClassScope(completionProposalCollector);
        if (classScope != null) {
            TypeDeclaration decl = classScope.referenceContext;
            TypeBinding firstParameterType = PatchExtensionMethodCompletionProposal.getFirstParameterType(decl, completionProposalCollector);
            EclipseNode typeNode = PatchExtensionMethod.getTypeNode(decl);
            while (typeNode != null) {
                Annotation ann = PatchExtensionMethod.getAnnotation(ExtensionMethod.class, typeNode);
                extensions.addAll(0, PatchExtensionMethod.getApplicableExtensionMethods(typeNode, ann, firstParameterType));
                typeNode = PatchExtensionMethod.upToType(typeNode);
            }
        }
        return extensions;
    }

    private static boolean isMatchingProposal(MethodBinding method, CompletionProposalCollector completionProposalCollector) {
        try {
            InternalCompletionContext context = (InternalCompletionContext)Reflection.contextField.get(completionProposalCollector);
            String searchToken = new String(context.getToken());
            String extensionMethodName = new String(method.selector);
            return extensionMethodName.contains(searchToken);
        }
        catch (IllegalAccessException illegalAccessException) {
            return true;
        }
    }

    static TypeBinding getFirstParameterType(TypeDeclaration decl, CompletionProposalCollector completionProposalCollector) {
        TypeBinding firstParameterType = null;
        ASTNode node = PatchExtensionMethodCompletionProposal.getAssistNode(completionProposalCollector);
        if (node == null) {
            return null;
        }
        if (!(node instanceof CompletionOnQualifiedNameReference || node instanceof CompletionOnSingleNameReference || node instanceof CompletionOnMemberAccess)) {
            return null;
        }
        if (node instanceof FieldReference && ((FieldReference)node).receiver instanceof SuperReference) {
            return null;
        }
        if (node instanceof NameReference) {
            Binding binding = ((NameReference)node).binding;
            if (binding instanceof VariableBinding) {
                firstParameterType = ((VariableBinding)binding).type;
            }
        } else if (node instanceof FieldReference) {
            firstParameterType = ((FieldReference)node).actualReceiverType;
        }
        return firstParameterType;
    }

    private static ASTNode getAssistNode(CompletionProposalCollector completionProposalCollector) {
        InternalExtendedCompletionContext extendedContext;
        block3: {
            try {
                InternalCompletionContext context = (InternalCompletionContext)Reflection.contextField.get(completionProposalCollector);
                extendedContext = (InternalExtendedCompletionContext)Reflection.extendedContextField.get(context);
                if (extendedContext != null) break block3;
                return null;
            }
            catch (Exception exception) {
                return null;
            }
        }
        return (ASTNode)Reflection.assistNodeField.get(extendedContext);
    }

    private static ClassScope getClassScope(CompletionProposalCollector completionProposalCollector) {
        ClassScope scope = null;
        try {
            Scope assistScope;
            InternalCompletionContext context = (InternalCompletionContext)Reflection.contextField.get(completionProposalCollector);
            InternalExtendedCompletionContext extendedContext = (InternalExtendedCompletionContext)Reflection.extendedContextField.get(context);
            if (extendedContext != null && (assistScope = (Scope)Reflection.assistScopeField.get(extendedContext)) != null) {
                scope = assistScope.classScope();
            }
        }
        catch (IllegalAccessException illegalAccessException) {}
        return scope;
    }

    private static void copyNameLookupAndCompletionEngine(CompletionProposalCollector completionProposalCollector, InternalCompletionProposal newProposal) {
        try {
            InternalCompletionContext context = (InternalCompletionContext)Reflection.contextField.get(completionProposalCollector);
            InternalExtendedCompletionContext extendedContext = (InternalExtendedCompletionContext)Reflection.extendedContextField.get(context);
            LookupEnvironment lookupEnvironment = (LookupEnvironment)Reflection.lookupEnvironmentField.get(extendedContext);
            Reflection.nameLookupField.set(newProposal, ((SearchableEnvironment)lookupEnvironment.nameEnvironment).nameLookup);
            Reflection.completionEngineField.set(newProposal, lookupEnvironment.typeRequestor);
        }
        catch (IllegalAccessException illegalAccessException) {}
    }

    private static void createAndAddJavaCompletionProposal(CompletionProposalCollector completionProposalCollector, CompletionProposal newProposal, List<IJavaCompletionProposal> proposals) {
        try {
            proposals.add((IJavaCompletionProposal)Reflection.createJavaCompletionProposalMethod.invoke((Object)completionProposalCollector, newProposal));
        }
        catch (Exception exception) {}
    }

    private static boolean canExtendCodeAssist() {
        return Reflection.isComplete();
    }

    static class Reflection {
        public static final Field replacementOffsetField = Reflection.accessField(AbstractJavaCompletionProposal.class, "fReplacementOffset");
        public static final Field contextField = Reflection.accessField(CompletionProposalCollector.class, "fContext");
        public static final Field extendedContextField = Reflection.accessField(InternalCompletionContext.class, "extendedContext");
        public static final Field assistNodeField = Reflection.accessField(InternalExtendedCompletionContext.class, "assistNode");
        public static final Field assistScopeField = Reflection.accessField(InternalExtendedCompletionContext.class, "assistScope");
        public static final Field lookupEnvironmentField = Reflection.accessField(InternalExtendedCompletionContext.class, "lookupEnvironment");
        public static final Field completionEngineField = Reflection.accessField(InternalCompletionProposal.class, "completionEngine");
        public static final Field nameLookupField = Reflection.accessField(InternalCompletionProposal.class, "nameLookup");
        public static final Method createJavaCompletionProposalMethod = Reflection.accessMethod(CompletionProposalCollector.class, "createJavaCompletionProposal", CompletionProposal.class);

        Reflection() {
        }

        static boolean isComplete() {
            Object[] requiredFieldsAndMethods;
            Object[] objectArray = requiredFieldsAndMethods = new Object[]{replacementOffsetField, contextField, extendedContextField, assistNodeField, assistScopeField, lookupEnvironmentField, completionEngineField, nameLookupField, createJavaCompletionProposalMethod};
            int n = requiredFieldsAndMethods.length;
            int n2 = 0;
            while (n2 < n) {
                Object o = objectArray[n2];
                if (o == null) {
                    return false;
                }
                ++n2;
            }
            return true;
        }

        private static Field accessField(Class<?> clazz, String fieldName) {
            try {
                return Reflection.makeAccessible(clazz.getDeclaredField(fieldName));
            }
            catch (Exception exception) {
                return null;
            }
        }

        private static Method accessMethod(Class<?> clazz, String methodName, Class<?> parameter) {
            try {
                return Reflection.makeAccessible(clazz.getDeclaredMethod(methodName, parameter));
            }
            catch (Exception exception) {
                return null;
            }
        }

        private static <T extends AccessibleObject> T makeAccessible(T object) {
            return Permit.setAccessible(object);
        }
    }
}
