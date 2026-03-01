package lombok.javac;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacNode;

public class JavacASTAdapter
implements JavacASTVisitor {
    @Override
    public void setTrees(Trees trees) {
    }

    @Override
    public void visitCompilationUnit(JavacNode top, JCTree.JCCompilationUnit unit) {
    }

    @Override
    public void endVisitCompilationUnit(JavacNode top, JCTree.JCCompilationUnit unit) {
    }

    @Override
    public void visitType(JavacNode typeNode, JCTree.JCClassDecl type) {
    }

    @Override
    public void visitAnnotationOnType(JCTree.JCClassDecl type, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
    }

    @Override
    public void endVisitType(JavacNode typeNode, JCTree.JCClassDecl type) {
    }

    @Override
    public void visitField(JavacNode fieldNode, JCTree.JCVariableDecl field) {
    }

    @Override
    public void visitAnnotationOnField(JCTree.JCVariableDecl field, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
    }

    @Override
    public void endVisitField(JavacNode fieldNode, JCTree.JCVariableDecl field) {
    }

    @Override
    public void visitInitializer(JavacNode initializerNode, JCTree.JCBlock initializer) {
    }

    @Override
    public void endVisitInitializer(JavacNode initializerNode, JCTree.JCBlock initializer) {
    }

    @Override
    public void visitMethod(JavacNode methodNode, JCTree.JCMethodDecl method) {
    }

    @Override
    public void visitAnnotationOnMethod(JCTree.JCMethodDecl method, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
    }

    @Override
    public void endVisitMethod(JavacNode methodNode, JCTree.JCMethodDecl method) {
    }

    @Override
    public void visitMethodArgument(JavacNode argumentNode, JCTree.JCVariableDecl argument, JCTree.JCMethodDecl method) {
    }

    @Override
    public void visitAnnotationOnMethodArgument(JCTree.JCVariableDecl argument, JCTree.JCMethodDecl method, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
    }

    @Override
    public void endVisitMethodArgument(JavacNode argumentNode, JCTree.JCVariableDecl argument, JCTree.JCMethodDecl method) {
    }

    @Override
    public void visitLocal(JavacNode localNode, JCTree.JCVariableDecl local) {
    }

    @Override
    public void visitAnnotationOnLocal(JCTree.JCVariableDecl local, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
    }

    @Override
    public void endVisitLocal(JavacNode localNode, JCTree.JCVariableDecl local) {
    }

    @Override
    public void visitTypeUse(JavacNode typeUseNode, JCTree typeUse) {
    }

    @Override
    public void visitAnnotationOnTypeUse(JCTree typeUse, JavacNode annotationNode, JCTree.JCAnnotation annotation) {
    }

    @Override
    public void endVisitTypeUse(JavacNode typeUseNode, JCTree typeUse) {
    }

    @Override
    public void visitStatement(JavacNode statementNode, JCTree statement) {
    }

    @Override
    public void endVisitStatement(JavacNode statementNode, JCTree statement) {
    }
}
