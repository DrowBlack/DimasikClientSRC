package lombok.eclipse;

import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public abstract class EclipseASTAdapter
implements EclipseASTVisitor {
    private final boolean deferUntilPostDiet = this.getClass().isAnnotationPresent(DeferUntilPostDiet.class);

    @Override
    public void visitCompilationUnit(EclipseNode top, CompilationUnitDeclaration unit) {
    }

    @Override
    public void endVisitCompilationUnit(EclipseNode top, CompilationUnitDeclaration unit) {
    }

    @Override
    public void visitType(EclipseNode typeNode, TypeDeclaration type) {
    }

    @Override
    public void visitAnnotationOnType(TypeDeclaration type, EclipseNode annotationNode, Annotation annotation) {
    }

    @Override
    public void endVisitType(EclipseNode typeNode, TypeDeclaration type) {
    }

    @Override
    public void visitInitializer(EclipseNode initializerNode, Initializer initializer) {
    }

    @Override
    public void endVisitInitializer(EclipseNode initializerNode, Initializer initializer) {
    }

    @Override
    public void visitField(EclipseNode fieldNode, FieldDeclaration field) {
    }

    @Override
    public void visitAnnotationOnField(FieldDeclaration field, EclipseNode annotationNode, Annotation annotation) {
    }

    @Override
    public void endVisitField(EclipseNode fieldNode, FieldDeclaration field) {
    }

    @Override
    public void visitMethod(EclipseNode methodNode, AbstractMethodDeclaration method) {
    }

    @Override
    public void visitAnnotationOnMethod(AbstractMethodDeclaration method, EclipseNode annotationNode, Annotation annotation) {
    }

    @Override
    public void endVisitMethod(EclipseNode methodNode, AbstractMethodDeclaration method) {
    }

    @Override
    public void visitMethodArgument(EclipseNode argNode, Argument arg, AbstractMethodDeclaration method) {
    }

    @Override
    public void visitAnnotationOnMethodArgument(Argument arg, AbstractMethodDeclaration method, EclipseNode annotationNode, Annotation annotation) {
    }

    @Override
    public void endVisitMethodArgument(EclipseNode argNode, Argument arg, AbstractMethodDeclaration method) {
    }

    @Override
    public void visitLocal(EclipseNode localNode, LocalDeclaration local) {
    }

    @Override
    public void visitAnnotationOnLocal(LocalDeclaration local, EclipseNode annotationNode, Annotation annotation) {
    }

    @Override
    public void endVisitLocal(EclipseNode localNode, LocalDeclaration local) {
    }

    @Override
    public void visitTypeUse(EclipseNode typeUseNode, TypeReference typeUse) {
    }

    @Override
    public void visitAnnotationOnTypeUse(TypeReference typeUse, EclipseNode annotationNode, Annotation annotation) {
    }

    @Override
    public void endVisitTypeUse(EclipseNode typeUseNode, TypeReference typeUse) {
    }

    @Override
    public void visitStatement(EclipseNode statementNode, Statement statement) {
    }

    @Override
    public void endVisitStatement(EclipseNode statementNode, Statement statement) {
    }

    @Override
    public boolean isDeferUntilPostDiet() {
        return this.deferUntilPostDiet;
    }
}
