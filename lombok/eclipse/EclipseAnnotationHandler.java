package lombok.eclipse;

import lombok.core.AnnotationValues;
import lombok.core.SpiLoadUtil;
import lombok.eclipse.EclipseNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public abstract class EclipseAnnotationHandler<T extends java.lang.annotation.Annotation> {
    public abstract void handle(AnnotationValues<T> var1, Annotation var2, EclipseNode var3);

    public void preHandle(AnnotationValues<T> annotation, Annotation ast, EclipseNode annotationNode) {
    }

    public Class<T> getAnnotationHandledByThisHandler() {
        return SpiLoadUtil.findAnnotationClass(this.getClass(), EclipseAnnotationHandler.class);
    }
}
