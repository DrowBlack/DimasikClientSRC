package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.PropertyKey;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

@Immutable
public final class PropertyNode
extends Node {
    private static final long serialVersionUID = 1L;
    private final Expression key;
    private final Expression value;
    private final FunctionNode getter;
    private final FunctionNode setter;
    private final boolean isStatic;
    private final boolean computed;

    public PropertyNode(long token, int finish, Expression key, Expression value, FunctionNode getter, FunctionNode setter, boolean isStatic, boolean computed) {
        super(token, finish);
        this.key = key;
        this.value = value;
        this.getter = getter;
        this.setter = setter;
        this.isStatic = isStatic;
        this.computed = computed;
    }

    private PropertyNode(PropertyNode propertyNode, Expression key, Expression value, FunctionNode getter, FunctionNode setter, boolean isStatic, boolean computed) {
        super(propertyNode);
        this.key = key;
        this.value = value;
        this.getter = getter;
        this.setter = setter;
        this.isStatic = isStatic;
        this.computed = computed;
    }

    public String getKeyName() {
        return !this.computed && this.key instanceof PropertyKey ? ((PropertyKey)((Object)this.key)).getPropertyName() : null;
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterPropertyNode(this)) {
            return visitor.leavePropertyNode(this.setKey((Expression)this.key.accept(visitor)).setValue(this.value == null ? null : (Expression)this.value.accept(visitor)).setGetter(this.getter == null ? null : (FunctionNode)this.getter.accept((NodeVisitor)visitor)).setSetter(this.setter == null ? null : (FunctionNode)this.setter.accept((NodeVisitor)visitor)));
        }
        return this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        if (this.value instanceof FunctionNode && ((FunctionNode)this.value).getIdent() != null) {
            this.value.toString(sb);
        }
        if (this.value != null) {
            this.key.toString(sb, printType);
            sb.append(": ");
            this.value.toString(sb, printType);
        }
        if (this.getter != null) {
            sb.append(' ');
            this.getter.toString(sb, printType);
        }
        if (this.setter != null) {
            sb.append(' ');
            this.setter.toString(sb, printType);
        }
    }

    public FunctionNode getGetter() {
        return this.getter;
    }

    public PropertyNode setGetter(FunctionNode getter) {
        if (this.getter == getter) {
            return this;
        }
        return new PropertyNode(this, this.key, this.value, getter, this.setter, this.isStatic, this.computed);
    }

    public Expression getKey() {
        return this.key;
    }

    private PropertyNode setKey(Expression key) {
        if (this.key == key) {
            return this;
        }
        return new PropertyNode(this, key, this.value, this.getter, this.setter, this.isStatic, this.computed);
    }

    public FunctionNode getSetter() {
        return this.setter;
    }

    public PropertyNode setSetter(FunctionNode setter) {
        if (this.setter == setter) {
            return this;
        }
        return new PropertyNode(this, this.key, this.value, this.getter, setter, this.isStatic, this.computed);
    }

    public Expression getValue() {
        return this.value;
    }

    public PropertyNode setValue(Expression value) {
        if (this.value == value) {
            return this;
        }
        return new PropertyNode(this, this.key, value, this.getter, this.setter, this.isStatic, this.computed);
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    public boolean isComputed() {
        return this.computed;
    }
}
