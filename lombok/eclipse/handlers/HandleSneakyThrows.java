package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.ConfigurationKeys;
import lombok.SneakyThrows;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EcjAugments;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

@DeferUntilPostDiet
@HandlerPriority(value=1024)
public class HandleSneakyThrows
extends EclipseAnnotationHandler<SneakyThrows> {
    @Override
    public void preHandle(AnnotationValues<SneakyThrows> annotation, Annotation ast, EclipseNode annotationNode) {
        if (EclipseHandlerUtil.hasParsedBody(EclipseHandlerUtil.getAnnotatedMethod(annotationNode))) {
            this.handle(annotation, ast, annotationNode);
            EcjAugments.ASTNode_handled.set((ASTNode)ast, true);
        }
    }

    @Override
    public void handle(AnnotationValues<SneakyThrows> annotation, Annotation source, EclipseNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.SNEAKY_THROWS_FLAG_USAGE, "@SneakyThrows");
        List<String> exceptionNames = annotation.getRawExpressions("value");
        ArrayList<DeclaredException> exceptions = new ArrayList<DeclaredException>();
        MemberValuePair[] memberValuePairs = source.memberValuePairs();
        if (memberValuePairs == null || memberValuePairs.length == 0) {
            exceptions.add(new DeclaredException("java.lang.Throwable", (ASTNode)source));
        } else {
            Expression arrayOrSingle = memberValuePairs[0].value;
            Expression[] exceptionNameNodes = arrayOrSingle instanceof ArrayInitializer ? ((ArrayInitializer)arrayOrSingle).expressions : new Expression[]{arrayOrSingle};
            if (exceptionNames.size() != exceptionNameNodes.length) {
                annotationNode.addError("LOMBOK BUG: The number of exception classes in the annotation isn't the same pre- and post- guessing.");
            }
            int idx = 0;
            for (String exceptionName : exceptionNames) {
                if (exceptionName.endsWith(".class")) {
                    exceptionName = exceptionName.substring(0, exceptionName.length() - 6);
                }
                exceptions.add(new DeclaredException(exceptionName, (ASTNode)exceptionNameNodes[idx++]));
            }
        }
        EclipseNode owner = (EclipseNode)annotationNode.up();
        switch (owner.getKind()) {
            case METHOD: {
                this.handleMethod(annotationNode, (AbstractMethodDeclaration)owner.get(), exceptions);
                break;
            }
            default: {
                annotationNode.addError("@SneakyThrows is legal only on methods and constructors.");
            }
        }
    }

    public void handleMethod(EclipseNode annotation, AbstractMethodDeclaration method, List<DeclaredException> exceptions) {
        if (method.isAbstract()) {
            annotation.addError("@SneakyThrows can only be used on concrete methods.");
            return;
        }
        if (method.statements == null || method.statements.length == 0) {
            boolean hasConstructorCall = false;
            if (method instanceof ConstructorDeclaration) {
                ExplicitConstructorCall constructorCall = ((ConstructorDeclaration)method).constructorCall;
                boolean bl = hasConstructorCall = constructorCall != null && !constructorCall.isImplicitSuper() && !constructorCall.isImplicitThis();
            }
            if (hasConstructorCall) {
                annotation.addWarning("Calls to sibling / super constructors are always excluded from @SneakyThrows; @SneakyThrows has been ignored because there is no other code in this constructor.");
            } else {
                annotation.addWarning("This method or constructor is empty; @SneakyThrows has been ignored.");
            }
            return;
        }
        Statement[] contents = method.statements;
        for (DeclaredException exception : exceptions) {
            contents = new Statement[]{this.buildTryCatchBlock(contents, exception, exception.node, method)};
        }
        method.statements = contents;
        ((EclipseNode)annotation.up()).rebuild();
    }

    public Statement buildTryCatchBlock(Statement[] contents, DeclaredException exception, ASTNode source, AbstractMethodDeclaration method) {
        SingleTypeReference typeReference;
        int methodStart = method.bodyStart;
        int methodEnd = method.bodyEnd;
        long methodPosEnd = (long)methodEnd << 32 | (long)methodEnd & 0xFFFFFFFFL;
        TryStatement tryStatement = new TryStatement();
        EclipseHandlerUtil.setGeneratedBy(tryStatement, source);
        tryStatement.tryBlock = new Block(0);
        tryStatement.tryBlock.sourceStart = methodStart;
        tryStatement.tryBlock.sourceEnd = methodEnd;
        EclipseHandlerUtil.setGeneratedBy(tryStatement.tryBlock, source);
        tryStatement.tryBlock.statements = contents;
        if (exception.exceptionName.indexOf(46) == -1) {
            typeReference = new SingleTypeReference(exception.exceptionName.toCharArray(), methodPosEnd);
            typeReference.statementEnd = methodEnd;
        } else {
            String[] x = exception.exceptionName.split("\\.");
            char[][] elems = new char[x.length][];
            long[] poss = new long[x.length];
            Arrays.fill(poss, methodPosEnd);
            int i = 0;
            while (i < x.length) {
                elems[i] = x[i].trim().toCharArray();
                ++i;
            }
            typeReference = new QualifiedTypeReference((char[][])elems, poss);
        }
        EclipseHandlerUtil.setGeneratedBy(typeReference, source);
        Argument catchArg = new Argument("$ex".toCharArray(), methodPosEnd, (TypeReference)typeReference, 16);
        EclipseHandlerUtil.setGeneratedBy(catchArg, source);
        catchArg.declarationEnd = catchArg.sourceEnd = methodEnd;
        catchArg.declarationSourceEnd = catchArg.sourceEnd;
        catchArg.modifiersSourceStart = catchArg.sourceStart = methodEnd;
        catchArg.declarationSourceStart = catchArg.sourceStart;
        tryStatement.catchArguments = new Argument[]{catchArg};
        MessageSend sneakyThrowStatement = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy(sneakyThrowStatement, source);
        sneakyThrowStatement.receiver = new QualifiedNameReference((char[][])new char[][]{"lombok".toCharArray(), "Lombok".toCharArray()}, new long[2], methodEnd, methodEnd);
        EclipseHandlerUtil.setGeneratedBy(sneakyThrowStatement.receiver, source);
        sneakyThrowStatement.receiver.statementEnd = methodEnd;
        sneakyThrowStatement.selector = "sneakyThrow".toCharArray();
        SingleNameReference exRef = new SingleNameReference("$ex".toCharArray(), methodPosEnd);
        EclipseHandlerUtil.setGeneratedBy(exRef, source);
        exRef.statementEnd = methodEnd;
        sneakyThrowStatement.arguments = new Expression[]{exRef};
        sneakyThrowStatement.nameSourcePosition = -2L;
        sneakyThrowStatement.sourceStart = methodEnd;
        sneakyThrowStatement.sourceEnd = sneakyThrowStatement.statementEnd = methodEnd;
        ThrowStatement rethrowStatement = new ThrowStatement((Expression)sneakyThrowStatement, methodEnd, methodEnd);
        EclipseHandlerUtil.setGeneratedBy(rethrowStatement, source);
        Block block = new Block(0);
        block.sourceStart = methodEnd;
        block.sourceEnd = methodEnd;
        EclipseHandlerUtil.setGeneratedBy(block, source);
        block.statements = new Statement[]{rethrowStatement};
        tryStatement.catchBlocks = new Block[]{block};
        tryStatement.sourceStart = method.bodyStart;
        tryStatement.sourceEnd = method.bodyEnd;
        return tryStatement;
    }

    private static class DeclaredException {
        final String exceptionName;
        final ASTNode node;

        DeclaredException(String exceptionName, ASTNode node) {
            this.exceptionName = exceptionName;
            this.node = node;
        }
    }
}
