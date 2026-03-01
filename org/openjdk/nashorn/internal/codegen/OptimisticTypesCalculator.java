package org.openjdk.nashorn.internal.codegen;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import org.openjdk.nashorn.internal.codegen.Compiler;
import org.openjdk.nashorn.internal.ir.AccessNode;
import org.openjdk.nashorn.internal.ir.BinaryNode;
import org.openjdk.nashorn.internal.ir.CallNode;
import org.openjdk.nashorn.internal.ir.CatchNode;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.ExpressionStatement;
import org.openjdk.nashorn.internal.ir.ForNode;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.IfNode;
import org.openjdk.nashorn.internal.ir.IndexNode;
import org.openjdk.nashorn.internal.ir.JoinPredecessorExpression;
import org.openjdk.nashorn.internal.ir.LiteralNode;
import org.openjdk.nashorn.internal.ir.LoopNode;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.ObjectNode;
import org.openjdk.nashorn.internal.ir.Optimistic;
import org.openjdk.nashorn.internal.ir.PropertyNode;
import org.openjdk.nashorn.internal.ir.Symbol;
import org.openjdk.nashorn.internal.ir.TernaryNode;
import org.openjdk.nashorn.internal.ir.UnaryNode;
import org.openjdk.nashorn.internal.ir.VarNode;
import org.openjdk.nashorn.internal.ir.WhileNode;
import org.openjdk.nashorn.internal.ir.visitor.SimpleNodeVisitor;
import org.openjdk.nashorn.internal.parser.TokenType;
import org.openjdk.nashorn.internal.runtime.UnwarrantedOptimismException;

final class OptimisticTypesCalculator
extends SimpleNodeVisitor {
    final Compiler compiler;
    final Deque<BitSet> neverOptimistic = new ArrayDeque<BitSet>();

    OptimisticTypesCalculator(Compiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public boolean enterAccessNode(AccessNode accessNode) {
        this.tagNeverOptimistic(accessNode.getBase());
        return true;
    }

    @Override
    public boolean enterPropertyNode(PropertyNode propertyNode) {
        if ("__proto__".equals(propertyNode.getKeyName())) {
            this.tagNeverOptimistic(propertyNode.getValue());
        }
        return super.enterPropertyNode(propertyNode);
    }

    @Override
    public boolean enterBinaryNode(BinaryNode binaryNode) {
        if (binaryNode.isAssignment()) {
            Symbol symbol;
            Expression lhs = binaryNode.lhs();
            if (!binaryNode.isSelfModifying()) {
                this.tagNeverOptimistic(lhs);
            }
            if (lhs instanceof IdentNode && (symbol = ((IdentNode)lhs).getSymbol()).isInternal() && !binaryNode.rhs().isSelfModifying()) {
                this.tagNeverOptimistic(binaryNode.rhs());
            }
        } else if (binaryNode.isTokenType(TokenType.INSTANCEOF) || binaryNode.isTokenType(TokenType.EQ_STRICT) || binaryNode.isTokenType(TokenType.NE_STRICT)) {
            this.tagNeverOptimistic(binaryNode.lhs());
            this.tagNeverOptimistic(binaryNode.rhs());
        }
        return true;
    }

    @Override
    public boolean enterCallNode(CallNode callNode) {
        this.tagNeverOptimistic(callNode.getFunction());
        return true;
    }

    @Override
    public boolean enterCatchNode(CatchNode catchNode) {
        this.tagNeverOptimistic(catchNode.getExceptionCondition());
        return true;
    }

    @Override
    public boolean enterExpressionStatement(ExpressionStatement expressionStatement) {
        Expression expr = expressionStatement.getExpression();
        if (!expr.isSelfModifying()) {
            this.tagNeverOptimistic(expr);
        }
        return true;
    }

    @Override
    public boolean enterForNode(ForNode forNode) {
        if (forNode.isForInOrOf()) {
            this.tagNeverOptimistic(forNode.getModify());
        } else {
            this.tagNeverOptimisticLoopTest(forNode);
        }
        return true;
    }

    @Override
    public boolean enterFunctionNode(FunctionNode functionNode) {
        if (!this.neverOptimistic.isEmpty() && this.compiler.isOnDemandCompilation()) {
            return false;
        }
        this.neverOptimistic.push(new BitSet());
        return true;
    }

    @Override
    public boolean enterIfNode(IfNode ifNode) {
        this.tagNeverOptimistic(ifNode.getTest());
        return true;
    }

    @Override
    public boolean enterIndexNode(IndexNode indexNode) {
        this.tagNeverOptimistic(indexNode.getBase());
        return true;
    }

    @Override
    public boolean enterTernaryNode(TernaryNode ternaryNode) {
        this.tagNeverOptimistic(ternaryNode.getTest());
        return true;
    }

    @Override
    public boolean enterUnaryNode(UnaryNode unaryNode) {
        if (unaryNode.isTokenType(TokenType.NOT) || unaryNode.isTokenType(TokenType.NEW)) {
            this.tagNeverOptimistic(unaryNode.getExpression());
        }
        return true;
    }

    @Override
    public boolean enterVarNode(VarNode varNode) {
        this.tagNeverOptimistic(varNode.getName());
        return true;
    }

    @Override
    public boolean enterObjectNode(ObjectNode objectNode) {
        if (objectNode.getSplitRanges() != null) {
            return false;
        }
        return super.enterObjectNode(objectNode);
    }

    @Override
    public boolean enterLiteralNode(LiteralNode<?> literalNode) {
        if (literalNode.isArray() && ((LiteralNode.ArrayLiteralNode)literalNode).getSplitRanges() != null) {
            return false;
        }
        return super.enterLiteralNode(literalNode);
    }

    @Override
    public boolean enterWhileNode(WhileNode whileNode) {
        this.tagNeverOptimisticLoopTest(whileNode);
        return true;
    }

    @Override
    protected Node leaveDefault(Node node) {
        if (node instanceof Optimistic) {
            return this.leaveOptimistic((Optimistic)((Object)node));
        }
        return node;
    }

    @Override
    public Node leaveFunctionNode(FunctionNode functionNode) {
        this.neverOptimistic.pop();
        return functionNode;
    }

    @Override
    public Node leaveIdentNode(IdentNode identNode) {
        Symbol symbol = identNode.getSymbol();
        if (symbol == null) {
            assert (identNode.isPropertyName());
            return identNode;
        }
        if (symbol.isBytecodeLocal()) {
            return identNode;
        }
        if (symbol.isParam() && this.lc.getCurrentFunction().isVarArg()) {
            return identNode.setType(identNode.getMostPessimisticType());
        }
        assert (symbol.isScope());
        return this.leaveOptimistic(identNode);
    }

    private Expression leaveOptimistic(Optimistic opt) {
        int pp = opt.getProgramPoint();
        if (UnwarrantedOptimismException.isValid(pp) && !this.neverOptimistic.peek().get(pp)) {
            return (Expression)((Object)opt.setType(this.compiler.getOptimisticType(opt)));
        }
        return (Expression)((Object)opt);
    }

    private void tagNeverOptimistic(Expression expr) {
        int pp;
        if (expr instanceof Optimistic && UnwarrantedOptimismException.isValid(pp = ((Optimistic)((Object)expr)).getProgramPoint())) {
            this.neverOptimistic.peek().set(pp);
        }
    }

    private void tagNeverOptimisticLoopTest(LoopNode loopNode) {
        JoinPredecessorExpression test = loopNode.getTest();
        if (test != null) {
            this.tagNeverOptimistic(test.getExpression());
        }
    }
}
