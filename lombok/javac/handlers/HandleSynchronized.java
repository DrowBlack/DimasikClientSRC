package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.ConfigurationKeys;
import lombok.Synchronized;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=1024)
public class HandleSynchronized
extends JavacAnnotationHandler<Synchronized> {
    private static final String INSTANCE_LOCK_NAME = "$lock";
    private static final String STATIC_LOCK_NAME = "$LOCK";

    @Override
    public void handle(AnnotationValues<Synchronized> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.SYNCHRONIZED_FLAG_USAGE, "@Synchronized");
        if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
            return;
        }
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Synchronized.class);
        JavacNode methodNode = (JavacNode)annotationNode.up();
        if (methodNode == null || methodNode.getKind() != AST.Kind.METHOD || !(methodNode.get() instanceof JCTree.JCMethodDecl)) {
            annotationNode.addError("@Synchronized is legal only on methods.");
            return;
        }
        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl)methodNode.get();
        if ((method.mods.flags & 0x400L) != 0L) {
            annotationNode.addError("@Synchronized is legal only on concrete methods.");
            return;
        }
        JavacNode typeNode = JavacHandlerUtil.upToTypeNode(annotationNode);
        if (!JavacHandlerUtil.isClassOrEnum(typeNode)) {
            annotationNode.addError("@Synchronized is legal only on methods in classes and enums.");
            return;
        }
        boolean isStatic = (method.mods.flags & 8L) != 0L;
        String lockName = annotation.getInstance().value();
        boolean autoMake = false;
        if (lockName.length() == 0) {
            autoMake = true;
            lockName = isStatic ? STATIC_LOCK_NAME : INSTANCE_LOCK_NAME;
        }
        JavacTreeMaker maker = methodNode.getTreeMaker().at(ast.pos);
        JavacHandlerUtil.MemberExistsResult exists = JavacHandlerUtil.MemberExistsResult.NOT_EXISTS;
        if (typeNode != null && typeNode.get() instanceof JCTree.JCClassDecl) {
            for (JCTree def : ((JCTree.JCClassDecl)typeNode.get()).defs) {
                boolean st;
                if (!(def instanceof JCTree.JCVariableDecl) || !((JCTree.JCVariableDecl)def).name.contentEquals(lockName)) continue;
                exists = JavacHandlerUtil.getGeneratedBy(def) == null ? JavacHandlerUtil.MemberExistsResult.EXISTS_BY_USER : JavacHandlerUtil.MemberExistsResult.EXISTS_BY_LOMBOK;
                boolean bl = st = (((JCTree.JCVariableDecl)def).mods.flags & 8L) != 0L;
                if (isStatic && !st) {
                    annotationNode.addError("The field " + lockName + " is non-static and this cannot be used on this static method");
                    return;
                }
                isStatic = st;
            }
        }
        if (exists == JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            if (!autoMake) {
                annotationNode.addError("The field " + lockName + " does not exist.");
                return;
            }
            JCTree.JCExpression objectType = JavacHandlerUtil.genJavaLangTypeRef(methodNode, ast.pos, "Object");
            JCTree.JCNewArray newObjectArray = maker.NewArray(JavacHandlerUtil.genJavaLangTypeRef(methodNode, ast.pos, "Object"), List.of(maker.Literal(Javac.CTC_INT, 0)), null);
            JCTree.JCVariableDecl fieldDecl = JavacHandlerUtil.recursiveSetGeneratedBy(maker.VarDef(maker.Modifiers(0x12 | (isStatic ? 8 : 0)), methodNode.toName(lockName), objectType, newObjectArray), annotationNode);
            JavacHandlerUtil.injectFieldAndMarkGenerated((JavacNode)methodNode.up(), fieldDecl);
        }
        if (method.body == null) {
            return;
        }
        JCTree.JCExpression lockNode = isStatic ? JavacHandlerUtil.namePlusTypeParamsToTypeReference(maker, typeNode, methodNode.toName(lockName), false, List.<JCTree.JCTypeParameter>nil()) : maker.Select(maker.Ident(methodNode.toName("this")), methodNode.toName(lockName));
        JavacHandlerUtil.recursiveSetGeneratedBy(lockNode, annotationNode);
        method.body = JavacHandlerUtil.setGeneratedBy(maker.Block(0L, List.of((JCTree.JCStatement)JavacHandlerUtil.setGeneratedBy(maker.Synchronized(lockNode, method.body), annotationNode))), annotationNode);
        methodNode.rebuild();
    }
}
