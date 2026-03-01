package org.openjdk.nashorn.internal.ir;

import java.util.Collections;
import java.util.List;
import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.PropertyNode;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

public class ClassNode
extends Expression {
    private static final long serialVersionUID = 1L;
    private final IdentNode ident;
    private final Expression classHeritage;
    private final PropertyNode constructor;
    private final List<PropertyNode> classElements;
    private final int line;
    private final boolean isStatement;

    public ClassNode(int line, long token, int finish, IdentNode ident, Expression classHeritage, PropertyNode constructor, List<PropertyNode> classElements, boolean isStatement) {
        super(token, finish);
        this.line = line;
        this.ident = ident;
        this.classHeritage = classHeritage;
        this.constructor = constructor;
        this.classElements = classElements;
        this.isStatement = isStatement;
    }

    public IdentNode getIdent() {
        return this.ident;
    }

    public Expression getClassHeritage() {
        return this.classHeritage;
    }

    public PropertyNode getConstructor() {
        return this.constructor;
    }

    public List<PropertyNode> getClassElements() {
        return Collections.unmodifiableList(this.classElements);
    }

    public boolean isStatement() {
        return this.isStatement;
    }

    public int getLineNumber() {
        return this.line;
    }

    @Override
    public Type getType() {
        return Type.OBJECT;
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterClassNode(this)) {
            return visitor.leaveClassNode(this);
        }
        return this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        sb.append("class");
        if (this.ident != null) {
            sb.append(' ');
            this.ident.toString(sb, printType);
        }
        if (this.classHeritage != null) {
            sb.append(" extends");
            this.classHeritage.toString(sb, printType);
        }
        sb.append(" {");
        if (this.constructor != null) {
            this.constructor.toString(sb, printType);
        }
        for (PropertyNode classElement : this.classElements) {
            sb.append(" ");
            classElement.toString(sb, printType);
        }
        sb.append("}");
    }
}
