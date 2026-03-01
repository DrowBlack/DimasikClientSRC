package org.openjdk.nashorn.internal.ir;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.BinaryNode;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.UnaryNode;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;
import org.openjdk.nashorn.internal.parser.TokenType;

@Immutable
public class RuntimeNode
extends Expression {
    private static final long serialVersionUID = 1L;
    private final Request request;
    private final List<Expression> args;

    public RuntimeNode(long token, int finish, Request request, List<Expression> args) {
        super(token, finish);
        this.request = request;
        this.args = args;
    }

    private RuntimeNode(RuntimeNode runtimeNode, Request request, List<Expression> args) {
        super(runtimeNode);
        this.request = request;
        this.args = args;
    }

    public RuntimeNode(long token, int finish, Request request, Expression ... args) {
        this(token, finish, request, Arrays.asList(args));
    }

    public RuntimeNode(Expression parent, Request request, Expression ... args) {
        this(parent, request, Arrays.asList(args));
    }

    public RuntimeNode(Expression parent, Request request, List<Expression> args) {
        super(parent);
        this.request = request;
        this.args = args;
    }

    public RuntimeNode(UnaryNode parent, Request request) {
        this((Expression)parent, request, parent.getExpression());
    }

    public RuntimeNode(BinaryNode parent) {
        this((Expression)parent, Request.requestFor(parent), parent.lhs(), parent.rhs());
    }

    public RuntimeNode setRequest(Request request) {
        if (this.request == request) {
            return this;
        }
        return new RuntimeNode(this, request, this.args);
    }

    @Override
    public Type getType() {
        return this.request.getReturnType();
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterRuntimeNode(this)) {
            return visitor.leaveRuntimeNode(this.setArgs(Node.accept(visitor, this.args)));
        }
        return this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        sb.append("ScriptRuntime.");
        sb.append((Object)this.request);
        sb.append('(');
        boolean first = true;
        for (Node node : this.args) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            node.toString(sb, printType);
        }
        sb.append(')');
    }

    public List<Expression> getArgs() {
        return Collections.unmodifiableList(this.args);
    }

    public RuntimeNode setArgs(List<Expression> args) {
        if (this.args == args) {
            return this;
        }
        return new RuntimeNode(this, this.request, args);
    }

    public Request getRequest() {
        return this.request;
    }

    public boolean isPrimitive() {
        for (Expression arg : this.args) {
            if (!arg.getType().isObject()) continue;
            return false;
        }
        return true;
    }

    public static final class Request
    extends Enum<Request> {
        public static final /* enum */ Request ADD = new Request(TokenType.ADD, Type.OBJECT, 2, true);
        public static final /* enum */ Request DEBUGGER = new Request();
        public static final /* enum */ Request NEW = new Request();
        public static final /* enum */ Request TYPEOF = new Request();
        public static final /* enum */ Request REFERENCE_ERROR = new Request();
        public static final /* enum */ Request EQ_STRICT = new Request(TokenType.EQ_STRICT, Type.BOOLEAN, 2, true);
        public static final /* enum */ Request EQ = new Request(TokenType.EQ, Type.BOOLEAN, 2, true);
        public static final /* enum */ Request GE = new Request(TokenType.GE, Type.BOOLEAN, 2, true);
        public static final /* enum */ Request GT = new Request(TokenType.GT, Type.BOOLEAN, 2, true);
        public static final /* enum */ Request IN = new Request(TokenType.IN, Type.BOOLEAN, 2);
        public static final /* enum */ Request INSTANCEOF = new Request(TokenType.INSTANCEOF, Type.BOOLEAN, 2);
        public static final /* enum */ Request LE = new Request(TokenType.LE, Type.BOOLEAN, 2, true);
        public static final /* enum */ Request LT = new Request(TokenType.LT, Type.BOOLEAN, 2, true);
        public static final /* enum */ Request NE_STRICT = new Request(TokenType.NE_STRICT, Type.BOOLEAN, 2, true);
        public static final /* enum */ Request NE = new Request(TokenType.NE, Type.BOOLEAN, 2, true);
        public static final /* enum */ Request IS_UNDEFINED = new Request(TokenType.EQ_STRICT, Type.BOOLEAN, 2);
        public static final /* enum */ Request IS_NOT_UNDEFINED = new Request(TokenType.NE_STRICT, Type.BOOLEAN, 2);
        public static final /* enum */ Request GET_TEMPLATE_OBJECT = new Request(TokenType.TEMPLATE, Type.SCRIPT_OBJECT, 2);
        private final TokenType tokenType;
        private final Type returnType;
        private final int arity;
        private final boolean canSpecialize;
        private static final /* synthetic */ Request[] $VALUES;

        public static Request[] values() {
            return (Request[])$VALUES.clone();
        }

        public static Request valueOf(String name) {
            return Enum.valueOf(Request.class, name);
        }

        private Request() {
            this(TokenType.VOID, Type.OBJECT, 0);
        }

        private Request(TokenType tokenType, Type returnType, int arity) {
            this(tokenType, returnType, arity, false);
        }

        private Request(TokenType tokenType, Type returnType, int arity, boolean canSpecialize) {
            this.tokenType = tokenType;
            this.returnType = returnType;
            this.arity = arity;
            this.canSpecialize = canSpecialize;
        }

        public boolean canSpecialize() {
            return this.canSpecialize;
        }

        public int getArity() {
            return this.arity;
        }

        public Type getReturnType() {
            return this.returnType;
        }

        public TokenType getTokenType() {
            return this.tokenType;
        }

        public String nonStrictName() {
            switch (this) {
                case NE_STRICT: {
                    return NE.name();
                }
                case EQ_STRICT: {
                    return EQ.name();
                }
            }
            return this.name();
        }

        public static Request requestFor(Expression node) {
            switch (node.tokenType()) {
                case TYPEOF: {
                    return TYPEOF;
                }
                case IN: {
                    return IN;
                }
                case INSTANCEOF: {
                    return INSTANCEOF;
                }
                case EQ_STRICT: {
                    return EQ_STRICT;
                }
                case NE_STRICT: {
                    return NE_STRICT;
                }
                case EQ: {
                    return EQ;
                }
                case NE: {
                    return NE;
                }
                case LT: {
                    return LT;
                }
                case LE: {
                    return LE;
                }
                case GT: {
                    return GT;
                }
                case GE: {
                    return GE;
                }
            }
            assert (false);
            return null;
        }

        public static boolean isUndefinedCheck(Request request) {
            return request == IS_UNDEFINED || request == IS_NOT_UNDEFINED;
        }

        public static boolean isEQ(Request request) {
            return request == EQ || request == EQ_STRICT;
        }

        public static boolean isNE(Request request) {
            return request == NE || request == NE_STRICT;
        }

        public static boolean isStrict(Request request) {
            return request == EQ_STRICT || request == NE_STRICT;
        }

        public static Request reverse(Request request) {
            switch (request) {
                case NE_STRICT: 
                case EQ_STRICT: 
                case EQ: 
                case NE: {
                    return request;
                }
                case LE: {
                    return GE;
                }
                case LT: {
                    return GT;
                }
                case GE: {
                    return LE;
                }
                case GT: {
                    return LT;
                }
            }
            return null;
        }

        public static Request invert(Request request) {
            switch (request) {
                case EQ: {
                    return NE;
                }
                case EQ_STRICT: {
                    return NE_STRICT;
                }
                case NE: {
                    return EQ;
                }
                case NE_STRICT: {
                    return EQ_STRICT;
                }
                case LE: {
                    return GT;
                }
                case LT: {
                    return GE;
                }
                case GE: {
                    return LT;
                }
                case GT: {
                    return LE;
                }
            }
            return null;
        }

        public static boolean isComparison(Request request) {
            switch (request) {
                case NE_STRICT: 
                case EQ_STRICT: 
                case EQ: 
                case NE: 
                case LE: 
                case LT: 
                case GE: 
                case GT: 
                case IS_UNDEFINED: 
                case IS_NOT_UNDEFINED: {
                    return true;
                }
            }
            return false;
        }

        static {
            $VALUES = new Request[]{ADD, DEBUGGER, NEW, TYPEOF, REFERENCE_ERROR, EQ_STRICT, EQ, GE, GT, IN, INSTANCEOF, LE, LT, NE_STRICT, NE, IS_UNDEFINED, IS_NOT_UNDEFINED, GET_TEMPLATE_OBJECT};
        }
    }
}
