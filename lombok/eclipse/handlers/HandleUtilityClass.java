package lombok.eclipse.handlers;

import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import lombok.experimental.UtilityClass;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

@HandlerPriority(value=-4096)
public class HandleUtilityClass
extends EclipseAnnotationHandler<UtilityClass> {
    private static final char[][] JAVA_LANG_UNSUPPORTED_OPERATION_EXCEPTION = new char[][]{TypeConstants.JAVA, TypeConstants.LANG, "UnsupportedOperationException".toCharArray()};
    private static final char[] UNSUPPORTED_MESSAGE = "This is a utility class and cannot be instantiated".toCharArray();

    @Override
    public void handle(AnnotationValues<UtilityClass> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.UTILITY_CLASS_FLAG_USAGE, "@UtilityClass");
        EclipseNode typeNode = (EclipseNode)annotationNode.up();
        if (!HandleUtilityClass.checkLegality(typeNode, annotationNode)) {
            return;
        }
        this.changeModifiersAndGenerateConstructor((EclipseNode)annotationNode.up(), annotationNode);
    }

    private static boolean checkLegality(EclipseNode typeNode, EclipseNode errorNode) {
        if (!EclipseHandlerUtil.isClass(typeNode)) {
            errorNode.addError("@UtilityClass is only supported on a class.");
            return false;
        }
        EclipseNode typeWalk = typeNode;
        block4: while (true) {
            typeWalk = (EclipseNode)typeWalk.up();
            switch (typeWalk.getKind()) {
                case TYPE: {
                    if ((((TypeDeclaration)typeWalk.get()).modifiers & 0x6208) != 0) continue block4;
                    if (((EclipseNode)typeWalk.up()).getKind() == AST.Kind.COMPILATION_UNIT) {
                        return true;
                    }
                    errorNode.addError("@UtilityClass automatically makes the class static, however, this class cannot be made static.");
                    return false;
                }
                case COMPILATION_UNIT: {
                    return true;
                }
            }
            break;
        }
        errorNode.addError("@UtilityClass cannot be placed on a method local or anonymous inner class, or any class nested in such a class.");
        return false;
    }

    private void changeModifiersAndGenerateConstructor(EclipseNode typeNode, EclipseNode annotationNode) {
        TypeDeclaration classDecl = (TypeDeclaration)typeNode.get();
        boolean makeConstructor = true;
        classDecl.modifiers |= 0x10;
        boolean markStatic = true;
        boolean requiresClInit = false;
        boolean alreadyHasClinit = false;
        if (((EclipseNode)typeNode.up()).getKind() == AST.Kind.COMPILATION_UNIT) {
            markStatic = false;
        }
        if (markStatic && ((EclipseNode)typeNode.up()).getKind() == AST.Kind.TYPE) {
            TypeDeclaration typeDecl = (TypeDeclaration)((EclipseNode)typeNode.up()).get();
            if ((typeDecl.modifiers & 0x2200) != 0) {
                markStatic = false;
            }
        }
        if (markStatic) {
            classDecl.modifiers |= 8;
        }
        for (EclipseNode element : typeNode.down()) {
            if (element.getKind() == AST.Kind.FIELD) {
                FieldDeclaration fieldDecl = (FieldDeclaration)element.get();
                if ((fieldDecl.modifiers & 8) != 0) continue;
                requiresClInit = true;
                fieldDecl.modifiers |= 8;
                continue;
            }
            if (element.getKind() == AST.Kind.METHOD) {
                AbstractMethodDeclaration amd = (AbstractMethodDeclaration)element.get();
                if (amd instanceof ConstructorDeclaration) {
                    ConstructorDeclaration constrDecl = (ConstructorDeclaration)element.get();
                    if (EclipseHandlerUtil.getGeneratedBy((ASTNode)constrDecl) != null || (constrDecl.bits & 0x80) != 0) continue;
                    element.addError("@UtilityClasses cannot have declared constructors.");
                    makeConstructor = false;
                    continue;
                }
                if (amd instanceof MethodDeclaration) {
                    amd.modifiers |= 8;
                    continue;
                }
                if (!(amd instanceof Clinit)) continue;
                alreadyHasClinit = true;
                continue;
            }
            if (element.getKind() != AST.Kind.TYPE) continue;
            ((TypeDeclaration)element.get()).modifiers |= 8;
        }
        if (makeConstructor) {
            this.createPrivateDefaultConstructor(typeNode, annotationNode);
        }
        if (requiresClInit && !alreadyHasClinit) {
            classDecl.addClinit();
        }
    }

    private void createPrivateDefaultConstructor(EclipseNode typeNode, EclipseNode sourceNode) {
        ASTNode source = (ASTNode)sourceNode.get();
        TypeDeclaration typeDeclaration = (TypeDeclaration)typeNode.get();
        ConstructorDeclaration constructor = new ConstructorDeclaration(((CompilationUnitDeclaration)((EclipseNode)typeNode.top()).get()).compilationResult);
        constructor.modifiers = 2;
        constructor.selector = typeDeclaration.name;
        constructor.constructorCall = new ExplicitConstructorCall(1);
        constructor.thrownExceptions = null;
        constructor.typeParameters = null;
        constructor.bits |= 0x800000;
        constructor.arguments = null;
        long[] ps = new long[JAVA_LANG_UNSUPPORTED_OPERATION_EXCEPTION.length];
        AllocationExpression exception = new AllocationExpression();
        exception.type = new QualifiedTypeReference(JAVA_LANG_UNSUPPORTED_OPERATION_EXCEPTION, ps);
        exception.arguments = new Expression[]{new StringLiteral(UNSUPPORTED_MESSAGE, 0, 0, 0)};
        ThrowStatement throwStatement = new ThrowStatement((Expression)exception, 0, 0);
        constructor.statements = new Statement[]{throwStatement};
        constructor.traverse((ASTVisitor)new SetGeneratedByVisitor(source), typeDeclaration.scope);
        EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)constructor);
    }
}
