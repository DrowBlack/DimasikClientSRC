package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import lombok.Cleanup;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.delombok.LombokOptionsFactory;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleCleanup
extends JavacAnnotationHandler<Cleanup> {
    @Override
    public void handle(AnnotationValues<Cleanup> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        List<JCTree.JCStatement> statements;
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.CLEANUP_FLAG_USAGE, "@Cleanup");
        if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
            return;
        }
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Cleanup.class);
        String cleanupName = annotation.getInstance().value();
        if (cleanupName.length() == 0) {
            annotationNode.addError("cleanupName cannot be the empty string.");
            return;
        }
        if (((JavacNode)annotationNode.up()).getKind() != AST.Kind.LOCAL) {
            annotationNode.addError("@Cleanup is legal only on local variable declarations.");
            return;
        }
        JCTree.JCVariableDecl decl = (JCTree.JCVariableDecl)((JavacNode)annotationNode.up()).get();
        if (decl.init == null) {
            annotationNode.addError("@Cleanup variable declarations need to be initialized.");
            return;
        }
        JavacNode ancestor = (JavacNode)((JavacNode)annotationNode.up()).directUp();
        JCTree blockNode = (JCTree)ancestor.get();
        if (blockNode instanceof JCTree.JCBlock) {
            statements = ((JCTree.JCBlock)blockNode).stats;
        } else if (blockNode instanceof JCTree.JCCase) {
            statements = ((JCTree.JCCase)blockNode).stats;
        } else if (blockNode instanceof JCTree.JCMethodDecl) {
            statements = ((JCTree.JCMethodDecl)blockNode).body.stats;
        } else {
            annotationNode.addError("@Cleanup is legal only on a local variable declaration inside a block.");
            return;
        }
        boolean seenDeclaration = false;
        ListBuffer<JCTree.JCStatement> newStatements = new ListBuffer<JCTree.JCStatement>();
        ListBuffer<JCTree.JCStatement> tryBlock = new ListBuffer<JCTree.JCStatement>();
        for (JCTree.JCStatement statement : statements) {
            if (!seenDeclaration) {
                if (statement == decl) {
                    seenDeclaration = true;
                }
                newStatements.append(statement);
                continue;
            }
            tryBlock.append(statement);
        }
        if (!seenDeclaration) {
            annotationNode.addError("LOMBOK BUG: Can't find this local variable declaration inside its parent.");
            return;
        }
        this.doAssignmentCheck(annotationNode, tryBlock.toList(), decl.name);
        JavacTreeMaker maker = annotationNode.getTreeMaker();
        JCTree.JCFieldAccess cleanupMethod = maker.Select(maker.Ident(decl.name), annotationNode.toName(cleanupName));
        List<JCTree.JCStatement> cleanupCall = List.of(maker.Exec(maker.Apply(List.<JCTree.JCExpression>nil(), cleanupMethod, List.<JCTree.JCExpression>nil())));
        JCTree.JCExpression preventNullAnalysis = this.preventNullAnalysis(maker, annotationNode, maker.Ident(decl.name));
        JCTree.JCBinary isNull = maker.Binary(Javac.CTC_NOT_EQUAL, preventNullAnalysis, maker.Literal(Javac.CTC_BOT, null));
        JCTree.JCIf ifNotNullCleanup = maker.If(isNull, maker.Block(0L, cleanupCall), null);
        JCTree.JCBlock finalizer = JavacHandlerUtil.recursiveSetGeneratedBy(maker.Block(0L, List.of(ifNotNullCleanup)), annotationNode);
        newStatements.append(JavacHandlerUtil.setGeneratedBy(maker.Try(JavacHandlerUtil.setGeneratedBy(maker.Block(0L, tryBlock.toList()), annotationNode), List.<JCTree.JCCatch>nil(), finalizer), annotationNode));
        if (blockNode instanceof JCTree.JCBlock) {
            ((JCTree.JCBlock)blockNode).stats = newStatements.toList();
        } else if (blockNode instanceof JCTree.JCCase) {
            ((JCTree.JCCase)blockNode).stats = newStatements.toList();
        } else if (blockNode instanceof JCTree.JCMethodDecl) {
            ((JCTree.JCMethodDecl)blockNode).body.stats = newStatements.toList();
        } else {
            throw new AssertionError((Object)"Should not get here");
        }
        ancestor.rebuild();
    }

    public JCTree.JCExpression preventNullAnalysis(JavacTreeMaker maker, JavacNode node, JCTree.JCExpression expression) {
        if (LombokOptionsFactory.getDelombokOptions(node.getContext()).getFormatPreferences().danceAroundIdeChecks()) {
            JCTree.JCMethodInvocation singletonList = maker.Apply(List.<JCTree.JCExpression>nil(), JavacHandlerUtil.chainDotsString(node, "java.util.Collections.singletonList"), List.of(expression));
            JCTree.JCMethodInvocation cleanedExpr = maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(singletonList, node.toName("get")), List.of(maker.Literal(Javac.CTC_INT, 0)));
            return cleanedExpr;
        }
        return expression;
    }

    public void doAssignmentCheck(JavacNode node, List<JCTree.JCStatement> statements, Name name) {
        for (JCTree.JCStatement statement : statements) {
            this.doAssignmentCheck0(node, statement, name);
        }
    }

    public void doAssignmentCheck0(JavacNode node, JCTree statement, Name name) {
        JavacNode problemNode;
        if (statement instanceof JCTree.JCAssign) {
            this.doAssignmentCheck0(node, ((JCTree.JCAssign)statement).rhs, name);
        }
        if (statement instanceof JCTree.JCExpressionStatement) {
            this.doAssignmentCheck0(node, ((JCTree.JCExpressionStatement)statement).expr, name);
        }
        if (statement instanceof JCTree.JCVariableDecl) {
            this.doAssignmentCheck0(node, ((JCTree.JCVariableDecl)statement).init, name);
        }
        if (statement instanceof JCTree.JCTypeCast) {
            this.doAssignmentCheck0(node, ((JCTree.JCTypeCast)statement).expr, name);
        }
        if (statement instanceof JCTree.JCIdent && ((JCTree.JCIdent)statement).name.contentEquals(name) && (problemNode = (JavacNode)node.getNodeFor(statement)) != null) {
            problemNode.addWarning("You're assigning an auto-cleanup variable to something else. This is a bad idea.");
        }
    }
}
