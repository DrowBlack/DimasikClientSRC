package lombok.eclipse.handlers;

import java.util.Arrays;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExtendedStringLiteral;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocArraySingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocImplicitTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend;
import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;

public final class SetGeneratedByVisitor
extends ASTVisitor {
    private static final long INT_TO_LONG_MASK = 0xFFFFFFFFL;
    private final ASTNode source;
    private final int sourceStart;
    private final int sourceEnd;
    private final long sourcePos;

    public SetGeneratedByVisitor(ASTNode source) {
        this.source = source;
        this.sourceStart = this.source.sourceStart;
        this.sourceEnd = this.source.sourceEnd;
        this.sourcePos = (long)this.sourceStart << 32 | (long)this.sourceEnd & 0xFFFFFFFFL;
    }

    private void fixPositions(JavadocAllocationExpression node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        node.memberStart = this.sourceStart;
        node.tagSourceEnd = this.sourceEnd;
        node.tagSourceStart = this.sourceStart;
    }

    private void fixPositions(JavadocMessageSend node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        node.nameSourcePosition = this.sourcePos;
        node.tagSourceEnd = this.sourceEnd;
        node.tagSourceStart = this.sourceStart;
    }

    private void fixPositions(JavadocSingleNameReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        node.tagSourceEnd = this.sourceEnd;
        node.tagSourceStart = this.sourceStart;
    }

    private void fixPositions(JavadocSingleTypeReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        node.tagSourceEnd = this.sourceEnd;
        node.tagSourceStart = this.sourceStart;
    }

    private void fixPositions(JavadocFieldReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        node.nameSourcePosition = this.sourcePos;
        node.tagSourceEnd = this.sourceEnd;
        node.tagSourceStart = this.sourceStart;
    }

    private void fixPositions(JavadocArrayQualifiedTypeReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        if (node.sourcePositions == null || node.sourcePositions.length != node.tokens.length) {
            node.sourcePositions = new long[node.tokens.length];
        }
        Arrays.fill(node.sourcePositions, this.sourcePos);
        node.tagSourceEnd = this.sourceEnd;
        node.tagSourceStart = this.sourceStart;
    }

    private void fixPositions(JavadocQualifiedTypeReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        if (node.sourcePositions == null || node.sourcePositions.length != node.tokens.length) {
            node.sourcePositions = new long[node.tokens.length];
        }
        Arrays.fill(node.sourcePositions, this.sourcePos);
        node.tagSourceEnd = this.sourceEnd;
        node.tagSourceStart = this.sourceStart;
    }

    private void fixPositions(Annotation node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        node.declarationSourceEnd = this.sourceEnd;
    }

    private void fixPositions(ArrayTypeReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        node.originalSourceEnd = this.sourceEnd;
    }

    private void fixPositions(AbstractMethodDeclaration node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.bodyEnd = this.sourceEnd;
        node.bodyStart = this.sourceStart;
        node.declarationSourceEnd = this.sourceEnd;
        node.declarationSourceStart = this.sourceStart;
        node.modifiersSourceStart = this.sourceStart;
    }

    private void fixPositions(Javadoc node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.valuePositions = this.sourceStart;
    }

    private void fixPositions(Initializer node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.declarationEnd = this.sourceEnd;
        node.declarationSourceEnd = this.sourceEnd;
        node.declarationSourceStart = this.sourceStart;
        node.modifiersSourceStart = this.sourceStart;
        node.endPart1Position = this.sourceEnd;
        node.endPart2Position = this.sourceEnd;
        node.bodyStart = this.sourceStart;
        node.bodyEnd = this.sourceEnd;
    }

    private void fixPositions(TypeDeclaration node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.bodyEnd = this.sourceEnd;
        node.bodyStart = this.sourceStart;
        node.declarationSourceEnd = this.sourceEnd;
        node.declarationSourceStart = this.sourceStart;
        node.modifiersSourceStart = this.sourceStart;
    }

    private void fixPositions(ImportReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.declarationEnd = this.sourceEnd;
        node.declarationSourceEnd = this.sourceEnd;
        node.declarationSourceStart = this.sourceStart;
        if (node.sourcePositions == null || node.sourcePositions.length != node.tokens.length) {
            node.sourcePositions = new long[node.tokens.length];
        }
        Arrays.fill(node.sourcePositions, this.sourcePos);
    }

    private void fixPositions(ASTNode node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
    }

    private void fixPositions(SwitchStatement node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.blockStart = this.sourceStart;
    }

    private void fixPositions(Expression node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
    }

    private void fixPositions(AbstractVariableDeclaration node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.declarationEnd = this.sourceEnd;
        node.declarationSourceEnd = this.sourceEnd;
        node.declarationSourceStart = this.sourceStart;
        node.modifiersSourceStart = this.sourceStart;
    }

    private void fixPositions(FieldDeclaration node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.declarationEnd = this.sourceEnd;
        node.declarationSourceEnd = this.sourceEnd;
        node.declarationSourceStart = this.sourceStart;
        node.modifiersSourceStart = this.sourceStart;
        node.endPart1Position = this.sourceEnd;
        node.endPart2Position = this.sourceEnd;
    }

    private void fixPositions(FieldReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        node.nameSourcePosition = this.sourcePos;
    }

    private void fixPositions(MessageSend node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        node.nameSourcePosition = this.sourcePos;
    }

    private void fixPositions(QualifiedNameReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        if (node.sourcePositions == null || node.sourcePositions.length != node.tokens.length) {
            node.sourcePositions = new long[node.tokens.length];
        }
        Arrays.fill(node.sourcePositions, this.sourcePos);
    }

    private void fixPositions(QualifiedTypeReference node) {
        node.sourceEnd = this.sourceEnd;
        node.sourceStart = this.sourceStart;
        node.statementEnd = this.sourceEnd;
        if (node.sourcePositions == null || node.sourcePositions.length != node.tokens.length) {
            node.sourcePositions = new long[node.tokens.length];
        }
        Arrays.fill(node.sourcePositions, this.sourcePos);
    }

    public boolean visit(AllocationExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(AND_AND_Expression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(AnnotationMethodDeclaration node, ClassScope classScope) {
        this.fixPositions((AbstractMethodDeclaration)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, classScope);
    }

    public boolean visit(Argument node, BlockScope scope) {
        this.fixPositions((AbstractVariableDeclaration)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(Argument node, ClassScope scope) {
        this.fixPositions((AbstractVariableDeclaration)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ArrayAllocationExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ArrayInitializer node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ArrayQualifiedTypeReference node, BlockScope scope) {
        this.fixPositions((QualifiedTypeReference)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ArrayQualifiedTypeReference node, ClassScope scope) {
        this.fixPositions((QualifiedTypeReference)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ArrayReference node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ArrayTypeReference node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ArrayTypeReference node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(AssertStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(Assignment node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(BinaryExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(Block node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(BreakStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(CaseStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(CastExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(CharLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ClassLiteralAccess node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(Clinit node, ClassScope scope) {
        this.fixPositions((AbstractMethodDeclaration)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(CompilationUnitDeclaration node, CompilationUnitScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(CompoundAssignment node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ConditionalExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ConstructorDeclaration node, ClassScope scope) {
        this.fixPositions((AbstractMethodDeclaration)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ContinueStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(DoStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(DoubleLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(EmptyStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(EqualExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ExplicitConstructorCall node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ExtendedStringLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(FalseLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(FieldDeclaration node, MethodScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(FieldReference node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(FieldReference node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(FloatLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ForeachStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ForStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(IfStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ImportReference node, CompilationUnitScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(Initializer node, MethodScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(InstanceOfExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(IntLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(Javadoc node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(Javadoc node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocAllocationExpression node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocAllocationExpression node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArgumentExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArgumentExpression node, ClassScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArrayQualifiedTypeReference node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArrayQualifiedTypeReference node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArraySingleTypeReference node, BlockScope scope) {
        this.fixPositions((ArrayTypeReference)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocArraySingleTypeReference node, ClassScope scope) {
        this.fixPositions((ArrayTypeReference)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocFieldReference node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocFieldReference node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocImplicitTypeReference node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocImplicitTypeReference node, ClassScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocMessageSend node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocMessageSend node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocQualifiedTypeReference node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocQualifiedTypeReference node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocReturnStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocReturnStatement node, ClassScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocSingleNameReference node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocSingleNameReference node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocSingleTypeReference node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(JavadocSingleTypeReference node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(LabeledStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(LocalDeclaration node, BlockScope scope) {
        this.fixPositions((AbstractVariableDeclaration)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(LongLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(MarkerAnnotation node, BlockScope scope) {
        this.fixPositions((Annotation)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(MemberValuePair node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(MessageSend node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(MethodDeclaration node, ClassScope scope) {
        this.fixPositions((AbstractMethodDeclaration)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(StringLiteralConcatenation node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(NormalAnnotation node, BlockScope scope) {
        this.fixPositions((Annotation)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(NullLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(OR_OR_Expression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ParameterizedQualifiedTypeReference node, BlockScope scope) {
        this.fixPositions((QualifiedTypeReference)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ParameterizedQualifiedTypeReference node, ClassScope scope) {
        this.fixPositions((QualifiedTypeReference)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ParameterizedSingleTypeReference node, BlockScope scope) {
        this.fixPositions((ArrayTypeReference)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ParameterizedSingleTypeReference node, ClassScope scope) {
        this.fixPositions((ArrayTypeReference)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(PostfixExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(PrefixExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedAllocationExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedNameReference node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedNameReference node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedSuperReference node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedSuperReference node, ClassScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedThisReference node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedThisReference node, ClassScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedTypeReference node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(QualifiedTypeReference node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ReturnStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(SingleMemberAnnotation node, BlockScope scope) {
        this.fixPositions((Annotation)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(SingleNameReference node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(SingleNameReference node, ClassScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(SingleTypeReference node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(SingleTypeReference node, ClassScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(StringLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(SuperReference node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(SwitchStatement node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(SynchronizedStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ThisReference node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ThisReference node, ClassScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(ThrowStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(TrueLiteral node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(TryStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(TypeDeclaration node, BlockScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(TypeDeclaration node, ClassScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(TypeDeclaration node, CompilationUnitScope scope) {
        this.fixPositions(EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(TypeParameter node, BlockScope scope) {
        this.fixPositions((AbstractVariableDeclaration)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(TypeParameter node, ClassScope scope) {
        this.fixPositions((AbstractVariableDeclaration)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(UnaryExpression node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(WhileStatement node, BlockScope scope) {
        this.fixPositions((ASTNode)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(Wildcard node, BlockScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }

    public boolean visit(Wildcard node, ClassScope scope) {
        this.fixPositions((Expression)EclipseHandlerUtil.setGeneratedBy(node, this.source));
        return super.visit(node, scope);
    }
}
