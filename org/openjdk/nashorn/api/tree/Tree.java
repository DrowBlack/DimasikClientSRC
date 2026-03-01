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
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.api.tree.TryTree;
import org.openjdk.nashorn.api.tree.UnaryTree;
import org.openjdk.nashorn.api.tree.VariableTree;
import org.openjdk.nashorn.api.tree.WhileLoopTree;
import org.openjdk.nashorn.api.tree.WithTree;
import org.openjdk.nashorn.api.tree.YieldTree;

public interface Tree {
    public long getStartPosition();

    public long getEndPosition();

    public Kind getKind();

    public <R, D> R accept(TreeVisitor<R, D> var1, D var2);

    public static enum Kind {
        ARRAY_ACCESS(ArrayAccessTree.class),
        ARRAY_LITERAL(ArrayLiteralTree.class),
        ASSIGNMENT(AssignmentTree.class),
        BLOCK(BlockTree.class),
        BREAK(BreakTree.class),
        CLASS(ClassDeclarationTree.class),
        CLASS_EXPRESSION(ClassExpressionTree.class),
        CASE(CaseTree.class),
        CATCH(CatchTree.class),
        COMPILATION_UNIT(CompilationUnitTree.class),
        CONDITIONAL_EXPRESSION(ConditionalExpressionTree.class),
        CONTINUE(ContinueTree.class),
        DO_WHILE_LOOP(DoWhileLoopTree.class),
        DEBUGGER(DebuggerTree.class),
        FOR_IN_LOOP(ForInLoopTree.class),
        FUNCTION_EXPRESSION(FunctionExpressionTree.class),
        ERROR(ErroneousTree.class),
        EXPRESSION_STATEMENT(ExpressionStatementTree.class),
        MEMBER_SELECT(MemberSelectTree.class),
        FOR_LOOP(ForLoopTree.class),
        IDENTIFIER(IdentifierTree.class),
        IF(IfTree.class),
        INSTANCE_OF(InstanceOfTree.class),
        LABELED_STATEMENT(LabeledStatementTree.class),
        MODULE(ModuleTree.class),
        EXPORT_ENTRY(ExportEntryTree.class),
        IMPORT_ENTRY(ImportEntryTree.class),
        FUNCTION(FunctionDeclarationTree.class),
        FUNCTION_INVOCATION(FunctionCallTree.class),
        NEW(NewTree.class),
        OBJECT_LITERAL(ObjectLiteralTree.class),
        PARENTHESIZED(ParenthesizedTree.class),
        PROPERTY(PropertyTree.class),
        REGEXP_LITERAL(RegExpLiteralTree.class),
        TEMPLATE_LITERAL(TemplateLiteralTree.class),
        RETURN(ReturnTree.class),
        EMPTY_STATEMENT(EmptyStatementTree.class),
        SWITCH(SwitchTree.class),
        THROW(ThrowTree.class),
        TRY(TryTree.class),
        VARIABLE(VariableTree.class),
        WHILE_LOOP(WhileLoopTree.class),
        WITH(WithTree.class),
        POSTFIX_INCREMENT(UnaryTree.class),
        POSTFIX_DECREMENT(UnaryTree.class),
        PREFIX_INCREMENT(UnaryTree.class),
        PREFIX_DECREMENT(UnaryTree.class),
        UNARY_PLUS(UnaryTree.class),
        UNARY_MINUS(UnaryTree.class),
        BITWISE_COMPLEMENT(UnaryTree.class),
        LOGICAL_COMPLEMENT(UnaryTree.class),
        DELETE(UnaryTree.class),
        TYPEOF(UnaryTree.class),
        VOID(UnaryTree.class),
        COMMA(BinaryTree.class),
        MULTIPLY(BinaryTree.class),
        DIVIDE(BinaryTree.class),
        REMAINDER(BinaryTree.class),
        PLUS(BinaryTree.class),
        MINUS(BinaryTree.class),
        LEFT_SHIFT(BinaryTree.class),
        RIGHT_SHIFT(BinaryTree.class),
        UNSIGNED_RIGHT_SHIFT(BinaryTree.class),
        LESS_THAN(BinaryTree.class),
        GREATER_THAN(BinaryTree.class),
        LESS_THAN_EQUAL(BinaryTree.class),
        GREATER_THAN_EQUAL(BinaryTree.class),
        IN(BinaryTree.class),
        EQUAL_TO(BinaryTree.class),
        NOT_EQUAL_TO(BinaryTree.class),
        STRICT_EQUAL_TO(BinaryTree.class),
        STRICT_NOT_EQUAL_TO(BinaryTree.class),
        AND(BinaryTree.class),
        XOR(BinaryTree.class),
        OR(BinaryTree.class),
        CONDITIONAL_AND(BinaryTree.class),
        CONDITIONAL_OR(BinaryTree.class),
        MULTIPLY_ASSIGNMENT(CompoundAssignmentTree.class),
        DIVIDE_ASSIGNMENT(CompoundAssignmentTree.class),
        REMAINDER_ASSIGNMENT(CompoundAssignmentTree.class),
        PLUS_ASSIGNMENT(CompoundAssignmentTree.class),
        MINUS_ASSIGNMENT(CompoundAssignmentTree.class),
        LEFT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class),
        RIGHT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class),
        UNSIGNED_RIGHT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class),
        AND_ASSIGNMENT(CompoundAssignmentTree.class),
        XOR_ASSIGNMENT(CompoundAssignmentTree.class),
        OR_ASSIGNMENT(CompoundAssignmentTree.class),
        SPREAD(SpreadTree.class),
        YIELD(YieldTree.class),
        NUMBER_LITERAL(LiteralTree.class),
        BOOLEAN_LITERAL(LiteralTree.class),
        STRING_LITERAL(LiteralTree.class),
        NULL_LITERAL(LiteralTree.class),
        OTHER(null);

        private final Class<? extends Tree> associatedInterface;

        private Kind(Class<? extends Tree> intf) {
            this.associatedInterface = intf;
        }

        public Class<? extends Tree> asInterface() {
            return this.associatedInterface;
        }

        public boolean isLiteral() {
            return this.associatedInterface == LiteralTree.class;
        }

        public boolean isExpression() {
            return ExpressionTree.class.isAssignableFrom(this.associatedInterface);
        }

        public boolean isStatement() {
            return StatementTree.class.isAssignableFrom(this.associatedInterface);
        }
    }
}
