package lombok.eclipse.handlers;

import lombok.ConfigurationKeys;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.experimental.Delegate;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleDelegate
extends EclipseAnnotationHandler<Delegate> {
    @Override
    public void handle(AnnotationValues<Delegate> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.DELEGATE_FLAG_USAGE, "@Delegate");
    }
}
