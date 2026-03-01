package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.parser.TokenType;

abstract class TreeImpl
implements Tree {
    protected final Node node;

    TreeImpl(Node node) {
        this.node = node;
    }

    @Override
    public long getStartPosition() {
        return this.node.getStart();
    }

    @Override
    public long getEndPosition() {
        return this.node.getFinish();
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitUnknown(this, data);
    }

    static Tree.Kind getOperator(TokenType tt) {
        switch (tt) {
            case NEW: {
                return Tree.Kind.NEW;
            }
            case NOT: {
                return Tree.Kind.LOGICAL_COMPLEMENT;
            }
            case NE: {
                return Tree.Kind.NOT_EQUAL_TO;
            }
            case NE_STRICT: {
                return Tree.Kind.STRICT_NOT_EQUAL_TO;
            }
            case MOD: {
                return Tree.Kind.REMAINDER;
            }
            case ASSIGN_MOD: {
                return Tree.Kind.REMAINDER_ASSIGNMENT;
            }
            case BIT_AND: {
                return Tree.Kind.AND;
            }
            case AND: {
                return Tree.Kind.CONDITIONAL_AND;
            }
            case ASSIGN_BIT_AND: {
                return Tree.Kind.AND_ASSIGNMENT;
            }
            case MUL: {
                return Tree.Kind.MULTIPLY;
            }
            case ASSIGN_MUL: {
                return Tree.Kind.MULTIPLY_ASSIGNMENT;
            }
            case POS: {
                return Tree.Kind.UNARY_PLUS;
            }
            case ADD: {
                return Tree.Kind.PLUS;
            }
            case INCPREFIX: {
                return Tree.Kind.PREFIX_INCREMENT;
            }
            case INCPOSTFIX: {
                return Tree.Kind.POSTFIX_INCREMENT;
            }
            case ASSIGN_ADD: {
                return Tree.Kind.PLUS_ASSIGNMENT;
            }
            case NEG: {
                return Tree.Kind.UNARY_MINUS;
            }
            case SUB: {
                return Tree.Kind.MINUS;
            }
            case DECPREFIX: {
                return Tree.Kind.PREFIX_DECREMENT;
            }
            case DECPOSTFIX: {
                return Tree.Kind.POSTFIX_DECREMENT;
            }
            case ASSIGN_SUB: {
                return Tree.Kind.MINUS_ASSIGNMENT;
            }
            case DIV: {
                return Tree.Kind.DIVIDE;
            }
            case ASSIGN_DIV: {
                return Tree.Kind.DIVIDE_ASSIGNMENT;
            }
            case LT: {
                return Tree.Kind.LESS_THAN;
            }
            case SHL: {
                return Tree.Kind.LEFT_SHIFT;
            }
            case ASSIGN_SHL: {
                return Tree.Kind.LEFT_SHIFT_ASSIGNMENT;
            }
            case LE: {
                return Tree.Kind.LESS_THAN_EQUAL;
            }
            case ASSIGN: {
                return Tree.Kind.ASSIGNMENT;
            }
            case EQ: {
                return Tree.Kind.EQUAL_TO;
            }
            case EQ_STRICT: {
                return Tree.Kind.STRICT_EQUAL_TO;
            }
            case GT: {
                return Tree.Kind.GREATER_THAN;
            }
            case GE: {
                return Tree.Kind.GREATER_THAN_EQUAL;
            }
            case SAR: {
                return Tree.Kind.RIGHT_SHIFT;
            }
            case ASSIGN_SAR: {
                return Tree.Kind.RIGHT_SHIFT_ASSIGNMENT;
            }
            case SHR: {
                return Tree.Kind.UNSIGNED_RIGHT_SHIFT;
            }
            case ASSIGN_SHR: {
                return Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT;
            }
            case TERNARY: {
                return Tree.Kind.CONDITIONAL_EXPRESSION;
            }
            case BIT_XOR: {
                return Tree.Kind.XOR;
            }
            case ASSIGN_BIT_XOR: {
                return Tree.Kind.XOR_ASSIGNMENT;
            }
            case BIT_OR: {
                return Tree.Kind.OR;
            }
            case ASSIGN_BIT_OR: {
                return Tree.Kind.OR_ASSIGNMENT;
            }
            case OR: {
                return Tree.Kind.CONDITIONAL_OR;
            }
            case BIT_NOT: {
                return Tree.Kind.BITWISE_COMPLEMENT;
            }
            case DELETE: {
                return Tree.Kind.DELETE;
            }
            case SPREAD_ARRAY: 
            case SPREAD_ARGUMENT: {
                return Tree.Kind.SPREAD;
            }
            case TYPEOF: {
                return Tree.Kind.TYPEOF;
            }
            case VOID: {
                return Tree.Kind.VOID;
            }
            case YIELD: {
                return Tree.Kind.YIELD;
            }
            case IN: {
                return Tree.Kind.IN;
            }
            case INSTANCEOF: {
                return Tree.Kind.INSTANCE_OF;
            }
            case COMMARIGHT: {
                return Tree.Kind.COMMA;
            }
        }
        throw new AssertionError((Object)("should not reach here: " + tt));
    }
}
