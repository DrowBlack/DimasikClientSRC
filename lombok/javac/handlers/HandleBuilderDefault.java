package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.Builder;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.experimental.SuperBuilder;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=-1025)
public class HandleBuilderDefault
extends JavacAnnotationHandler<Builder.Default> {
    @Override
    public void handle(AnnotationValues<Builder.Default> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JavacNode annotatedField = (JavacNode)annotationNode.up();
        if (annotatedField.getKind() != AST.Kind.FIELD) {
            return;
        }
        JavacNode classWithAnnotatedField = (JavacNode)annotatedField.up();
        if (!(JavacHandlerUtil.hasAnnotation(Builder.class, classWithAnnotatedField) || JavacHandlerUtil.hasAnnotation("lombok.experimental.Builder", classWithAnnotatedField) || JavacHandlerUtil.hasAnnotation(SuperBuilder.class, classWithAnnotatedField))) {
            annotationNode.addWarning("@Builder.Default requires @Builder or @SuperBuilder on the class for it to mean anything.");
            JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Builder.Default.class);
        }
        if (ast.annotationType instanceof JCTree.JCFieldAccess) {
            JCTree.JCFieldAccess jfa = (JCTree.JCFieldAccess)ast.annotationType;
            if (jfa.selected instanceof JCTree.JCIdent && ((JCTree.JCIdent)jfa.selected).name.contentEquals("Builder") && jfa.name.contentEquals("Default")) {
                JCTree.JCFieldAccess newJfaSel = annotationNode.getTreeMaker().Select(annotationNode.getTreeMaker().Ident(annotationNode.toName("lombok")), ((JCTree.JCIdent)jfa.selected).name);
                JavacHandlerUtil.recursiveSetGeneratedBy(newJfaSel, annotationNode);
                jfa.selected = newJfaSel;
            }
        }
    }
}
