package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ArrayAccessTree;
import org.openjdk.nashorn.api.tree.ArrayLiteralTree;
import org.openjdk.nashorn.api.tree.AssignmentTree;
import org.openjdk.nashorn.api.tree.BinaryTree;
import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.BreakTree;
import org.openjdk.nashorn.api.tree.CaseTree;
import org.openjdk.nashorn.api.tree.CatchTree;
import org.openjdk.nashorn.api.tree.ClassDeclarationTree;
import org.openjdk.nashorn.api.tree.ClassExpressionTree;
import org.openjdk.nashorn.api.tree.CompilationUnitTree;
import org.openjdk.nashorn.api.tree.CompoundAssignmentTree;
import org.openjdk.nashorn.api.tree.ConditionalExpressionTree;
import org.openjdk.nashorn.api.tree.ContinueTree;
import org.openjdk.nashorn.api.tree.DebuggerTree;
import org.openjdk.nashorn.api.tree.DoWhileLoopTree;
import org.openjdk.nashorn.api.tree.EmptyStatementTree;
import org.openjdk.nashorn.api.tree.ErroneousTree;
import org.openjdk.nashorn.api.tree.ExportEntryTree;
import org.openjdk.nashorn.api.tree.ExpressionStatementTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ForInLoopTree;
import org.openjdk.nashorn.api.tree.ForLoopTree;
import org.openjdk.nashorn.api.tree.ForOfLoopTree;
import org.openjdk.nashorn.api.tree.FunctionCallTree;
import org.openjdk.nashorn.api.tree.FunctionDeclarationTree;
import org.openjdk.nashorn.api.tree.FunctionExpressionTree;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.IfTree;
import org.openjdk.nashorn.api.tree.ImportEntryTree;
import org.openjdk.nashorn.api.tree.InstanceOfTree;
import org.openjdk.nashorn.api.tree.LabeledStatementTree;
import org.openjdk.nashorn.api.tree.LiteralTree;
import org.openjdk.nashorn.api.tree.MemberSelectTree;
import org.openjdk.nashorn.api.tree.ModuleTree;
import org.openjdk.nashorn.api.tree.NewTree;
import org.openjdk.nashorn.api.tree.ObjectLiteralTree;
import org.openjdk.nashorn.api.tree.ParenthesizedTree;
import org.openjdk.nashorn.api.tree.PropertyTree;
import org.openjdk.nashorn.api.tree.RegExpLiteralTree;
import org.openjdk.nashorn.api.tree.ReturnTree;
import org.openjdk.nashorn.api.tree.SpreadTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.SwitchTree;
import org.openjdk.nashorn.api.tree.TemplateLiteralTree;
import org.openjdk.nashorn.api.tree.ThrowTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.api.tree.TryTree;
import org.openjdk.nashorn.api.tree.UnaryTree;
import org.openjdk.nashorn.api.tree.UnknownTreeException;
import org.openjdk.nashorn.api.tree.VariableTree;
import org.openjdk.nashorn.api.tree.WhileLoopTree;
import org.openjdk.nashorn.api.tree.WithTree;
import org.openjdk.nashorn.api.tree.YieldTree;

