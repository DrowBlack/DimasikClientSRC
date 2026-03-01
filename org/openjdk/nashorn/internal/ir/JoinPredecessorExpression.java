package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.JoinPredecessor;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LocalVariableConversion;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

public class JoinPredecessorExpression
extends Expression
implements JoinPredecessor {
    private static final long serialVersionUID = 1L;
    private final Expression expression;
    private final LocalVariableConversion conversion;

    public JoinPredecessorExpression() {
        this(null);
    }

    public JoinPredecessorExpression(Expression expression) {
        this(expression, null);
    }

    private JoinPredecessorExpression(Expression expression, LocalVariableConversion conversion) {
        super(expression == null ? 0L : expression.getToken(), expression == null ? 0 : expression.getStart(), expression == null ? 0 : expression.getFinish());
        this.expression = expression;
        this.conversion = conversion;
    }

    @Override
    public JoinPredecessor setLocalVariableConversion(LexicalContext lc, LocalVariableConversion conversion) {
        if (conversion == this.conversion) {
            return this;
        }
        return new JoinPredecessorExpression(this.expression, conversion);
    }

    @Override
    public Type getType() {
        return this.expression.getType();
    }

    @Override
    public boolean isAlwaysFalse() {
        return this.expression != null && this.expression.isAlwaysFalse();
    }

    @Override
    public boolean isAlwaysTrue() {
        return this.expression != null && this.expression.isAlwaysTrue();
    }

    public Expression getExpression() {
        return this.expression;
    }

    public JoinPredecessorExpression setExpression(Expression expression) {
        if (expression == this.expression) {
            return this;
        }
        return new JoinPredecessorExpression(expression, this.conversion);
    }

    @Override
    public LocalVariableConversion getLocalVariableConversion() {
        return this.conversion;
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterJoinPredecessorExpression(this)) {
            Expression expr = this.getExpression();
            return visitor.leaveJoinPredecessorExpression(expr == null ? this : this.setExpression((Expression)expr.accept(visitor)));
        }
        return this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        if (this.expression != null) {
            this.expression.toString(sb, printType);
        }
        if (this.conversion != null) {
            this.conversion.toString(sb);
        }
    }
}
