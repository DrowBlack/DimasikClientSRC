package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.Singular;
import lombok.core.AlreadyHandledAnnotations;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=32768)
@AlreadyHandledAnnotations
public class HandleSingularRemove
extends JavacAnnotationHandler<Singular> {
    @Override
    public void handle(AnnotationValues<Singular> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Singular.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, Singular.class.getName());
    }
}
