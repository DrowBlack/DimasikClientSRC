package lombok.eclipse.handlers;

import lombok.Locked;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EcjAugments;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.HandleLockedUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

@DeferUntilPostDiet
@HandlerPriority(value=1024)
public class HandleLockedWrite
extends EclipseAnnotationHandler<Locked.Write> {
    private static final char[] LOCK_METHOD = "writeLock".toCharArray();
    private static final String ANNOTATION_NAME = "@Locked.Write";
    private static final char[][] LOCK_TYPE_CLASS = new char[][]{TypeConstants.JAVA, TypeConstants.UTIL, "concurrent".toCharArray(), "locks".toCharArray(), "ReadWriteLock".toCharArray()};
    private static final char[][] LOCK_IMPL_CLASS = new char[][]{TypeConstants.JAVA, TypeConstants.UTIL, "concurrent".toCharArray(), "locks".toCharArray(), "ReentrantReadWriteLock".toCharArray()};

    @Override
    public void handle(AnnotationValues<Locked.Write> annotation, Annotation source, EclipseNode annotationNode) {
        String annotationValue = annotation.getInstance().value();
        HandleLockedUtil.handle(annotationValue, source, annotationNode, ANNOTATION_NAME, LOCK_TYPE_CLASS, LOCK_IMPL_CLASS, LOCK_METHOD);
    }

    @Override
    public void preHandle(AnnotationValues<Locked.Write> annotation, Annotation source, EclipseNode annotationNode) {
        String annotationValue = annotation.getInstance().value();
        HandleLockedUtil.preHandle(annotationValue, LOCK_TYPE_CLASS, LOCK_IMPL_CLASS, annotationNode);
        if (EclipseHandlerUtil.hasParsedBody(EclipseHandlerUtil.getAnnotatedMethod(annotationNode))) {
            this.handle(annotation, source, annotationNode);
            EcjAugments.ASTNode_handled.set((ASTNode)source, true);
        }
    }
}
