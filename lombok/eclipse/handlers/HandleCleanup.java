package lombok.eclipse.handlers;

import java.util.Arrays;
import lombok.Cleanup;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;

public class HandleCleanup
extends EclipseAnnotationHandler<Cleanup> {
    @Override
    public void handle(AnnotationValues<Cleanup> annotation, Annotation ast, EclipseNode annotationNode) {
        int ss;
        int end;
        Statement[] statements;
        boolean isSwitch;
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.CLEANUP_FLAG_USAGE, "@Cleanup");
        String cleanupName = annotation.getInstance().value();
        if (cleanupName.length() == 0) {
            annotationNode.addError("cleanupName cannot be the empty string.");
            return;
        }
        if (((EclipseNode)annotationNode.up()).getKind() != AST.Kind.LOCAL) {
            annotationNode.addError("@Cleanup is legal only on local variable declarations.");
            return;
        }
        LocalDeclaration decl = (LocalDeclaration)((EclipseNode)annotationNode.up()).get();
        if (decl.initialization == null) {
            annotationNode.addError("@Cleanup variable declarations need to be initialized.");
            return;
        }
        EclipseNode ancestor = (EclipseNode)((EclipseNode)annotationNode.up()).directUp();
        ASTNode blockNode = (ASTNode)ancestor.get();
        if (blockNode instanceof AbstractMethodDeclaration) {
            isSwitch = false;
            statements = ((AbstractMethodDeclaration)blockNode).statements;
        } else if (blockNode instanceof Block) {
            isSwitch = false;
            statements = ((Block)blockNode).statements;
        } else if (blockNode instanceof SwitchStatement) {
            isSwitch = true;
            statements = ((SwitchStatement)blockNode).statements;
        } else {
            annotationNode.addError("@Cleanup is legal only on a local variable declaration inside a block.");
            return;
        }
        if (statements == null) {
            annotationNode.addError("LOMBOK BUG: Parent block does not contain any statements.");
            return;
        }
        int start = 0;
        while (start < statements.length) {
            if (statements[start] == decl) break;
            ++start;
        }
        if (start == statements.length) {
            annotationNode.addError("LOMBOK BUG: Can't find this local variable declaration inside its parent.");
            return;
        }
        ++start;
        if (isSwitch) {
            end = start + 1;
            while (end < statements.length) {
                if (!(statements[end] instanceof CaseStatement)) {
                    ++end;
                    continue;
                }
                break;
            }
        } else {
            end = statements.length;
        }
        Statement[] tryBlock = new Statement[end - start];
        System.arraycopy(statements, start, tryBlock, 0, end - start);
        int newStatementsLength = statements.length - (end - start);
        Statement[] newStatements = new Statement[++newStatementsLength];
        System.arraycopy(statements, 0, newStatements, 0, start);
        System.arraycopy(statements, end, newStatements, start + 1, statements.length - end);
        this.doAssignmentCheck(annotationNode, tryBlock, decl.name);
        TryStatement tryStatement = new TryStatement();
        EclipseHandlerUtil.setGeneratedBy(tryStatement, (ASTNode)ast);
        tryStatement.tryBlock = new Block(0);
        tryStatement.tryBlock.statements = tryBlock;
        EclipseHandlerUtil.setGeneratedBy(tryStatement.tryBlock, (ASTNode)ast);
        int se = ss = decl.declarationSourceEnd + 1;
        if (tryBlock.length > 0) {
            se = tryBlock[tryBlock.length - 1].sourceEnd + 1;
            tryStatement.sourceStart = ss;
            tryStatement.sourceEnd = se;
            tryStatement.tryBlock.sourceStart = ss;
            tryStatement.tryBlock.sourceEnd = se;
        }
        newStatements[start] = tryStatement;
        Statement[] finallyBlock = new Statement[1];
        MessageSend unsafeClose = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy(unsafeClose, (ASTNode)ast);
        unsafeClose.sourceStart = ast.sourceStart;
        unsafeClose.sourceEnd = ast.sourceEnd;
        SingleNameReference receiver = new SingleNameReference(decl.name, 0L);
        EclipseHandlerUtil.setGeneratedBy(receiver, (ASTNode)ast);
        unsafeClose.receiver = receiver;
        long nameSourcePosition = (long)ast.sourceStart << 32 | (long)ast.sourceEnd;
        if (ast.memberValuePairs() != null) {
            MemberValuePair[] memberValuePairArray = ast.memberValuePairs();
            int n = memberValuePairArray.length;
            int n2 = 0;
            while (n2 < n) {
                MemberValuePair pair = memberValuePairArray[n2];
                if (pair.name != null && new String(pair.name).equals("value")) {
                    nameSourcePosition = (long)pair.value.sourceStart << 32 | (long)pair.value.sourceEnd;
                    break;
                }
                ++n2;
            }
        }
        unsafeClose.nameSourcePosition = nameSourcePosition;
        unsafeClose.selector = cleanupName.toCharArray();
        int pS = ast.sourceStart;
        int pE = ast.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        SingleNameReference varName = new SingleNameReference(decl.name, p);
        EclipseHandlerUtil.setGeneratedBy(varName, (ASTNode)ast);
        NullLiteral nullLiteral = new NullLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy(nullLiteral, (ASTNode)ast);
        MessageSend preventNullAnalysis = this.preventNullAnalysis(ast, (Expression)varName);
        EqualExpression equalExpression = new EqualExpression((Expression)preventNullAnalysis, (Expression)nullLiteral, OperatorIds.NOT_EQUAL);
        equalExpression.sourceStart = pS;
        equalExpression.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(equalExpression, (ASTNode)ast);
        Block closeBlock = new Block(0);
        closeBlock.statements = new Statement[1];
        closeBlock.statements[0] = unsafeClose;
        EclipseHandlerUtil.setGeneratedBy(closeBlock, (ASTNode)ast);
        IfStatement ifStatement = new IfStatement((Expression)equalExpression, (Statement)closeBlock, 0, 0);
        EclipseHandlerUtil.setGeneratedBy(ifStatement, (ASTNode)ast);
        finallyBlock[0] = ifStatement;
        tryStatement.finallyBlock = new Block(0);
        if (!isSwitch) {
            tryStatement.finallyBlock.sourceStart = blockNode.sourceEnd;
            tryStatement.finallyBlock.sourceEnd = blockNode.sourceEnd;
        }
        EclipseHandlerUtil.setGeneratedBy(tryStatement.finallyBlock, (ASTNode)ast);
        tryStatement.finallyBlock.statements = finallyBlock;
        tryStatement.catchArguments = null;
        tryStatement.catchBlocks = null;
        if (blockNode instanceof AbstractMethodDeclaration) {
            ((AbstractMethodDeclaration)blockNode).statements = newStatements;
        } else if (blockNode instanceof Block) {
            ((Block)blockNode).statements = newStatements;
        } else if (blockNode instanceof SwitchStatement) {
            ((SwitchStatement)blockNode).statements = newStatements;
        }
        ancestor.rebuild();
    }

    public MessageSend preventNullAnalysis(Annotation ast, Expression expr) {
        MessageSend singletonList = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy(singletonList, (ASTNode)ast);
        int pS = ast.sourceStart;
        int pE = ast.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        singletonList.receiver = EclipseHandlerUtil.createNameReference("java.util.Collections", ast);
        singletonList.selector = "singletonList".toCharArray();
        singletonList.arguments = new Expression[]{expr};
        singletonList.nameSourcePosition = p;
        singletonList.sourceStart = pS;
        singletonList.sourceEnd = singletonList.statementEnd = pE;
        MessageSend preventNullAnalysis = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy(preventNullAnalysis, (ASTNode)ast);
        preventNullAnalysis.receiver = singletonList;
        preventNullAnalysis.selector = "get".toCharArray();
        preventNullAnalysis.arguments = new Expression[]{EclipseHandlerUtil.makeIntLiteral("0".toCharArray(), (ASTNode)ast)};
        preventNullAnalysis.nameSourcePosition = p;
        preventNullAnalysis.sourceStart = pS;
        preventNullAnalysis.sourceEnd = singletonList.statementEnd = pE;
        return preventNullAnalysis;
    }

    public void doAssignmentCheck(EclipseNode node, Statement[] tryBlock, char[] varName) {
        Statement[] statementArray = tryBlock;
        int n = tryBlock.length;
        int n2 = 0;
        while (n2 < n) {
            Statement statement = statementArray[n2];
            this.doAssignmentCheck0(node, statement, varName);
            ++n2;
        }
    }

    private void doAssignmentCheck0(EclipseNode node, Statement statement, char[] varName) {
        EclipseNode problemNode;
        if (statement instanceof Assignment) {
            this.doAssignmentCheck0(node, (Statement)((Assignment)statement).expression, varName);
        } else if (statement instanceof LocalDeclaration) {
            this.doAssignmentCheck0(node, (Statement)((LocalDeclaration)statement).initialization, varName);
        } else if (statement instanceof CastExpression) {
            this.doAssignmentCheck0(node, (Statement)((CastExpression)statement).expression, varName);
        } else if (statement instanceof SingleNameReference && Arrays.equals(((SingleNameReference)statement).token, varName) && (problemNode = (EclipseNode)node.getNodeFor(statement)) != null) {
            problemNode.addWarning("You're assigning an auto-cleanup variable to something else. This is a bad idea.");
        }
    }
}
