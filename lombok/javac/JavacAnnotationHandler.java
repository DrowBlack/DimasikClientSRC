package lombok.javac;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import java.lang.annotation.Annotation;
import lombok.core.AnnotationValues;
import lombok.core.SpiLoadUtil;
import lombok.javac.JavacNode;

public abstract class JavacAnnotationHandler<T extends Annotation> {
    protected Trees trees;

    public abstract void handle(AnnotationValues<T> var1, JCTree.JCAnnotation var2, JavacNode var3);

    public Class<T> getAnnotationHandledByThisHandler() {
        return SpiLoadUtil.findAnnotationClass(this.getClass(), JavacAnnotationHandler.class);
    }

    public void setTrees(Trees trees) {
        this.trees = trees;
    }
}
