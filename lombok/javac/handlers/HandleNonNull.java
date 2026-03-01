package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.NonNull;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=512)
public class HandleNonNull
extends JavacAnnotationHandler<NonNull> {
    private JCTree.JCMethodDecl createRecordArgslessConstructor(JavacNode typeNode, JavacNode source, JCTree.JCMethodDecl existingCtr) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        ArrayList<JCTree.JCVariableDecl> fields = new ArrayList<JCTree.JCVariableDecl>();
        for (JavacNode child : typeNode.down()) {
            if (child.getKind() != AST.Kind.FIELD) continue;
            JCTree.JCVariableDecl v = (JCTree.JCVariableDecl)child.get();
            if ((v.mods.flags & 0x2000000000000000L) == 0L) continue;
            fields.add(v);
        }
        ListBuffer<JCTree.JCVariableDecl> params = new ListBuffer<JCTree.JCVariableDecl>();
        int i = 0;
        while (i < fields.size()) {
            JCTree.JCVariableDecl arg = (JCTree.JCVariableDecl)fields.get(i);
            JCTree.JCModifiers mods = maker.Modifiers(0x201000000L, arg.mods.annotations);
            params.append(maker.VarDef(mods, arg.name, arg.vartype, null));
            ++i;
        }
        JCTree.JCModifiers mods = maker.Modifiers((long)JavacHandlerUtil.toJavacModifier(AccessLevel.PUBLIC) | 0x8000000000000L, List.<JCTree.JCAnnotation>nil());
        JCTree.JCBlock body = maker.Block(0L, List.<JCTree.JCStatement>nil());
        if (existingCtr == null) {
            JCTree.JCMethodDecl constr = maker.MethodDef(mods, typeNode.toName("<init>"), null, List.<JCTree.JCTypeParameter>nil(), params.toList(), List.<JCTree.JCExpression>nil(), body, null);
            return JavacHandlerUtil.recursiveSetGeneratedBy(constr, source);
        }
        existingCtr.mods = mods;
        existingCtr.body = body;
        existingCtr = JavacHandlerUtil.recursiveSetGeneratedBy(existingCtr, source);
        JavacHandlerUtil.addSuppressWarningsAll(existingCtr.mods, typeNode, (JavacNode)typeNode.getNodeFor(JavacHandlerUtil.getGeneratedBy(existingCtr)), typeNode.getContext());
        JavacHandlerUtil.addGenerated(existingCtr.mods, typeNode, (JavacNode)typeNode.getNodeFor(JavacHandlerUtil.getGeneratedBy(existingCtr)), typeNode.getContext());
        return existingCtr;
    }

    private List<JCTree.JCMethodDecl> addCompactConstructorIfNeeded(JavacNode typeNode, JavacNode source) {
        List<JCTree.JCMethodDecl> answer = List.nil();
        if (typeNode == null || !(typeNode.get() instanceof JCTree.JCClassDecl)) {
            return answer;
        }
        JCTree.JCClassDecl cDecl = (JCTree.JCClassDecl)typeNode.get();
        if ((cDecl.mods.flags & 0x2000000000000000L) == 0L) {
            return answer;
        }
        boolean generateConstructor = false;
        JCTree.JCMethodDecl existingCtr = null;
        for (JCTree def : cDecl.defs) {
            if (!(def instanceof JCTree.JCMethodDecl)) continue;
            JCTree.JCMethodDecl md = (JCTree.JCMethodDecl)def;
            if (!md.name.contentEquals("<init>")) continue;
            if ((md.mods.flags & 0x1000000000L) != 0L) {
                existingCtr = md;
                existingCtr.mods.flags &= 0xFFFFFFEFFFFFFFFFL;
                generateConstructor = true;
                continue;
            }
            if (JavacHandlerUtil.isTolerate(typeNode, md) || (md.mods.flags & 0x8000000000000L) == 0L) continue;
            generateConstructor = false;
            answer = answer.prepend(md);
        }
        if (generateConstructor) {
            JCTree.JCMethodDecl ctr;
            if (existingCtr != null) {
                ctr = this.createRecordArgslessConstructor(typeNode, source, existingCtr);
            } else {
                ctr = this.createRecordArgslessConstructor(typeNode, source, null);
                JavacHandlerUtil.injectMethod(typeNode, ctr);
            }
            answer = answer.prepend(ctr);
        }
        return answer;
    }

    private void addNullCheckIfNeeded(JCTree.JCMethodDecl method, JavacNode paramNode, JavacNode source) {
        JCTree.JCStatement nullCheck = JavacHandlerUtil.recursiveSetGeneratedBy(JavacHandlerUtil.generateNullCheck(source.getTreeMaker(), paramNode, source), source);
        if (nullCheck == null) {
            source.addWarning("@NonNull is meaningless on a primitive.");
            return;
        }
        List<JCTree.JCStatement> statements = method.body.stats;
        String expectedName = paramNode.getName();
        List<JCTree.JCStatement> stats = statements;
        int idx = 0;
        while (stats.size() > idx) {
            JCTree.JCStatement stat;
            if (JavacHandlerUtil.isConstructorCall(stat = stats.get(idx++))) continue;
            if (stat instanceof JCTree.JCTry) {
                stats = ((JCTree.JCTry)stat).body.stats;
                idx = 0;
                continue;
            }
            if (stat instanceof JCTree.JCSynchronized) {
                stats = ((JCTree.JCSynchronized)stat).body.stats;
                idx = 0;
                continue;
            }
            String varNameOfNullCheck = this.returnVarNameIfNullCheck(stat);
            if (varNameOfNullCheck == null) break;
            if (!varNameOfNullCheck.equals(expectedName)) continue;
            return;
        }
        List<JCTree.JCStatement> tail = statements;
        List<JCTree.JCStatement> head = List.nil();
        for (JCTree.JCStatement stat : statements) {
            if (!JavacHandlerUtil.isConstructorCall(stat) && (!JavacHandlerUtil.isGenerated(stat) || !this.isNullCheck(stat))) break;
            tail = tail.tail;
            head = head.prepend(stat);
        }
        List<JCTree.JCStatement> newList = tail.prepend(nullCheck);
        for (JCTree.JCStatement stat : head) {
            newList = newList.prepend(stat);
        }
        method.body.stats = newList;
        source.getAst().setChanged();
    }

    @Override
    public void handle(AnnotationValues<NonNull> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JCTree.JCMethodDecl declaration;
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.NON_NULL_FLAG_USAGE, "@NonNull");
        JavacNode node = ((JavacNode)annotationNode.up()).getKind() == AST.Kind.TYPE_USE ? (JavacNode)((JavacNode)annotationNode.directUp()).directUp() : (JavacNode)annotationNode.up();
        if (node.getKind() == AST.Kind.FIELD) {
            try {
                if (Javac.isPrimitive(((JCTree.JCVariableDecl)node.get()).vartype)) {
                    annotationNode.addWarning("@NonNull is meaningless on a primitive.");
                }
            }
            catch (Exception exception) {}
            JCTree.JCVariableDecl fDecl = (JCTree.JCVariableDecl)node.get();
            if ((fDecl.mods.flags & 0x2000000000000000L) != 0L) {
                List<JCTree.JCMethodDecl> compactConstructors = this.addCompactConstructorIfNeeded((JavacNode)node.up(), annotationNode);
                for (JCTree.JCMethodDecl ctr : compactConstructors) {
                    this.addNullCheckIfNeeded(ctr, node, annotationNode);
                }
            }
            return;
        }
        if (node.getKind() != AST.Kind.ARGUMENT) {
            return;
        }
        try {
            declaration = (JCTree.JCMethodDecl)((JavacNode)node.up()).get();
        }
        catch (Exception exception) {
            return;
        }
        if (declaration.body == null) {
            return;
        }
        if ((declaration.mods.flags & 0x8000001000000L) != 0L) {
            return;
        }
        this.addNullCheckIfNeeded(declaration, node, annotationNode);
    }

    public boolean isNullCheck(JCTree.JCStatement stat) {
        return this.returnVarNameIfNullCheck(stat) != null;
    }

    public String returnVarNameIfNullCheck(JCTree.JCStatement stat) {
        boolean isIf = stat instanceof JCTree.JCIf;
        boolean isExpression = stat instanceof JCTree.JCExpressionStatement;
        if (!(isIf || stat instanceof JCTree.JCAssert || isExpression)) {
            return null;
        }
        if (isExpression) {
            JCTree.JCExpression expression = ((JCTree.JCExpressionStatement)stat).expr;
            if (expression instanceof JCTree.JCAssign) {
                expression = ((JCTree.JCAssign)expression).rhs;
            }
            if (!(expression instanceof JCTree.JCMethodInvocation)) {
                return null;
            }
            JCTree.JCMethodInvocation invocation = (JCTree.JCMethodInvocation)expression;
            JCTree.JCExpression method = invocation.meth;
            Name name = null;
            if (method instanceof JCTree.JCFieldAccess) {
                name = ((JCTree.JCFieldAccess)method).name;
            } else if (method instanceof JCTree.JCIdent) {
                name = ((JCTree.JCIdent)method).name;
            }
            if (name == null || !name.contentEquals("checkNotNull") && !name.contentEquals("requireNonNull")) {
                return null;
            }
            if (invocation.args.isEmpty()) {
                return null;
            }
            JCTree.JCExpression firstArgument = (JCTree.JCExpression)invocation.args.head;
            if (!(firstArgument instanceof JCTree.JCIdent)) {
                return null;
            }
            return ((JCTree.JCIdent)firstArgument).toString();
        }
        if (isIf) {
            JCTree.JCStatement then = ((JCTree.JCIf)stat).thenpart;
            if (then instanceof JCTree.JCBlock) {
                List<JCTree.JCStatement> stats = ((JCTree.JCBlock)then).stats;
                if (stats.length() == 0) {
                    return null;
                }
                then = stats.get(0);
            }
            if (!(then instanceof JCTree.JCThrow)) {
                return null;
            }
        }
        JCTree.JCExpression cond = isIf ? ((JCTree.JCIf)stat).cond : ((JCTree.JCAssert)stat).cond;
        while (cond instanceof JCTree.JCParens) {
            cond = ((JCTree.JCParens)cond).expr;
        }
        if (!(cond instanceof JCTree.JCBinary)) {
            return null;
        }
        JCTree.JCBinary bin = (JCTree.JCBinary)cond;
        if (isIf ? !Javac.CTC_EQUAL.equals(JavacTreeMaker.TreeTag.treeTag(bin)) : !Javac.CTC_NOT_EQUAL.equals(JavacTreeMaker.TreeTag.treeTag(bin))) {
            return null;
        }
        if (!(bin.lhs instanceof JCTree.JCIdent)) {
            return null;
        }
        if (!(bin.rhs instanceof JCTree.JCLiteral)) {
            return null;
        }
        if (!Javac.CTC_BOT.equals(JavacTreeMaker.TypeTag.typeTag(bin.rhs))) {
            return null;
        }
        return ((JCTree.JCIdent)bin.lhs).name.toString();
    }
}
