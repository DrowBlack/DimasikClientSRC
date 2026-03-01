package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.ConfigurationKeys;
import lombok.NonNull;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EcjAugments;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

@HandlerPriority(value=512)
public class HandleNonNull
extends EclipseAnnotationHandler<NonNull> {
    private static final char[] REQUIRE_NON_NULL = "requireNonNull".toCharArray();
    private static final char[] CHECK_NOT_NULL = "checkNotNull".toCharArray();
    public static final HandleNonNull INSTANCE = new HandleNonNull();

    public void fix(EclipseNode method) {
        for (EclipseNode m : method.down()) {
            if (m.getKind() != AST.Kind.ARGUMENT) continue;
            for (EclipseNode c : m.down()) {
                if (c.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(NonNull.class, c)) continue;
                this.handle0((Annotation)c.get(), c, true);
            }
        }
    }

    private List<FieldDeclaration> getRecordComponents(EclipseNode typeNode) {
        ArrayList<FieldDeclaration> list = new ArrayList<FieldDeclaration>();
        for (EclipseNode child : typeNode.down()) {
            if (child.getKind() != AST.Kind.FIELD) continue;
            FieldDeclaration fd = (FieldDeclaration)child.get();
            if ((fd.modifiers & 0x1000000) == 0) continue;
            list.add(fd);
        }
        return list;
    }

    private EclipseNode addCompactConstructorIfNeeded(EclipseNode typeNode, EclipseNode annotationNode) {
        int i;
        ConstructorDeclaration cd;
        EclipseNode toRemove = null;
        EclipseNode existingCompactConstructor = null;
        List<FieldDeclaration> recordComponents = null;
        for (EclipseNode child : typeNode.down()) {
            boolean isCanonical;
            if (!(child.get() instanceof ConstructorDeclaration)) continue;
            cd = (ConstructorDeclaration)child.get();
            if ((cd.bits & 0x200) != 0) {
                if ((cd.bits & 0x400) != 0) {
                    toRemove = child;
                    continue;
                }
                existingCompactConstructor = child;
                continue;
            }
            if (recordComponents == null) {
                recordComponents = this.getRecordComponents(typeNode);
            }
            int argLength = cd.arguments == null ? 0 : cd.arguments.length;
            int compLength = recordComponents.size();
            boolean bl = isCanonical = argLength == compLength;
            if (isCanonical) {
                int i2 = 0;
                block1: while (i2 < argLength) {
                    TypeReference a = recordComponents.get((int)i2).type;
                    TypeReference b = cd.arguments[i2] == null ? null : cd.arguments[i2].type;
                    char[][] ta = HandleNonNull.getRawTypeName(a);
                    char[][] tb = HandleNonNull.getRawTypeName(b);
                    if (ta == null || tb == null || ta.length != tb.length) {
                        isCanonical = false;
                        break;
                    }
                    int j = 0;
                    while (j < ta.length) {
                        if (!Arrays.equals(ta[j], tb[j])) {
                            isCanonical = false;
                            break block1;
                        }
                        ++j;
                    }
                    ++i2;
                }
            }
            if (!isCanonical) continue;
            return null;
        }
        if (existingCompactConstructor != null) {
            return existingCompactConstructor;
        }
        int posToInsert = -1;
        TypeDeclaration td = (TypeDeclaration)typeNode.get();
        if (toRemove != null) {
            int idxToRemove = -1;
            i = 0;
            while (i < td.methods.length) {
                if (td.methods[i] == toRemove.get()) {
                    idxToRemove = i;
                }
                ++i;
            }
            if (idxToRemove != -1) {
                System.arraycopy(td.methods, idxToRemove + 1, td.methods, idxToRemove, td.methods.length - idxToRemove - 1);
                posToInsert = td.methods.length - 1;
                typeNode.removeChild(toRemove);
            }
        }
        if (posToInsert == -1) {
            AbstractMethodDeclaration[] na = new AbstractMethodDeclaration[td.methods.length + 1];
            posToInsert = td.methods.length;
            System.arraycopy(td.methods, 0, na, 0, posToInsert);
            td.methods = na;
        }
        cd = new ConstructorDeclaration(((CompilationUnitDeclaration)((EclipseNode)typeNode.top()).get()).compilationResult);
        cd.modifiers = 1;
        cd.bits = -2139094528;
        cd.selector = td.name;
        cd.constructorCall = new ExplicitConstructorCall(1);
        if (recordComponents == null) {
            recordComponents = this.getRecordComponents(typeNode);
        }
        cd.arguments = new Argument[recordComponents.size()];
        cd.statements = new Statement[recordComponents.size()];
        cd.bits = 512;
        i = 0;
        while (i < cd.arguments.length) {
            FieldDeclaration cmp = recordComponents.get(i);
            cd.arguments[i] = new Argument(cmp.name, (long)cmp.sourceStart, cmp.type, 0);
            cd.arguments[i].bits = -1073741820;
            FieldReference lhs = new FieldReference(cmp.name, 0L);
            lhs.receiver = new ThisReference(0, 0);
            SingleNameReference rhs = new SingleNameReference(cmp.name, 0L);
            cd.statements[i] = new Assignment((Expression)lhs, (Expression)rhs, cmp.sourceEnd);
            ++i;
        }
        EclipseHandlerUtil.setGeneratedBy(cd, (ASTNode)annotationNode.get());
        i = 0;
        while (i < cd.arguments.length) {
            FieldDeclaration cmp = recordComponents.get(i);
            cd.arguments[i].sourceStart = cmp.sourceStart;
            cd.arguments[i].sourceEnd = cmp.sourceStart;
            cd.arguments[i].declarationSourceEnd = cmp.sourceStart;
            cd.arguments[i].declarationEnd = cmp.sourceStart;
            ++i;
        }
        td.methods[posToInsert] = cd;
        cd.annotations = EclipseHandlerUtil.addSuppressWarningsAll(typeNode, (ASTNode)cd, cd.annotations);
        cd.annotations = EclipseHandlerUtil.addGenerated(typeNode, (ASTNode)cd, cd.annotations);
        return (EclipseNode)typeNode.add(cd, AST.Kind.METHOD);
    }

    private static char[][] getRawTypeName(TypeReference a) {
        if (a instanceof QualifiedTypeReference) {
            return ((QualifiedTypeReference)a).tokens;
        }
        if (a instanceof SingleTypeReference) {
            return new char[][]{((SingleTypeReference)a).token};
        }
        return null;
    }

    @Override
    public void handle(AnnotationValues<NonNull> annotation, Annotation ast, EclipseNode annotationNode) {
        if (!annotationNode.isCompleteParse()) {
            EclipseNode typeNode;
            EclipseNode node = ((EclipseNode)annotationNode.up()).getKind() == AST.Kind.TYPE_USE ? (EclipseNode)((EclipseNode)annotationNode.directUp()).directUp() : (EclipseNode)annotationNode.up();
            if (node.getKind() == AST.Kind.FIELD && (typeNode = (EclipseNode)node.up()).getKind() == AST.Kind.TYPE && EclipseHandlerUtil.isRecord(typeNode)) {
                this.addCompactConstructorIfNeeded(typeNode, annotationNode);
            }
            EcjAugments.ASTNode_handled.clear((ASTNode)ast);
            return;
        }
        this.handle0(ast, annotationNode, false);
    }

    private EclipseNode findCompactConstructor(EclipseNode typeNode) {
        for (EclipseNode child : typeNode.down()) {
            if (!(child.get() instanceof ConstructorDeclaration)) continue;
            ConstructorDeclaration cd = (ConstructorDeclaration)child.get();
            if ((cd.bits & 0x200) == 0 || (cd.bits & 0x400) != 0) continue;
            return child;
        }
        return null;
    }

    private void handle0(Annotation ast, EclipseNode annotationNode, boolean force) {
        AbstractMethodDeclaration declaration;
        Argument param;
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.NON_NULL_FLAG_USAGE, "@NonNull");
        EclipseNode node = ((EclipseNode)annotationNode.up()).getKind() == AST.Kind.TYPE_USE ? (EclipseNode)((EclipseNode)annotationNode.directUp()).directUp() : (EclipseNode)annotationNode.up();
        if (node.getKind() == AST.Kind.FIELD) {
            EclipseNode compactConstructor;
            EclipseNode fieldNode = node;
            EclipseNode typeNode = (EclipseNode)fieldNode.up();
            try {
                if (Eclipse.isPrimitive(((AbstractVariableDeclaration)node.get()).type)) {
                    annotationNode.addWarning("@NonNull is meaningless on a primitive.");
                    return;
                }
            }
            catch (Exception exception) {}
            if (EclipseHandlerUtil.isRecord(typeNode) && (compactConstructor = this.findCompactConstructor(typeNode)) != null) {
                this.addNullCheckIfNeeded((AbstractMethodDeclaration)compactConstructor.get(), (AbstractVariableDeclaration)fieldNode.get(), annotationNode);
            }
            return;
        }
        if (node.getKind() != AST.Kind.ARGUMENT) {
            return;
        }
        try {
            param = (Argument)node.get();
            declaration = (AbstractMethodDeclaration)((EclipseNode)node.up()).get();
        }
        catch (Exception exception) {
            return;
        }
        if (!force && EclipseHandlerUtil.isGenerated((ASTNode)declaration)) {
            return;
        }
        if (declaration.isAbstract()) {
            return;
        }
        this.addNullCheckIfNeeded(declaration, (AbstractVariableDeclaration)param, annotationNode);
        ((EclipseNode)node.up()).rebuild();
    }

    private void addNullCheckIfNeeded(AbstractMethodDeclaration declaration, AbstractVariableDeclaration param, EclipseNode annotationNode) {
        Statement nullCheck = EclipseHandlerUtil.generateNullCheck(param, annotationNode, null);
        if (nullCheck == null) {
            annotationNode.addWarning("@NonNull is meaningless on a primitive.");
            return;
        }
        if (declaration.statements == null) {
            declaration.statements = new Statement[]{nullCheck};
        } else {
            Statement stat;
            char[] expectedName = param.name;
            Statement[] stats = declaration.statements;
            int idx = 0;
            while (stats != null && stats.length > idx) {
                if ((stat = stats[idx++]) instanceof TryStatement) {
                    stats = ((TryStatement)stat).tryBlock.statements;
                    idx = 0;
                    continue;
                }
                if (stat instanceof SynchronizedStatement) {
                    stats = ((SynchronizedStatement)stat).block.statements;
                    idx = 0;
                    continue;
                }
                char[] varNameOfNullCheck = this.returnVarNameIfNullCheck(stat);
                if (varNameOfNullCheck == null) break;
                if (!Arrays.equals(varNameOfNullCheck, expectedName)) continue;
                return;
            }
            Statement[] newStatements = new Statement[declaration.statements.length + 1];
            int skipOver = 0;
            Statement[] statementArray = declaration.statements;
            int n = declaration.statements.length;
            int n2 = 0;
            while (n2 < n) {
                stat = statementArray[n2];
                if (!EclipseHandlerUtil.isGenerated((ASTNode)stat) || !this.isNullCheck(stat)) break;
                ++skipOver;
                ++n2;
            }
            System.arraycopy(declaration.statements, 0, newStatements, 0, skipOver);
            System.arraycopy(declaration.statements, skipOver, newStatements, skipOver + 1, declaration.statements.length - skipOver);
            newStatements[skipOver] = nullCheck;
            declaration.statements = newStatements;
        }
    }

    public boolean isNullCheck(Statement stat) {
        return this.returnVarNameIfNullCheck(stat) != null;
    }

    public char[] returnVarNameIfNullCheck(Statement stat) {
        Expression cond;
        boolean isIf = stat instanceof IfStatement;
        boolean isExpression = stat instanceof Expression;
        if (!(isIf || stat instanceof AssertStatement || isExpression)) {
            return null;
        }
        if (isExpression) {
            Expression expression = (Expression)stat;
            if (expression instanceof Assignment) {
                expression = ((Assignment)expression).expression;
            }
            if (!(expression instanceof MessageSend)) {
                return null;
            }
            MessageSend invocation = (MessageSend)expression;
            if (!Arrays.equals(invocation.selector, CHECK_NOT_NULL) && !Arrays.equals(invocation.selector, REQUIRE_NON_NULL)) {
                return null;
            }
            if (invocation.arguments == null || invocation.arguments.length == 0) {
                return null;
            }
            Expression firstArgument = invocation.arguments[0];
            if (!(firstArgument instanceof SingleNameReference)) {
                return null;
            }
            return ((SingleNameReference)firstArgument).token;
        }
        if (isIf) {
            Statement then = ((IfStatement)stat).thenStatement;
            if (then instanceof Block) {
                Statement[] blockStatements = ((Block)then).statements;
                if (blockStatements == null || blockStatements.length == 0) {
                    return null;
                }
                then = blockStatements[0];
            }
            if (!(then instanceof ThrowStatement)) {
                return null;
            }
        }
        Expression expression = cond = isIf ? ((IfStatement)stat).condition : ((AssertStatement)stat).assertExpression;
        if (!(cond instanceof EqualExpression)) {
            return null;
        }
        EqualExpression bin = (EqualExpression)cond;
        String op = bin.operatorToString();
        if (isIf ? !"==".equals(op) : !"!=".equals(op)) {
            return null;
        }
        if (!(bin.left instanceof SingleNameReference)) {
            return null;
        }
        if (!(bin.right instanceof NullLiteral)) {
            return null;
        }
        return ((SingleNameReference)bin.left).token;
    }
}
