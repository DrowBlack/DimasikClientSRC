package lombok.eclipse.agent;

import java.util.Arrays;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.internal.codeassist.InternalCompletionProposal;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberAccess;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedNameReference;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.core.NameLookup;

public class ExtensionMethodCompletionProposal
extends InternalCompletionProposal {
    private char[] fullSignature;
    private char[][] parameterNames;

    public ExtensionMethodCompletionProposal(int replacementOffset) {
        super(6, replacementOffset - 1);
    }

    public void setMethodBinding(MethodBinding method, ASTNode node) {
        if (method.parameterNames != null && method.parameterNames.length > 0) {
            this.setParameterNames((char[][])Arrays.copyOfRange(method.parameterNames, 1, method.parameterNames.length));
        } else {
            this.fullSignature = CompletionEngine.getSignature((MethodBinding)method);
        }
        MethodBinding original = method.original();
        TypeBinding[] parameters = Arrays.copyOf(method.parameters, method.parameters.length);
        method.parameters = Arrays.copyOfRange(method.parameters, 1, method.parameters.length);
        TypeBinding[] originalParameters = null;
        if (original != method) {
            originalParameters = Arrays.copyOf(method.original().parameters, method.original().parameters.length);
            method.original().parameters = Arrays.copyOfRange(method.original().parameters, 1, method.original().parameters.length);
        }
        int length = method.parameters == null ? 0 : method.parameters.length;
        char[][] parameterPackageNames = new char[length][];
        char[][] parameterTypeNames = new char[length][];
        int i = 0;
        while (i < length) {
            TypeBinding type = method.original().parameters[i];
            parameterPackageNames[i] = type.qualifiedPackageName();
            parameterTypeNames[i] = type.qualifiedSourceName();
            ++i;
        }
        char[] completion = CharOperation.concat((char[])method.selector, (char[])new char[]{'(', ')'});
        this.setDeclarationSignature(CompletionEngine.getSignature((TypeBinding)method.declaringClass));
        this.setSignature(CompletionEngine.getSignature((MethodBinding)method));
        if (original != method) {
            this.setOriginalSignature(CompletionEngine.getSignature((MethodBinding)original));
        }
        this.setDeclarationPackageName(method.declaringClass.qualifiedPackageName());
        this.setDeclarationTypeName(method.declaringClass.qualifiedSourceName());
        this.setParameterPackageNames(parameterPackageNames);
        this.setParameterTypeNames(parameterTypeNames);
        this.setPackageName(method.returnType.qualifiedPackageName());
        this.setTypeName(method.returnType.qualifiedSourceName());
        this.setName(method.selector);
        this.setCompletion(completion);
        this.setFlags(method.modifiers & 0xFFFFFFF7);
        if (method.isVarargs() && length == 0) {
            this.setFlags(this.getFlags() & 0xFFFFFF7F);
        }
        int index = node.sourceEnd + 1;
        if (node instanceof CompletionOnQualifiedNameReference) {
            index -= ((CompletionOnQualifiedNameReference)node).completionIdentifier.length;
        }
        if (node instanceof CompletionOnMemberAccess) {
            index -= ((CompletionOnMemberAccess)node).token.length;
        }
        if (node instanceof CompletionOnSingleNameReference) {
            index -= ((CompletionOnSingleNameReference)node).token.length;
        }
        this.setReplaceRange(index, index);
        this.setTokenRange(index, index);
        this.setRelevance(100);
        method.parameters = parameters;
        if (original != method) {
            method.original().parameters = originalParameters;
        }
    }

    public char[][] findParameterNames(IProgressMonitor monitor) {
        if (this.parameterNames != null) {
            return this.parameterNames;
        }
        NameLookup.Answer answer = this.nameLookup.findType(new String(this.declarationTypeName), new String(this.declarationPackageName), false, 0, true, false, false, null);
        if (answer != null && answer.type != null) {
            char[][] parameterTypes = Signature.getParameterTypes((char[])this.fullSignature);
            String[] args = new String[parameterTypes.length];
            int i = 0;
            while (i < parameterTypes.length) {
                args[i] = new String(parameterTypes[i]);
                ++i;
            }
            IMethod method = answer.type.getMethod(new String(this.getName()), args);
            IMethod[] methods = answer.type.findMethods(method);
            if (methods != null && methods.length > 0) {
                method = methods[0];
            }
            if (method != null) {
                try {
                    String[] parameterNames = method.getParameterNames();
                    char[][] parameterNamesAsChar = new char[parameterNames.length - 1][];
                    int i2 = 0;
                    while (i2 < parameterNamesAsChar.length) {
                        parameterNamesAsChar[i2] = parameterNames[i2 + 1].toCharArray();
                        ++i2;
                    }
                    this.setParameterNames(parameterNamesAsChar);
                }
                catch (JavaModelException javaModelException) {}
            }
        }
        if (this.parameterNames == null) {
            this.parameterNames = super.findParameterNames(monitor);
        }
        return this.parameterNames;
    }

    public void setParameterNames(char[][] parameterNames) {
        this.parameterNames = parameterNames;
        super.setParameterNames(parameterNames);
    }
}
