package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.Locked;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.HandleLockedUtil;

@HandlerPriority(value=1024)
public class HandleLocked
extends JavacAnnotationHandler<Locked> {
    private static final String ANNOTATION_NAME = "@Locked";
    private static final String[] LOCK_TYPE_CLASS = new String[]{"java", "util", "concurrent", "locks", "Lock"};
    private static final String[] LOCK_IMPL_CLASS = new String[]{"java", "util", "concurrent", "locks", "ReentrantLock"};

    @Override
    public void handle(AnnotationValues<Locked> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        String annotationValue = annotation.getInstance().value();
        HandleLockedUtil.handle(annotationValue, ast, annotationNode, Locked.class, ANNOTATION_NAME, LOCK_TYPE_CLASS, LOCK_IMPL_CLASS);
    }
}
