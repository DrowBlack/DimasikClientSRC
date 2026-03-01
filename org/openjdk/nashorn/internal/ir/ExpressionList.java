package org.openjdk.nashorn.internal.ir;

import java.util.Collections;
import java.util.List;
import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

public final class ExpressionList
extends Expression {
    private static final long serialVersionUID = 1L;
    private final List<Expression> expressions;

    public ExpressionList(long token, int finish, List<Expression> expressions) {
        super(token, finish);
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return Collections.unmodifiableList(this.expressions);
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        sb.append("(");
        boolean first = true;
        for (Expression expression : this.expressions) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            expression.toString(sb, printType);
        }
        sb.append(")");
    }
}
