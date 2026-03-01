package lombok.javac;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import java.util.List;
import java.util.SortedSet;
import javax.annotation.processing.Messager;
import lombok.ConfigurationKeys;
import lombok.core.CleanupRegistry;
import lombok.core.LombokConfiguration;
import lombok.javac.HandlerLibrary;
import lombok.javac.JavacAST;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacNode;
import lombok.javac.LombokOptions;

public class JavacTransformer {
    private final HandlerLibrary handlers;
    private final Messager messager;

    public JavacTransformer(Messager messager, Trees trees) {
        this.messager = messager;
        this.handlers = HandlerLibrary.load(messager, trees);
    }

    public SortedSet<Long> getPriorities() {
        return this.handlers.getPriorities();
    }

    public SortedSet<Long> getPrioritiesRequiringResolutionReset() {
        return this.handlers.getPrioritiesRequiringResolutionReset();
    }

    public void transform(long priority, Context context, List<JCTree.JCCompilationUnit> compilationUnits, CleanupRegistry cleanup) {
        if (compilationUnits.isEmpty()) {
            return;
        }
        JavacAST.ErrorLog errorLog = JavacAST.ErrorLog.create(this.messager, context);
        for (JCTree.JCCompilationUnit unit : compilationUnits) {
            if (Boolean.TRUE.equals(LombokConfiguration.read(ConfigurationKeys.LOMBOK_DISABLE, JavacAST.getAbsoluteFileLocation(unit)))) continue;
            JavacAST ast = new JavacAST(errorLog, context, unit, cleanup);
            ast.traverse(new AnnotationVisitor(priority));
            this.handlers.callASTVisitors(ast, priority);
            if (!ast.isChanged()) continue;
            LombokOptions.markChanged(context, (JCTree.JCCompilationUnit)((JavacNode)ast.top()).get());
        }
    }

    private class AnnotationVisitor
    extends JavacASTAdapter {
        private final long priority;

        AnnotationVisitor(long priority) {
            this.priority = priority;
        }

        @Override
        public void visitAnnotationOnType(JCTree.JCClassDecl type, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation, this.priority);
        }

        @Override
        public void visitAnnotationOnField(JCTree.JCVariableDecl field, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation, this.priority);
        }

        @Override
        public void visitAnnotationOnMethod(JCTree.JCMethodDecl method, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation, this.priority);
        }

        @Override
        public void visitAnnotationOnMethodArgument(JCTree.JCVariableDecl argument, JCTree.JCMethodDecl method, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation, this.priority);
        }

        @Override
        public void visitAnnotationOnLocal(JCTree.JCVariableDecl local, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation, this.priority);
        }

        @Override
        public void visitAnnotationOnTypeUse(JCTree typeUse, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
            JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)annotationNode.top()).get();
            JavacTransformer.this.handlers.handleAnnotation(top, annotationNode, annotation, this.priority);
        }
    }
}
