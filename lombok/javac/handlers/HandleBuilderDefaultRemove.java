package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.Builder;
import lombok.core.AlreadyHandledAnnotations;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=32768)
@AlreadyHandledAnnotations
public class HandleBuilderDefaultRemove
extends JavacAnnotationHandler<Builder.Default> {
    @Override
    public void handle(AnnotationValues<Builder.Default> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Builder.Default.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, Builder.class.getName());
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, Builder.Default.class.getName());
    }
}
