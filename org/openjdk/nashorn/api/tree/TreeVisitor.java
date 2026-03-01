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
import org.openjdk.nashorn.api.tree.SwitchTree;
import org.openjdk.nashorn.api.tree.TemplateLiteralTree;
import org.openjdk.nashorn.api.tree.ThrowTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TryTree;
import org.openjdk.nashorn.api.tree.UnaryTree;
import org.openjdk.nashorn.api.tree.VariableTree;
import org.openjdk.nashorn.api.tree.WhileLoopTree;
import org.openjdk.nashorn.api.tree.WithTree;
import org.openjdk.nashorn.api.tree.YieldTree;

public interface TreeVisitor<R, P> {
    public R visitAssignment(AssignmentTree var1, P var2);

    public R visitCompoundAssignment(CompoundAssignmentTree var1, P var2);

    public R visitBinary(BinaryTree var1, P var2);

    public R visitBlock(BlockTree var1, P var2);

    public R visitBreak(BreakTree var1, P var2);

    public R visitCase(CaseTree var1, P var2);

    public R visitCatch(CatchTree var1, P var2);

    public R visitClassDeclaration(ClassDeclarationTree var1, P var2);

    public R visitClassExpression(ClassExpressionTree var1, P var2);

    public R visitConditionalExpression(ConditionalExpressionTree var1, P var2);

    public R visitContinue(ContinueTree var1, P var2);

    public R visitDebugger(DebuggerTree var1, P var2);

    public R visitDoWhileLoop(DoWhileLoopTree var1, P var2);

    public R visitErroneous(ErroneousTree var1, P var2);

    public R visitExpressionStatement(ExpressionStatementTree var1, P var2);

    public R visitForLoop(ForLoopTree var1, P var2);

    public R visitForInLoop(ForInLoopTree var1, P var2);

    public R visitForOfLoop(ForOfLoopTree var1, P var2);

    public R visitFunctionCall(FunctionCallTree var1, P var2);

    public R visitFunctionDeclaration(FunctionDeclarationTree var1, P var2);

    public R visitFunctionExpression(FunctionExpressionTree var1, P var2);

    public R visitIdentifier(IdentifierTree var1, P var2);

    public R visitIf(IfTree var1, P var2);

    public R visitArrayAccess(ArrayAccessTree var1, P var2);

    public R visitArrayLiteral(ArrayLiteralTree var1, P var2);

    public R visitLabeledStatement(LabeledStatementTree var1, P var2);

    public R visitLiteral(LiteralTree var1, P var2);

    public R visitParenthesized(ParenthesizedTree var1, P var2);

    public R visitReturn(ReturnTree var1, P var2);

    public R visitMemberSelect(MemberSelectTree var1, P var2);

    public R visitNew(NewTree var1, P var2);

    public R visitObjectLiteral(ObjectLiteralTree var1, P var2);

    public R visitProperty(PropertyTree var1, P var2);

    public R visitRegExpLiteral(RegExpLiteralTree var1, P var2);

    public R visitTemplateLiteral(TemplateLiteralTree var1, P var2);

    public R visitEmptyStatement(EmptyStatementTree var1, P var2);

    public R visitSpread(SpreadTree var1, P var2);

    public R visitSwitch(SwitchTree var1, P var2);

    public R visitThrow(ThrowTree var1, P var2);

    public R visitCompilationUnit(CompilationUnitTree var1, P var2);

    public R visitModule(ModuleTree var1, P var2);

    public R visitExportEntry(ExportEntryTree var1, P var2);

    public R visitImportEntry(ImportEntryTree var1, P var2);

    public R visitTry(TryTree var1, P var2);

    public R visitInstanceOf(InstanceOfTree var1, P var2);

    public R visitUnary(UnaryTree var1, P var2);

    public R visitVariable(VariableTree var1, P var2);

    public R visitWhileLoop(WhileLoopTree var1, P var2);

    public R visitWith(WithTree var1, P var2);

    public R visitYield(YieldTree var1, P var2);

    public R visitUnknown(Tree var1, P var2);
}
