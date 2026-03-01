package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.lang.annotation.Annotation;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.handlers.HandlerUtil;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public final class HandleLockedUtil {
    private static final String INSTANCE_LOCK_NAME = "$lock";
    private static final String STATIC_LOCK_NAME = "$LOCK";
    private static final List<JCTree.JCExpression> NIL_EXPRESSION = List.nil();

    private HandleLockedUtil() {
    }

    public static <T extends Annotation> void handle(String annotationValue, JCTree.JCAnnotation ast, JavacNode annotationNode, Class<T> annotationClass, String annotationName, String[] lockTypeClass, String[] lockImplClass) {
        HandleLockedUtil.handle(annotationValue, ast, annotationNode, annotationClass, annotationName, lockTypeClass, lockImplClass, null);
    }

    public static <T extends Annotation> void handle(String annotationValue, JCTree.JCAnnotation ast, JavacNode annotationNode, Class<T> annotationClass, String annotationName, String[] lockTypeClass, String[] lockImplClass, String lockableMethodName) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOCKED_FLAG_USAGE, annotationName);
        if (JavacHandlerUtil.inNetbeansEditor(annotationNode)) {
            return;
        }
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, annotationClass);
        JavacNode methodNode = (JavacNode)annotationNode.up();
        if (methodNode == null || methodNode.getKind() != AST.Kind.METHOD || !(methodNode.get() instanceof JCTree.JCMethodDecl)) {
            annotationNode.addError(String.valueOf(annotationName) + " is legal only on methods.");
            return;
        }
        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl)methodNode.get();
        if ((method.mods.flags & 0x400L) != 0L) {
            annotationNode.addError(String.valueOf(annotationName) + " is legal only on concrete methods.");
            return;
        }
        JavacNode typeNode = JavacHandlerUtil.upToTypeNode(annotationNode);
        if (!JavacHandlerUtil.isClassOrEnum(typeNode)) {
            annotationNode.addError(String.valueOf(annotationName) + " is legal only on methods in classes and enums.");
            return;
        }
        boolean isStatic = (method.mods.flags & 8L) != 0L;
        String lockName = annotationValue;
        boolean autoMake = false;
        if (lockName.length() == 0) {
            autoMake = true;
            lockName = isStatic ? STATIC_LOCK_NAME : INSTANCE_LOCK_NAME;
        }
        JavacTreeMaker maker = methodNode.getTreeMaker().at(ast.pos);
        JavacHandlerUtil.MemberExistsResult exists = JavacHandlerUtil.MemberExistsResult.NOT_EXISTS;
        JCTree.JCExpression lockVarType = JavacHandlerUtil.chainDots(methodNode, ast.pos, null, null, lockTypeClass);
        if (typeNode != null && typeNode.get() instanceof JCTree.JCClassDecl) {
            for (JCTree def : ((JCTree.JCClassDecl)typeNode.get()).defs) {
                boolean st;
                if (!(def instanceof JCTree.JCVariableDecl) || !((JCTree.JCVariableDecl)def).name.contentEquals(lockName)) continue;
                JCTree.JCVariableDecl varDeclDef = (JCTree.JCVariableDecl)def;
                exists = JavacHandlerUtil.getGeneratedBy(varDeclDef) == null ? JavacHandlerUtil.MemberExistsResult.EXISTS_BY_USER : JavacHandlerUtil.MemberExistsResult.EXISTS_BY_LOMBOK;
                boolean bl = st = (varDeclDef.mods.flags & 8L) != 0L;
                if (isStatic != st && exists == JavacHandlerUtil.MemberExistsResult.EXISTS_BY_LOMBOK) {
                    annotationNode.addError("The generated field " + lockName + " does not match the static status of this method");
                    return;
                }
                isStatic = st;
                if (exists != JavacHandlerUtil.MemberExistsResult.EXISTS_BY_LOMBOK || lockVarType.toString().equals(varDeclDef.vartype.toString())) continue;
                annotationNode.addError("Expected field " + lockName + " to be of type " + lockVarType + " but got type " + varDeclDef.vartype + ". Did you mix @Locked with @Locked.Read/Write on the same generated field?");
                return;
            }
        }
        if (exists == JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            if (!autoMake) {
                annotationNode.addError("The field " + lockName + " does not exist.");
                return;
            }
            JCTree.JCExpression lockImplType = JavacHandlerUtil.chainDots(methodNode, ast.pos, null, null, lockImplClass);
            JCTree.JCNewClass lockInstance = maker.NewClass(null, NIL_EXPRESSION, lockImplType, NIL_EXPRESSION, null);
            JCTree.JCVariableDecl newLockField = JavacHandlerUtil.recursiveSetGeneratedBy(maker.VarDef(maker.Modifiers(0x12 | (isStatic ? 8 : 0)), methodNode.toName(lockName), lockVarType, lockInstance), annotationNode);
            JavacHandlerUtil.injectFieldAndMarkGenerated((JavacNode)methodNode.up(), newLockField);
        }
        if (method.body == null) {
            return;
        }
        JCTree.JCExpression lockNode = isStatic ? JavacHandlerUtil.namePlusTypeParamsToTypeReference(maker, typeNode, methodNode.toName(lockName), false, List.<JCTree.JCTypeParameter>nil()) : maker.Select(maker.Ident(methodNode.toName("this")), methodNode.toName(lockName));
        JCTree.JCExpressionStatement acquireLock = maker.Exec(maker.Apply(NIL_EXPRESSION, maker.Select(HandleLockedUtil.getLockable(maker, typeNode, methodNode, lockableMethodName, lockNode), annotationNode.toName("lock")), NIL_EXPRESSION));
        JCTree.JCExpressionStatement releaseLock = maker.Exec(maker.Apply(NIL_EXPRESSION, maker.Select(HandleLockedUtil.getLockable(maker, typeNode, methodNode, lockableMethodName, lockNode), annotationNode.toName("unlock")), NIL_EXPRESSION));
        JCTree.JCTry tryBlock = JavacHandlerUtil.setGeneratedBy(maker.Try(method.body, List.<JCTree.JCCatch>nil(), JavacHandlerUtil.recursiveSetGeneratedBy(maker.Block(0L, List.of(releaseLock)), annotationNode)), annotationNode);
        method.body = JavacHandlerUtil.setGeneratedBy(maker.Block(0L, List.of((JCTree.JCStatement)JavacHandlerUtil.recursiveSetGeneratedBy(acquireLock, annotationNode), tryBlock)), annotationNode);
        methodNode.rebuild();
    }

    private static JCTree.JCExpression getLockable(JavacTreeMaker maker, JavacNode typeNode, JavacNode methodNode, String lockableMethodName, JCTree.JCExpression lockNode) {
        if (lockableMethodName == null) {
            return JavacHandlerUtil.cloneType(maker, lockNode, typeNode);
        }
        return maker.Apply(NIL_EXPRESSION, maker.Select(JavacHandlerUtil.cloneType(maker, lockNode, typeNode), methodNode.toName(lockableMethodName)), NIL_EXPRESSION);
    }
}