public class SimpleTreeVisitorES5_1<R, P>
implements TreeVisitor<R, P> {
    @Override
    public R visitAssignment(AssignmentTree node, P r) {
        node.getVariable().accept(this, r);
        node.getExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitCompoundAssignment(CompoundAssignmentTree node, P r) {
        node.getVariable().accept(this, r);
        node.getExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitModule(ModuleTree node, P p) {
        return this.visitUnknown(node, p);
    }

    @Override
    public R visitExportEntry(ExportEntryTree node, P p) {
        return this.visitUnknown(node, p);
    }

    @Override
    public R visitImportEntry(ImportEntryTree node, P p) {
        return this.visitUnknown(node, p);
    }

    @Override
    public R visitBinary(BinaryTree node, P r) {
        node.getLeftOperand().accept(this, r);
        node.getRightOperand().accept(this, r);
        return null;
    }

    @Override
    public R visitBlock(BlockTree node, P r) {
        node.getStatements().forEach(tree -> tree.accept(this, r));
        return null;
    }

    @Override
    public R visitBreak(BreakTree node, P r) {
        return null;
    }

    @Override
    public R visitCase(CaseTree node, P r) {
        ExpressionTree caseVal = node.getExpression();
        if (caseVal != null) {
            caseVal.accept(this, r);
        }
        node.getStatements().forEach(tree -> tree.accept(this, r));
        return null;
    }

    @Override
    public R visitCatch(CatchTree node, P r) {
        ExpressionTree cond = node.getCondition();
        if (cond != null) {
            cond.accept(this, r);
        }
        node.getParameter().accept(this, r);
        node.getBlock().accept(this, r);
        return null;
    }

    @Override
    public R visitClassDeclaration(ClassDeclarationTree node, P p) {
        return this.visitUnknown(node, p);
    }

    @Override
    public R visitClassExpression(ClassExpressionTree node, P p) {
        return this.visitUnknown(node, p);
    }

    @Override
    public R visitConditionalExpression(ConditionalExpressionTree node, P r) {
        node.getCondition().accept(this, r);
        node.getTrueExpression().accept(this, r);
        node.getFalseExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitContinue(ContinueTree node, P r) {
        return null;
    }

    @Override
    public R visitDebugger(DebuggerTree node, P r) {
        return null;
    }

    @Override
    public R visitDoWhileLoop(DoWhileLoopTree node, P r) {
        node.getStatement().accept(this, r);
        node.getCondition().accept(this, r);
        return null;
    }

    @Override
    public R visitErroneous(ErroneousTree node, P r) {
        return null;
    }

    @Override
    public R visitExpressionStatement(ExpressionStatementTree node, P r) {
        node.getExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitForLoop(ForLoopTree node, P r) {
        ExpressionTree update;
        ExpressionTree cond;
        ExpressionTree init = node.getInitializer();
        if (init != null) {
            init.accept(this, r);
        }
        if ((cond = node.getCondition()) != null) {
            cond.accept(this, r);
        }
        if ((update = node.getUpdate()) != null) {
            update.accept(this, r);
        }
        node.getStatement().accept(this, r);
        return null;
    }

    @Override
    public R visitForInLoop(ForInLoopTree node, P r) {
        node.getVariable().accept(this, r);
        node.getExpression().accept(this, r);
        StatementTree stat = node.getStatement();
        if (stat != null) {
            stat.accept(this, r);
        }
        return null;
    }

    @Override
    public R visitForOfLoop(ForOfLoopTree node, P p) {
        return this.visitUnknown(node, p);
    }

    @Override
    public R visitFunctionCall(FunctionCallTree node, P r) {
        node.getFunctionSelect().accept(this, r);
        node.getArguments().forEach(tree -> tree.accept(this, r));
        return null;
    }

    @Override
    public R visitFunctionDeclaration(FunctionDeclarationTree node, P r) {
        node.getParameters().forEach(tree -> tree.accept(this, r));
        node.getBody().accept(this, r);
        return null;
    }

    @Override
    public R visitFunctionExpression(FunctionExpressionTree node, P r) {
        node.getParameters().forEach(tree -> tree.accept(this, r));
        node.getBody().accept(this, r);
        return null;
    }

    @Override
    public R visitIdentifier(IdentifierTree node, P r) {
        return null;
    }

    @Override
    public R visitIf(IfTree node, P r) {
        node.getCondition().accept(this, r);
        node.getThenStatement().accept(this, r);
        StatementTree elseStat = node.getElseStatement();
        if (elseStat != null) {
            elseStat.accept(this, r);
        }
        return null;
    }

    @Override
    public R visitArrayAccess(ArrayAccessTree node, P r) {
        node.getExpression().accept(this, r);
        node.getIndex().accept(this, r);
        return null;
    }

    @Override
    public R visitArrayLiteral(ArrayLiteralTree node, P r) {
        node.getElements().stream().filter(tree -> tree != null).forEach(tree -> tree.accept(this, r));
        return null;
    }

    @Override
    public R visitLabeledStatement(LabeledStatementTree node, P r) {
        node.getStatement().accept(this, r);
        return null;
    }

    @Override
    public R visitLiteral(LiteralTree node, P r) {
        return null;
    }

    @Override
    public R visitParenthesized(ParenthesizedTree node, P r) {
        node.getExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitReturn(ReturnTree node, P r) {
        ExpressionTree retExpr = node.getExpression();
        if (retExpr != null) {
            retExpr.accept(this, r);
        }
        return null;
    }

    @Override
    public R visitMemberSelect(MemberSelectTree node, P r) {
        node.getExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitNew(NewTree node, P r) {
        node.getConstructorExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitObjectLiteral(ObjectLiteralTree node, P r) {
        node.getProperties().forEach(tree -> tree.accept(this, r));
        return null;
    }

    @Override
    public R visitProperty(PropertyTree node, P r) {
        ExpressionTree value;
        FunctionExpressionTree setter;
        ExpressionTree key;
        FunctionExpressionTree getter = node.getGetter();
        if (getter != null) {
            getter.accept(this, r);
        }
        if ((key = node.getKey()) != null) {
            key.accept(this, r);
        }
        if ((setter = node.getSetter()) != null) {
            setter.accept(this, r);
        }
        if ((value = node.getValue()) != null) {
            value.accept(this, r);
        }
        return null;
    }

    @Override
    public R visitRegExpLiteral(RegExpLiteralTree node, P r) {
        return null;
    }

    @Override
    public R visitTemplateLiteral(TemplateLiteralTree node, P p) {
        return this.visitUnknown(node, p);
    }

    @Override
    public R visitEmptyStatement(EmptyStatementTree node, P r) {
        return null;
    }

    @Override
    public R visitSpread(SpreadTree node, P p) {
        return this.visitUnknown(node, p);
    }

    @Override
    public R visitSwitch(SwitchTree node, P r) {
        node.getExpression().accept(this, r);
        node.getCases().forEach(tree -> tree.accept(this, r));
        return null;
    }

    @Override
    public R visitThrow(ThrowTree node, P r) {
        node.getExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitCompilationUnit(CompilationUnitTree node, P r) {
        node.getSourceElements().forEach(tree -> tree.accept(this, r));
        return null;
    }

    @Override
    public R visitTry(TryTree node, P r) {
        node.getBlock().accept(this, r);
        node.getCatches().forEach(tree -> tree.accept(this, r));
        BlockTree finallyBlock = node.getFinallyBlock();
        if (finallyBlock != null) {
            finallyBlock.accept(this, r);
        }
        return null;
    }

    @Override
    public R visitInstanceOf(InstanceOfTree node, P r) {
        node.getType().accept(this, r);
        node.getExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitUnary(UnaryTree node, P r) {
        node.getExpression().accept(this, r);
        return null;
    }

    @Override
    public R visitVariable(VariableTree node, P r) {
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this, r);
        }
        return null;
    }

    @Override
    public R visitWhileLoop(WhileLoopTree node, P r) {
        node.getCondition().accept(this, r);
        node.getStatement().accept(this, r);
        return null;
    }

    @Override
    public R visitWith(WithTree node, P r) {
        node.getScope().accept(this, r);
        node.getStatement().accept(this, r);
        return null;
    }

    @Override
    public R visitYield(YieldTree node, P p) {
        return this.visitUnknown(node, p);
    }

    @Override
    public R visitUnknown(Tree node, P p) {
        throw new UnknownTreeException(node, p);
    }
}
