package lombok.javac;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.lang.annotation.Annotation;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.javac.Javac;
import lombok.javac.JavacAST;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacTreeMaker;
import lombok.javac.LombokOptions;
import lombok.javac.handlers.JavacHandlerUtil;

public class JavacNode
extends LombokNode<JavacAST, JavacNode, JCTree> {
    private JavacAST ast;

    public JavacNode(JavacAST ast, JCTree node, java.util.List<JavacNode> children, AST.Kind kind) {
        super(node, children, kind);
        this.ast = ast;
    }

    @Override
    public JavacAST getAst() {
        return this.ast;
    }

    public Element getElement() {
        if (this.node instanceof JCTree.JCClassDecl) {
            return ((JCTree.JCClassDecl)this.node).sym;
        }
        if (this.node instanceof JCTree.JCMethodDecl) {
            return ((JCTree.JCMethodDecl)this.node).sym;
        }
        if (this.node instanceof JCTree.JCVariableDecl) {
            return ((JCTree.JCVariableDecl)this.node).sym;
        }
        return null;
    }

    public int getEndPosition(JCDiagnostic.DiagnosticPosition pos) {
        JCTree.JCCompilationUnit cu = (JCTree.JCCompilationUnit)((JavacNode)this.top()).get();
        return Javac.getEndPosition(pos, cu);
    }

    public int getEndPosition() {
        return this.getEndPosition((JCDiagnostic.DiagnosticPosition)this.node);
    }

    public void traverse(JavacASTVisitor visitor) {
        block0 : switch (this.getKind()) {
            case COMPILATION_UNIT: {
                visitor.visitCompilationUnit(this, (JCTree.JCCompilationUnit)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitCompilationUnit(this, (JCTree.JCCompilationUnit)this.get());
                break;
            }
            case TYPE: {
                visitor.visitType(this, (JCTree.JCClassDecl)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitType(this, (JCTree.JCClassDecl)this.get());
                break;
            }
            case FIELD: {
                visitor.visitField(this, (JCTree.JCVariableDecl)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitField(this, (JCTree.JCVariableDecl)this.get());
                break;
            }
            case METHOD: {
                visitor.visitMethod(this, (JCTree.JCMethodDecl)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitMethod(this, (JCTree.JCMethodDecl)this.get());
                break;
            }
            case INITIALIZER: {
                visitor.visitInitializer(this, (JCTree.JCBlock)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitInitializer(this, (JCTree.JCBlock)this.get());
                break;
            }
            case ARGUMENT: {
                JCTree.JCMethodDecl parentMethod = (JCTree.JCMethodDecl)((JavacNode)this.up()).get();
                visitor.visitMethodArgument(this, (JCTree.JCVariableDecl)this.get(), parentMethod);
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitMethodArgument(this, (JCTree.JCVariableDecl)this.get(), parentMethod);
                break;
            }
            case LOCAL: {
                visitor.visitLocal(this, (JCTree.JCVariableDecl)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitLocal(this, (JCTree.JCVariableDecl)this.get());
                break;
            }
            case STATEMENT: {
                visitor.visitStatement(this, (JCTree)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitStatement(this, (JCTree)this.get());
                break;
            }
            case ANNOTATION: {
                switch (((JavacNode)this.up()).getKind()) {
                    case TYPE: {
                        visitor.visitAnnotationOnType((JCTree.JCClassDecl)((JavacNode)this.up()).get(), this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                    case FIELD: {
                        visitor.visitAnnotationOnField((JCTree.JCVariableDecl)((JavacNode)this.up()).get(), this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                    case METHOD: {
                        visitor.visitAnnotationOnMethod((JCTree.JCMethodDecl)((JavacNode)this.up()).get(), this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                    case ARGUMENT: {
                        JCTree.JCVariableDecl argument = (JCTree.JCVariableDecl)((JavacNode)this.up()).get();
                        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl)((JavacNode)((JavacNode)this.up()).up()).get();
                        visitor.visitAnnotationOnMethodArgument(argument, method, this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                    case LOCAL: {
                        visitor.visitAnnotationOnLocal((JCTree.JCVariableDecl)((JavacNode)this.up()).get(), this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                    case TYPE_USE: {
                        visitor.visitAnnotationOnTypeUse((JCTree)((JavacNode)this.up()).get(), this, (JCTree.JCAnnotation)this.get());
                        break block0;
                    }
                }
                throw new AssertionError((Object)("Annotion not expected as child of a " + (Object)((Object)((JavacNode)this.up()).getKind())));
            }
            case TYPE_USE: {
                visitor.visitTypeUse(this, (JCTree)this.get());
                this.ast.traverseChildren(visitor, this);
                visitor.endVisitTypeUse(this, (JCTree)this.get());
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected kind during node traversal: " + (Object)((Object)this.getKind())));
            }
        }
    }

    @Override
    public String getName() {
        Name n = this.node instanceof JCTree.JCClassDecl ? ((JCTree.JCClassDecl)this.node).name : (this.node instanceof JCTree.JCMethodDecl ? ((JCTree.JCMethodDecl)this.node).name : (this.node instanceof JCTree.JCVariableDecl ? ((JCTree.JCVariableDecl)this.node).name : null));
        return n == null ? null : n.toString();
    }

    @Override
    protected boolean calculateIsStructurallySignificant(JCTree parent) {
        if (this.node instanceof JCTree.JCClassDecl) {
            return true;
        }
        if (this.node instanceof JCTree.JCMethodDecl) {
            return true;
        }
        if (this.node instanceof JCTree.JCVariableDecl) {
            return true;
        }
        if (this.node instanceof JCTree.JCCompilationUnit) {
            return true;
        }
        if (this.node instanceof JCTree.JCBlock) {
            return parent instanceof JCTree.JCClassDecl;
        }
        return false;
    }

    public JavacTreeMaker getTreeMaker() {
        return this.ast.getTreeMaker();
    }

    public Symtab getSymbolTable() {
        return this.ast.getSymbolTable();
    }

    public JavacTypes getTypesUtil() {
        return this.ast.getTypesUtil();
    }

    public Context getContext() {
        return this.ast.getContext();
    }

    public boolean shouldDeleteLombokAnnotations() {
        return LombokOptions.shouldDeleteLombokAnnotations(this.ast.getContext());
    }

    public Name toName(String name) {
        return this.ast.toName(name);
    }

    public void removeDeferredErrors() {
        this.ast.removeDeferredErrors(this);
    }

    @Override
    public void addError(String message) {
        this.ast.printMessage(Diagnostic.Kind.ERROR, message, this, null, true);
    }

    public void addError(String message, JCDiagnostic.DiagnosticPosition pos) {
        this.ast.printMessage(Diagnostic.Kind.ERROR, message, null, pos, true);
    }

    @Override
    public void addWarning(String message) {
        this.ast.printMessage(Diagnostic.Kind.WARNING, message, this, null, false);
    }

    public void addWarning(String message, JCDiagnostic.DiagnosticPosition pos) {
        this.ast.printMessage(Diagnostic.Kind.WARNING, message, null, pos, false);
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> type) {
        return JavacHandlerUtil.hasAnnotationAndDeleteIfNeccessary(type, this);
    }

    @Override
    public <Z extends Annotation> AnnotationValues<Z> findAnnotation(Class<Z> type) {
        JavacNode annotation = JavacHandlerUtil.findAnnotation(type, this, true);
        if (annotation == null) {
            return null;
        }
        return JavacHandlerUtil.createAnnotation(type, annotation);
    }

    private JCTree.JCModifiers getModifiers() {
        if (this.node instanceof JCTree.JCClassDecl) {
            return ((JCTree.JCClassDecl)this.node).getModifiers();
        }
        if (this.node instanceof JCTree.JCMethodDecl) {
            return ((JCTree.JCMethodDecl)this.node).getModifiers();
        }
        if (this.node instanceof JCTree.JCVariableDecl) {
            return ((JCTree.JCVariableDecl)this.node).getModifiers();
        }
        return null;
    }

    @Override
    public boolean isStatic() {
        JCTree.JCModifiers mods;
        JavacNode directUp;
        if (this.node instanceof JCTree.JCClassDecl) {
            JCTree.JCClassDecl t = (JCTree.JCClassDecl)this.node;
            long f = t.mods.flags;
            if ((0x2000000000004200L & f) != 0L) {
                return true;
            }
            JavacNode directUp2 = (JavacNode)this.directUp();
            if (directUp2 == null || directUp2.getKind() == AST.Kind.COMPILATION_UNIT) {
                return true;
            }
            if (!(directUp2.get() instanceof JCTree.JCClassDecl)) {
                return false;
            }
            JCTree.JCClassDecl p = (JCTree.JCClassDecl)directUp2.get();
            f = p.mods.flags;
            if ((0x4200L & f) != 0L) {
                return true;
            }
        }
        if (this.node instanceof JCTree.JCVariableDecl && (directUp = (JavacNode)this.directUp()) != null && directUp.get() instanceof JCTree.JCClassDecl) {
            JCTree.JCClassDecl p = (JCTree.JCClassDecl)directUp.get();
            long f = p.mods.flags;
            if ((0x200L & f) != 0L) {
                return true;
            }
        }
        if ((mods = this.getModifiers()) == null) {
            return false;
        }
        return (mods.flags & 8L) != 0L;
    }

    @Override
    public boolean isFinal() {
        JCTree.JCModifiers mods;
        JavacNode directUp;
        if (this.node instanceof JCTree.JCVariableDecl && (directUp = (JavacNode)this.directUp()) != null && directUp.get() instanceof JCTree.JCClassDecl) {
            JCTree.JCClassDecl p = (JCTree.JCClassDecl)directUp.get();
            long f = p.mods.flags;
            if ((0x4200L & f) != 0L) {
                return true;
            }
        }
        return (mods = this.getModifiers()) != null && (0x10L & mods.flags) != 0L;
    }

    @Override
    public boolean isEnumMember() {
        if (this.getKind() != AST.Kind.FIELD) {
            return false;
        }
        JCTree.JCModifiers mods = this.getModifiers();
        return mods != null && (0x4000L & mods.flags) != 0L;
    }

    @Override
    public boolean isEnumType() {
        if (this.getKind() != AST.Kind.TYPE) {
            return false;
        }
        JCTree.JCModifiers mods = this.getModifiers();
        return mods != null && (0x4000L & mods.flags) != 0L;
    }

    @Override
    public boolean isPrimitive() {
        if (this.node instanceof JCTree.JCVariableDecl && !this.isEnumMember()) {
            return Javac.isPrimitive(((JCTree.JCVariableDecl)this.node).vartype);
        }
        if (this.node instanceof JCTree.JCMethodDecl) {
            return Javac.isPrimitive(((JCTree.JCMethodDecl)this.node).restype);
        }
        return false;
    }

    @Override
    public String fieldOrMethodBaseType() {
        if (this.node instanceof JCTree.JCVariableDecl && !this.isEnumMember()) {
            return ((JCTree.JCVariableDecl)this.node).vartype.toString();
        }
        if (this.node instanceof JCTree.JCMethodDecl) {
            return ((JCTree.JCMethodDecl)this.node).restype.toString();
        }
        return null;
    }

    @Override
    public boolean isTransient() {
        if (this.getKind() != AST.Kind.FIELD) {
            return false;
        }
        JCTree.JCModifiers mods = this.getModifiers();
        return mods != null && (0x80L & mods.flags) != 0L;
    }

    @Override
    public int countMethodParameters() {
        if (this.getKind() != AST.Kind.METHOD) {
            return 0;
        }
        List<JCTree.JCVariableDecl> params = ((JCTree.JCMethodDecl)this.node).params;
        if (params == null) {
            return 0;
        }
        return params.size();
    }

    @Override
    public int getStartPos() {
        return ((JCTree)this.node).getPreferredPosition();
    }
}
