package lombok.javac.handlers;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.experimental.Helper;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleHelper
extends JavacAnnotationHandler<Helper> {
    private List<JCTree.JCStatement> getStatementsFromJcNode(JCTree tree) {
        if (tree instanceof JCTree.JCBlock) {
            return ((JCTree.JCBlock)tree).stats;
        }
        if (tree instanceof JCTree.JCCase) {
            return ((JCTree.JCCase)tree).stats;
        }
        if (tree instanceof JCTree.JCMethodDecl) {
            return ((JCTree.JCMethodDecl)tree).body.stats;
        }
        return null;
    }

    private void setStatementsOfJcNode(JCTree tree, List<JCTree.JCStatement> statements) {
        if (tree instanceof JCTree.JCBlock) {
            ((JCTree.JCBlock)tree).stats = statements;
        } else if (tree instanceof JCTree.JCCase) {
            ((JCTree.JCCase)tree).stats = statements;
        } else if (tree instanceof JCTree.JCMethodDecl) {
            ((JCTree.JCMethodDecl)tree).body.stats = statements;
        } else {
            throw new IllegalArgumentException("Can't set statements on node type: " + tree.getClass());
        }
    }

    @Override
    public void handle(AnnotationValues<Helper> annotation, JCTree.JCAnnotation ast, final JavacNode annotationNode) {
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.HELPER_FLAG_USAGE, "@Helper");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Helper.class);
        JavacNode annotatedType = (JavacNode)annotationNode.up();
        JavacNode containingBlock = annotatedType == null ? null : (JavacNode)annotatedType.directUp();
        List<JCTree.JCStatement> origStatements = this.getStatementsFromJcNode(containingBlock == null ? null : (JCTree)containingBlock.get());
        if (annotatedType == null || annotatedType.getKind() != AST.Kind.TYPE || origStatements == null) {
            annotationNode.addError("@Helper is legal only on method-local classes.");
            return;
        }
        JCTree.JCClassDecl annotatedType_ = (JCTree.JCClassDecl)annotatedType.get();
        Iterator<JCTree.JCStatement> it = origStatements.iterator();
        while (it.hasNext()) {
            if (it.next() == annotatedType_) break;
        }
        ArrayList<String> knownMethodNames = new ArrayList<String>();
        for (JavacNode ch : annotatedType.down()) {
            String n;
            if (ch.getKind() != AST.Kind.METHOD || (n = ch.getName()) == null || n.isEmpty() || n.charAt(0) == '<') continue;
            knownMethodNames.add(n);
        }
        Collections.sort(knownMethodNames);
        final String[] knownMethodNames_ = knownMethodNames.toArray(new String[0]);
        final Name helperName = annotationNode.toName("$" + annotatedType_.name);
        final boolean[] helperUsed = new boolean[1];
        final JavacTreeMaker maker = annotationNode.getTreeMaker();
        TreeScanner<Void, Void> visitor = new TreeScanner<Void, Void>(){

            @Override
            public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
                JCTree.JCMethodInvocation jcmi = (JCTree.JCMethodInvocation)node;
                this.apply(jcmi);
                return (Void)super.visitMethodInvocation(node, p);
            }

            private void apply(JCTree.JCMethodInvocation jcmi) {
                if (!(jcmi.meth instanceof JCTree.JCIdent)) {
                    return;
                }
                JCTree.JCIdent jci = (JCTree.JCIdent)jcmi.meth;
                if (Arrays.binarySearch(knownMethodNames_, jci.name.toString()) < 0) {
                    return;
                }
                jcmi.meth = maker.Select(maker.Ident(helperName), jci.name);
                JavacHandlerUtil.recursiveSetGeneratedBy(jcmi.meth, annotationNode);
                helperUsed[0] = true;
            }
        };
        while (it.hasNext()) {
            JCTree.JCStatement stat = it.next();
            stat.accept(visitor, null);
        }
        if (!helperUsed[0]) {
            annotationNode.addWarning("No methods of this helper class are ever used.");
            return;
        }
        ListBuffer<JCTree.JCStatement> newStatements = new ListBuffer<JCTree.JCStatement>();
        boolean mark = false;
        for (JCTree.JCStatement stat : origStatements) {
            newStatements.append(stat);
            if (mark || stat != annotatedType_) continue;
            mark = true;
            JCTree.JCNewClass init = maker.NewClass(null, List.<JCTree.JCExpression>nil(), maker.Ident(annotatedType_.name), List.<JCTree.JCExpression>nil(), null);
            JCTree.JCIdent varType = maker.Ident(annotatedType_.name);
            JCTree.JCVariableDecl decl = maker.VarDef(maker.Modifiers(16L), helperName, varType, init);
            JavacHandlerUtil.recursiveSetGeneratedBy(decl, annotationNode);
            newStatements.append(decl);
        }
        this.setStatementsOfJcNode((JCTree)containingBlock.get(), newStatements.toList());
    }
}
