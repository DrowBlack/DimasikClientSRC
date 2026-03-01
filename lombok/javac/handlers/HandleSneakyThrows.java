package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.ConfigurationKeys;
import lombok.SneakyThrows;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=1024)
public class HandleSneakyThrows
extends JavacAnnotationHandler<SneakyThrows> {
    @Override
    public void handle(AnnotationValues<SneakyThrows> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.SNEAKY_THROWS_FLAG_USAGE, "@SneakyThrows");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, SneakyThrows.class);
        Collection<String> exceptionNames = annotation.getRawExpressions("value");
        if (exceptionNames.isEmpty()) {
            exceptionNames = Collections.singleton("java.lang.Throwable");
        }
        ArrayList<String> exceptions = new ArrayList<String>();
        for (String exception : exceptionNames) {
            if (exception.endsWith(".class")) {
                exception = exception.substring(0, exception.length() - 6);
            }
            exceptions.add(exception);
        }
        JavacNode owner = (JavacNode)annotationNode.up();
        switch (owner.getKind()) {
            case METHOD: {
                this.handleMethod(annotationNode, (JCTree.JCMethodDecl)owner.get(), exceptions);
                break;
            }
            default: {
                annotationNode.addError("@SneakyThrows is legal only on methods and constructors.");
            }
        }
    }

    public void handleMethod(JavacNode annotation, JCTree.JCMethodDecl method, Collection<String> exceptions) {
        List<JCTree.JCStatement> contents;
        JavacNode methodNode = (JavacNode)annotation.up();
        if ((method.mods.flags & 0x400L) != 0L) {
            annotation.addError("@SneakyThrows can only be used on concrete methods.");
            return;
        }
        if (method.body == null || method.body.stats.isEmpty()) {
            this.generateEmptyBlockWarning(methodNode, annotation, false);
            return;
        }
        JCTree.JCStatement constructorCall = method.body.stats.get(0);
        boolean isConstructorCall = JavacHandlerUtil.isConstructorCall(constructorCall);
        List<JCTree.JCStatement> list = contents = isConstructorCall ? method.body.stats.tail : method.body.stats;
        if (contents == null || contents.isEmpty()) {
            this.generateEmptyBlockWarning(methodNode, annotation, true);
            return;
        }
        for (String exception : exceptions) {
            contents = List.of(this.buildTryCatchBlock(methodNode, contents, exception, annotation));
        }
        method.body.stats = isConstructorCall ? List.of(constructorCall).appendList(contents) : contents;
        methodNode.rebuild();
    }

    public void generateEmptyBlockWarning(JavacNode methodNode, JavacNode annotation, boolean hasConstructorCall) {
        if (hasConstructorCall) {
            annotation.addWarning("Calls to sibling / super constructors are always excluded from @SneakyThrows; @SneakyThrows has been ignored because there is no other code in this constructor.");
        } else {
            annotation.addWarning("This method or constructor is empty; @SneakyThrows has been ignored.");
        }
    }

    public JCTree.JCStatement buildTryCatchBlock(JavacNode node, List<JCTree.JCStatement> contents, String exception, JavacNode source) {
        JavacTreeMaker maker = node.getTreeMaker();
        JCTree.JCBlock tryBlock = JavacHandlerUtil.setGeneratedBy(maker.Block(0L, contents), source);
        JCTree.JCExpression varType = JavacHandlerUtil.chainDots(node, exception.split("\\."));
        JCTree.JCVariableDecl catchParam = maker.VarDef(maker.Modifiers(0x200000010L), node.toName("$ex"), varType, null);
        JCTree.JCExpression lombokLombokSneakyThrowNameRef = JavacHandlerUtil.chainDots(node, "lombok", "Lombok", "sneakyThrow");
        JCTree.JCBlock catchBody = maker.Block(0L, List.of(maker.Throw(maker.Apply(List.<JCTree.JCExpression>nil(), lombokLombokSneakyThrowNameRef, List.of(maker.Ident(node.toName("$ex")))))));
        JCTree.JCTry tryStatement = maker.Try(tryBlock, List.of(JavacHandlerUtil.recursiveSetGeneratedBy(maker.Catch(catchParam, catchBody), source)), null);
        if (JavacHandlerUtil.inNetbeansEditor(node)) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)node.top()).get();
            int startPos = ((JCTree.JCStatement)contents.head).pos;
            int endPos = Javac.getEndPosition(contents.last().pos(), top);
            tryBlock.pos = startPos;
            tryStatement.pos = startPos;
            Javac.storeEnd(tryBlock, endPos, top);
            Javac.storeEnd(tryStatement, endPos, top);
        }
        return JavacHandlerUtil.setGeneratedBy(tryStatement, source);
    }
}
