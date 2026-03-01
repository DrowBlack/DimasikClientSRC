package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.BaseNode;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

@Immutable
public final class AccessNode
extends BaseNode {
    private static final long serialVersionUID = 1L;
    private final String property;

    public AccessNode(long token, int finish, Expression base, String property) {
        super(token, finish, base, false, false);
        this.property = property;
    }

    private AccessNode(AccessNode accessNode, Expression base, String property, boolean isFunction, Type type, int id, boolean isSuper) {
        super(accessNode, base, isFunction, type, id, isSuper);
        this.property = property;
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterAccessNode(this)) {
            return visitor.leaveAccessNode(this.setBase((Expression)this.base.accept(visitor)));
        }
        return this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        boolean needsParen = this.tokenType().needsParens(this.getBase().tokenType(), true);
        if (printType) {
            this.optimisticTypeToString(sb);
        }
        if (needsParen) {
            sb.append('(');
        }
        this.base.toString(sb, printType);
        if (needsParen) {
            sb.append(')');
        }
        sb.append('.');
        sb.append(this.property);
    }

    public String getProperty() {
        return this.property;
    }

    private AccessNode setBase(Expression base) {
        if (this.base == base) {
            return this;
        }
        return new AccessNode(this, base, this.property, this.isFunction(), this.type, this.programPoint, this.isSuper());
    }

    @Override
    public AccessNode setType(Type type) {
        if (this.type == type) {
            return this;
        }
        return new AccessNode(this, this.base, this.property, this.isFunction(), type, this.programPoint, this.isSuper());
    }

    @Override
    public AccessNode setProgramPoint(int programPoint) {
        if (this.programPoint == programPoint) {
            return this;
        }
        return new AccessNode(this, this.base, this.property, this.isFunction(), this.type, programPoint, this.isSuper());
    }

    @Override
    public AccessNode setIsFunction() {
        if (this.isFunction()) {
            return this;
        }
        return new AccessNode(this, this.base, this.property, true, this.type, this.programPoint, this.isSuper());
    }

    @Override
    public AccessNode setIsSuper() {
        if (this.isSuper()) {
            return this;
        }
        return new AccessNode(this, this.base, this.property, this.isFunction(), this.type, this.programPoint, true);
    }
}
