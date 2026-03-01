package lombok.eclipse;

import java.util.List;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class EclipseNode
extends LombokNode<EclipseAST, EclipseNode, ASTNode> {
    private EclipseAST ast;

    EclipseNode(EclipseAST ast, ASTNode node, List<EclipseNode> children, AST.Kind kind) {
        super(node, children, kind);
        this.ast = ast;
    }

    @Override
    public EclipseAST getAst() {
        return this.ast;
    }

    public void traverse(EclipseASTVisitor visitor) {
        if (visitor.isDeferUntilPostDiet() && !this.isCompleteParse()) {
            return;
        }
        block0 : switch (this.getKind()) {
            case COMPILATION_UNIT: {
                visitor.visitCompilationUnit(this, (CompilationUnitDeclaration)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitCompilationUnit(this, (CompilationUnitDeclaration)this.get());
                break;
            }
            case TYPE: {
                visitor.visitType(this, (TypeDeclaration)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitType(this, (TypeDeclaration)this.get());
                break;
            }
            case FIELD: {
                visitor.visitField(this, (FieldDeclaration)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitField(this, (FieldDeclaration)this.get());
                break;
            }
            case INITIALIZER: {
                visitor.visitInitializer(this, (Initializer)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitInitializer(this, (Initializer)this.get());
                break;
            }
            case METHOD: {
                if (this.get() instanceof Clinit) {
                    return;
                }
                visitor.visitMethod(this, (AbstractMethodDeclaration)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitMethod(this, (AbstractMethodDeclaration)this.get());
                break;
            }
            case ARGUMENT: {
                AbstractMethodDeclaration method = (AbstractMethodDeclaration)((EclipseNode)this.up()).get();
                visitor.visitMethodArgument(this, (Argument)this.get(), method);
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitMethodArgument(this, (Argument)this.get(), method);
                break;
            }
            case LOCAL: {
                visitor.visitLocal(this, (LocalDeclaration)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitLocal(this, (LocalDeclaration)this.get());
                break;
            }
            case ANNOTATION: {
                switch (((EclipseNode)this.up()).getKind()) {
                    case TYPE: {
                        visitor.visitAnnotationOnType((TypeDeclaration)((EclipseNode)this.up()).get(), this, (Annotation)this.get());
                        break block0;
                    }
                    case FIELD: {
                        visitor.visitAnnotationOnField((FieldDeclaration)((EclipseNode)this.up()).get(), this, (Annotation)this.get());
                        break block0;
                    }
                    case METHOD: {
                        visitor.visitAnnotationOnMethod((AbstractMethodDeclaration)((EclipseNode)this.up()).get(), this, (Annotation)this.get());
                        break block0;
                    }
                    case ARGUMENT: {
                        visitor.visitAnnotationOnMethodArgument((Argument)((EclipseNode)this.parent).get(), (AbstractMethodDeclaration)((EclipseNode)((EclipseNode)this.parent).directUp()).get(), this, (Annotation)this.get());
                        break block0;
                    }
                    case LOCAL: {
                        visitor.visitAnnotationOnLocal((LocalDeclaration)((EclipseNode)this.parent).get(), this, (Annotation)this.get());
                        break block0;
                    }
                    case TYPE_USE: {
                        visitor.visitAnnotationOnTypeUse((TypeReference)((EclipseNode)this.parent).get(), this, (Annotation)this.get());
                        break block0;
                    }
                }
                throw new AssertionError((Object)("Annotation not expected as child of a " + (Object)((Object)((EclipseNode)this.up()).getKind())));
            }
            case TYPE_USE: {
                visitor.visitTypeUse(this, (TypeReference)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitTypeUse(this, (TypeReference)this.get());
                break;
            }
            case STATEMENT: {
                visitor.visitStatement(this, (Statement)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitStatement(this, (Statement)this.get());
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected kind during node traversal: " + (Object)((Object)this.getKind())));
            }
        }
    }

    @Override
    public String getName() {
        Object n = this.node instanceof TypeDeclaration ? ((TypeDeclaration)this.node).name : (this.node instanceof FieldDeclaration ? ((FieldDeclaration)this.node).name : (this.node instanceof AbstractMethodDeclaration ? ((AbstractMethodDeclaration)this.node).selector : (Object)(this.node instanceof LocalDeclaration ? ((LocalDeclaration)this.node).name : null)));
        return n == null ? null : new String((char[])n);
    }

    @Override
    public void addError(String message) {
        this.addError(message, ((ASTNode)this.get()).sourceStart, ((ASTNode)this.get()).sourceEnd);
    }

    public void addError(String message, int sourceStart, int sourceEnd) {
        EclipseAST eclipseAST = this.ast;
        eclipseAST.getClass();
        this.ast.addProblem(new EclipseAST.ParseProblem(eclipseAST, false, message, sourceStart, sourceEnd));
    }

    @Override
    public void addWarning(String message) {
        this.addWarning(message, ((ASTNode)this.get()).sourceStart, ((ASTNode)this.get()).sourceEnd);
    }

    public void addWarning(String message, int sourceStart, int sourceEnd) {
        EclipseAST eclipseAST = this.ast;
        eclipseAST.getClass();
        this.ast.addProblem(new EclipseAST.ParseProblem(eclipseAST, true, message, sourceStart, sourceEnd));
    }

    @Override
    protected boolean calculateIsStructurallySignificant(ASTNode parent) {
        if (this.node instanceof TypeDeclaration) {
            return true;
        }
        if (this.node instanceof AbstractMethodDeclaration) {
            return true;
        }
        if (this.node instanceof FieldDeclaration) {
            return true;
        }
        if (this.node instanceof LocalDeclaration) {
            return true;
        }
        return this.node instanceof CompilationUnitDeclaration;
    }

    public boolean isCompleteParse() {
        return this.ast.isCompleteParse();
    }

    @Override
    public boolean hasAnnotation(Class<? extends java.lang.annotation.Annotation> type) {
        return EclipseHandlerUtil.hasAnnotation(type, this);
    }

    @Override
    public <Z extends java.lang.annotation.Annotation> AnnotationValues<Z> findAnnotation(Class<Z> type) {
        EclipseNode annotation = EclipseHandlerUtil.findAnnotation(type, this);
        if (annotation == null) {
            return null;
        }
        return EclipseHandlerUtil.createAnnotation(type, annotation);
    }

    private Integer getModifiers() {
        if (this.node instanceof TypeDeclaration) {
            return ((TypeDeclaration)this.node).modifiers;
        }
        if (this.node instanceof FieldDeclaration) {
            return ((FieldDeclaration)this.node).modifiers;
        }
        if (this.node instanceof LocalDeclaration) {
            return ((LocalDeclaration)this.node).modifiers;
        }
        if (this.node instanceof AbstractMethodDeclaration) {
            return ((AbstractMethodDeclaration)this.node).modifiers;
        }
        return null;
    }

    @Override
    public boolean isStatic() {
        Integer i;
        EclipseNode directUp;
        if (this.node instanceof TypeDeclaration) {
            TypeDeclaration t = (TypeDeclaration)this.node;
            int f = t.modifiers;
            if ((0x1004200 & f) != 0) {
                return true;
            }
            EclipseNode directUp2 = (EclipseNode)this.directUp();
            if (directUp2 == null || directUp2.getKind() == AST.Kind.COMPILATION_UNIT) {
                return true;
            }
            if (!(directUp2.get() instanceof TypeDeclaration)) {
                return false;
            }
            TypeDeclaration p = (TypeDeclaration)directUp2.get();
            f = p.modifiers;
            if ((0x4200 & f) != 0) {
                return true;
            }
        }
        if (this.node instanceof FieldDeclaration && (directUp = (EclipseNode)this.directUp()) != null && directUp.get() instanceof TypeDeclaration) {
            TypeDeclaration p = (TypeDeclaration)directUp.get();
            int f = p.modifiers;
            if ((0x200 & f) != 0) {
                return true;
            }
        }
        if ((i = this.getModifiers()) == null) {
            return false;
        }
        int f = i;
        return (8 & f) != 0;
    }

    @Override
    public boolean isFinal() {
        Integer i;
        EclipseNode directUp;
        if (this.node instanceof FieldDeclaration && (directUp = (EclipseNode)this.directUp()) != null && directUp.get() instanceof TypeDeclaration) {
            TypeDeclaration p = (TypeDeclaration)directUp.get();
            int f = p.modifiers;
            if ((0x4200 & f) != 0) {
                return true;
            }
        }
        if ((i = this.getModifiers()) == null) {
            return false;
        }
        int f = i;
        return (0x10 & f) != 0;
    }

    @Override
    public boolean isPrimitive() {
        if (this.node instanceof FieldDeclaration && !this.isEnumMember()) {
            return Eclipse.isPrimitive(((FieldDeclaration)this.node).type);
        }
        if (this.node instanceof MethodDeclaration) {
            return Eclipse.isPrimitive(((MethodDeclaration)this.node).returnType);
        }
        return false;
    }

    @Override
    public String fieldOrMethodBaseType() {
        TypeReference typeReference = null;
        if (this.node instanceof FieldDeclaration && !this.isEnumMember()) {
            typeReference = ((FieldDeclaration)this.node).type;
        }
        if (this.node instanceof MethodDeclaration) {
            typeReference = ((MethodDeclaration)this.node).returnType;
        }
        if (typeReference == null) {
            return null;
        }
        String fqn = Eclipse.toQualifiedName(typeReference.getTypeName());
        if (typeReference.dimensions() == 0) {
            return fqn;
        }
        StringBuilder result = new StringBuilder(fqn.length() + 2 * typeReference.dimensions());
        result.append(fqn);
        int i = 0;
        while (i < typeReference.dimensions()) {
            result.append("[]");
            ++i;
        }
        return result.toString();
    }

    @Override
    public boolean isTransient() {
        if (this.getKind() != AST.Kind.FIELD) {
            return false;
        }
        Integer i = this.getModifiers();
        return i != null && (i & 0x80) != 0;
    }

    @Override
    public boolean isEnumMember() {
        if (this.getKind() != AST.Kind.FIELD) {
            return false;
        }
        return ((FieldDeclaration)this.node).getKind() == 3;
    }

    @Override
    public boolean isEnumType() {
        if (this.getKind() != AST.Kind.TYPE) {
            return false;
        }
        return (((TypeDeclaration)this.node).modifiers & 0x4000) != 0;
    }

    @Override
    public int countMethodParameters() {
        if (this.getKind() != AST.Kind.METHOD) {
            return 0;
        }
        Argument[] a = ((AbstractMethodDeclaration)this.node).arguments;
        if (a == null) {
            return 0;
        }
        return a.length;
    }

    @Override
    public int getStartPos() {
        return ((ASTNode)this.node).sourceStart;
    }
}
