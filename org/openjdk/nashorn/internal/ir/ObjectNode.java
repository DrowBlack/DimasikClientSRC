package org.openjdk.nashorn.internal.ir;

import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LexicalContextNode;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.PropertyNode;
import org.openjdk.nashorn.internal.ir.Splittable;
import org.openjdk.nashorn.internal.ir.annotations.Ignore;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

@Immutable
public final class ObjectNode
extends Expression
implements LexicalContextNode,
Splittable {
    private static final long serialVersionUID = 1L;
    private final List<PropertyNode> elements;
    @Ignore
    private final List<Splittable.SplitRange> splitRanges;

    public ObjectNode(long token, int finish, List<PropertyNode> elements) {
        super(token, finish);
        this.elements = elements;
        this.splitRanges = null;
        assert (elements instanceof RandomAccess) : "Splitting requires random access lists";
    }

    private ObjectNode(ObjectNode objectNode, List<PropertyNode> elements, List<Splittable.SplitRange> splitRanges) {
        super(objectNode);
        this.elements = elements;
        this.splitRanges = splitRanges;
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        return LexicalContextNode.Acceptor.accept(this, visitor);
    }

    @Override
    public Node accept(LexicalContext lc, NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterObjectNode(this)) {
            return visitor.leaveObjectNode(this.setElements(lc, Node.accept(visitor, this.elements)));
        }
        return this;
    }

    @Override
    public Type getType() {
        return Type.OBJECT;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        sb.append('{');
        if (!this.elements.isEmpty()) {
            sb.append(' ');
            boolean first = true;
            for (Node node : this.elements) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                node.toString(sb, printType);
            }
            sb.append(' ');
        }
        sb.append('}');
    }

    public List<PropertyNode> getElements() {
        return Collections.unmodifiableList(this.elements);
    }

    private ObjectNode setElements(LexicalContext lc, List<PropertyNode> elements) {
        if (this.elements == elements) {
            return this;
        }
        return Node.replaceInLexicalContext(lc, this, new ObjectNode(this, elements, this.splitRanges));
    }

    public ObjectNode setSplitRanges(LexicalContext lc, List<Splittable.SplitRange> splitRanges) {
        if (this.splitRanges == splitRanges) {
            return this;
        }
        return Node.replaceInLexicalContext(lc, this, new ObjectNode(this, this.elements, splitRanges));
    }

    @Override
    public List<Splittable.SplitRange> getSplitRanges() {
        return this.splitRanges == null ? null : Collections.unmodifiableList(this.splitRanges);
    }
}
