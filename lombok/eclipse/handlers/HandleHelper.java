package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import lombok.experimental.Helper;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class HandleHelper
extends EclipseAnnotationHandler<Helper> {
    private Statement[] getStatementsFromAstNode(ASTNode node) {
        if (node instanceof Block) {
            return ((Block)node).statements;
        }
        if (node instanceof AbstractMethodDeclaration) {
            return ((AbstractMethodDeclaration)node).statements;
        }
        if (node instanceof SwitchStatement) {
            return ((SwitchStatement)node).statements;
        }
        return null;
    }

    private void setStatementsOfAstNode(ASTNode node, Statement[] statements) {
        if (node instanceof Block) {
            ((Block)node).statements = statements;
        } else if (node instanceof AbstractMethodDeclaration) {
            ((AbstractMethodDeclaration)node).statements = statements;
        } else if (node instanceof SwitchStatement) {
            ((SwitchStatement)node).statements = statements;
        } else {
            throw new IllegalArgumentException("Can't set statements on node type: " + node.getClass());
        }
    }

    @Override
    public void handle(AnnotationValues<Helper> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.HELPER_FLAG_USAGE, "@Helper");
        EclipseNode annotatedType = (EclipseNode)annotationNode.up();
        EclipseNode containingBlock = annotatedType == null ? null : (EclipseNode)annotatedType.directUp();
        Statement[] origStatements = this.getStatementsFromAstNode(containingBlock == null ? null : (ASTNode)containingBlock.get());
        if (annotatedType == null || annotatedType.getKind() != AST.Kind.TYPE || origStatements == null) {
            annotationNode.addError("@Helper is legal only on method-local classes.");
            return;
        }
        TypeDeclaration annotatedType_ = (TypeDeclaration)annotatedType.get();
        int indexOfType = -1;
        int i = 0;
        while (i < origStatements.length) {
            if (origStatements[i] == annotatedType_) {
                indexOfType = i;
                break;
            }
            ++i;
        }
        ArrayList<String> knownMethodNames = new ArrayList<String>();
        AbstractMethodDeclaration[] abstractMethodDeclarationArray = annotatedType_.methods;
        int n = annotatedType_.methods.length;
        int n2 = 0;
        while (n2 < n) {
            char[] name;
            AbstractMethodDeclaration methodOfHelper = abstractMethodDeclarationArray[n2];
            if (methodOfHelper instanceof MethodDeclaration && (name = methodOfHelper.selector) != null && name.length > 0 && name[0] != '<') {
                knownMethodNames.add(new String(name));
            }
            ++n2;
        }
        Collections.sort(knownMethodNames);
        final String[] knownMethodNames_ = knownMethodNames.toArray(new String[0]);
        final char[] helperName = new char[annotatedType_.name.length + 1];
        final boolean[] helperUsed = new boolean[1];
        helperName[0] = 36;
        System.arraycopy(annotatedType_.name, 0, helperName, 1, helperName.length - 1);
        ASTVisitor visitor = new ASTVisitor(){

            public boolean visit(MessageSend messageSend, BlockScope scope) {
                if (messageSend.receiver instanceof ThisReference ? (((ThisReference)messageSend.receiver).bits & 4) == 0 : messageSend.receiver != null) {
                    return true;
                }
                char[] name = messageSend.selector;
                if (name == null || name.length == 0 || name[0] == '<') {
                    return true;
                }
                String n = new String(name);
                if (Arrays.binarySearch(knownMethodNames_, n) < 0) {
                    return true;
                }
                messageSend.receiver = new SingleNameReference(helperName, messageSend.nameSourcePosition);
                helperUsed[0] = true;
                return true;
            }
        };
        int i2 = indexOfType + 1;
        while (i2 < origStatements.length) {
            origStatements[i2].traverse(visitor, null);
            ++i2;
        }
        if (!helperUsed[0]) {
            annotationNode.addWarning("No methods of this helper class are ever used.");
            return;
        }
        Statement[] newStatements = new Statement[origStatements.length + 1];
        System.arraycopy(origStatements, 0, newStatements, 0, indexOfType + 1);
        System.arraycopy(origStatements, indexOfType + 1, newStatements, indexOfType + 2, origStatements.length - indexOfType - 1);
        LocalDeclaration decl = new LocalDeclaration(helperName, 0, 0);
        decl.modifiers |= 0x10;
        AllocationExpression alloc = new AllocationExpression();
        alloc.type = new SingleTypeReference(annotatedType_.name, 0L);
        decl.initialization = alloc;
        decl.type = new SingleTypeReference(annotatedType_.name, 0L);
        SetGeneratedByVisitor sgbvVisitor = new SetGeneratedByVisitor((ASTNode)annotationNode.get());
        decl.traverse((ASTVisitor)sgbvVisitor, null);
        newStatements[indexOfType + 1] = decl;
        this.setStatementsOfAstNode((ASTNode)containingBlock.get(), newStatements);
    }
}
