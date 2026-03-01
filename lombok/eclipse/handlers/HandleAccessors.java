package lombok.eclipse.handlers;

import lombok.ConfigurationKeys;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.experimental.Accessors;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

@HandlerPriority(value=65536)
public class HandleAccessors
extends EclipseAnnotationHandler<Accessors> {
    @Override
    public void handle(AnnotationValues<Accessors> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.ACCESSORS_FLAG_USAGE, "@Accessors");
        if (annotation.isMarking()) {
            annotationNode.addWarning("Accessors on its own does nothing. Set at least one parameter");
        }
    }
}
