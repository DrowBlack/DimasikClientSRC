package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.Locked;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.HandleLockedUtil;

@HandlerPriority(value=1024)
public class HandleLockedWrite
extends JavacAnnotationHandler<Locked.Write> {
    private static final String LOCK_METHOD = "writeLock";
    private static final String ANNOTATION_NAME = "@Locked.Write";
    private static final String[] LOCK_TYPE_CLASS = new String[]{"java", "util", "concurrent", "locks", "ReadWriteLock"};
    private static final String[] LOCK_IMPL_CLASS = new String[]{"java", "util", "concurrent", "locks", "ReentrantReadWriteLock"};

    @Override
    public void handle(AnnotationValues<Locked.Write> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        String annotationValue = annotation.getInstance().value();
        HandleLockedUtil.handle(annotationValue, ast, annotationNode, Locked.Write.class, ANNOTATION_NAME, LOCK_TYPE_CLASS, LOCK_IMPL_CLASS, LOCK_METHOD);
    }
}
