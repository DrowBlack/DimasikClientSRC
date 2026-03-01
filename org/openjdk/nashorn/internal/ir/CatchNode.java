package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.ir.Block;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LiteralNode;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.ObjectNode;
import org.openjdk.nashorn.internal.ir.Statement;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

@Immutable
public final class CatchNode
extends Statement {
    private static final long serialVersionUID = 1L;
    private final Expression exception;
    private final Expression exceptionCondition;
    private final Block body;
    private final boolean isSyntheticRethrow;

    public CatchNode(int lineNumber, long token, int finish, Expression exception, Expression exceptionCondition, Block body, boolean isSyntheticRethrow) {
        super(lineNumber, token, finish);
        if (exception instanceof IdentNode) {
            this.exception = ((IdentNode)exception).setIsInitializedHere();
        } else if (exception instanceof LiteralNode.ArrayLiteralNode || exception instanceof ObjectNode) {
            this.exception = exception;
        } else {
            throw new IllegalArgumentException("invalid catch parameter");
        }
        this.exceptionCondition = exceptionCondition;
        this.body = body;
        this.isSyntheticRethrow = isSyntheticRethrow;
    }

    private CatchNode(CatchNode catchNode, Expression exception, Expression exceptionCondition, Block body, boolean isSyntheticRethrow) {
        super(catchNode);
        this.exception = exception;
        this.exceptionCondition = exceptionCondition;
        this.body = body;
        this.isSyntheticRethrow = isSyntheticRethrow;
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterCatchNode(this)) {
            return visitor.leaveCatchNode(this.setException((Expression)this.exception.accept(visitor)).setExceptionCondition(this.exceptionCondition == null ? null : (Expression)this.exceptionCondition.accept(visitor)).setBody((Block)this.body.accept(visitor)));
        }
        return this;
    }

    @Override
    public boolean isTerminal() {
        return this.body.isTerminal();
    }

    @Override
    public void toString(StringBuilder sb, boolean printTypes) {
        sb.append(" catch (");
        this.exception.toString(sb, printTypes);
        if (this.exceptionCondition != null) {
            sb.append(" if ");
            this.exceptionCondition.toString(sb, printTypes);
        }
        sb.append(')');
    }

    public Expression getException() {
        return this.exception;
    }

    public IdentNode getExceptionIdentifier() {
        return (IdentNode)this.exception;
    }

    public Expression getExceptionCondition() {
        return this.exceptionCondition;
    }

    public CatchNode setExceptionCondition(Expression exceptionCondition) {
        if (this.exceptionCondition == exceptionCondition) {
            return this;
        }
        return new CatchNode(this, this.exception, exceptionCondition, this.body, this.isSyntheticRethrow);
    }

    public Block getBody() {
        return this.body;
    }

    public CatchNode setException(Expression exception) {
        if (this.exception == exception) {
            return this;
        }
        if (!(exception instanceof IdentNode || exception instanceof LiteralNode.ArrayLiteralNode || exception instanceof ObjectNode)) {
            throw new IllegalArgumentException("invalid catch parameter");
        }
        return new CatchNode(this, exception, this.exceptionCondition, this.body, this.isSyntheticRethrow);
    }

    private CatchNode setBody(Block body) {
        if (this.body == body) {
            return this;
        }
        return new CatchNode(this, this.exception, this.exceptionCondition, body, this.isSyntheticRethrow);
    }

    public boolean isSyntheticRethrow() {
        return this.isSyntheticRethrow;
    }
}
