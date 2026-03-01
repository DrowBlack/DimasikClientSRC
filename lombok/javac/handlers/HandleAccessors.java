package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.ConfigurationKeys;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.experimental.Accessors;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=65536)
public class HandleAccessors
extends JavacAnnotationHandler<Accessors> {
    @Override
    public void handle(AnnotationValues<Accessors> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.ACCESSORS_FLAG_USAGE, "@Accessors");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Accessors.class);
        if (annotation.isMarking()) {
            annotationNode.addWarning("Accessors on its own does nothing. Set at least one parameter");
        }
    }
}
