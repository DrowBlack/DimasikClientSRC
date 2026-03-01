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
public class HandleBuilderRemove
extends JavacAnnotationHandler<Builder> {
    @Override
    public void handle(AnnotationValues<Builder> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Builder.class, "lombok.experimental.Builder");
    }
}
