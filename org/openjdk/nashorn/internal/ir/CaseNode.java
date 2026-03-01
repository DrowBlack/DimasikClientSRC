package org.openjdk.nashorn.internal.ir;

import java.util.List;
import org.openjdk.nashorn.internal.codegen.Label;
import org.openjdk.nashorn.internal.ir.Block;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.JoinPredecessor;
import org.openjdk.nashorn.internal.ir.Labels;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LocalVariableConversion;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.Terminal;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

@Immutable
public final class CaseNode
extends Node
implements JoinPredecessor,
Labels,
Terminal {
    private static final long serialVersionUID = 1L;
    private final Expression test;
    private final Block body;
    private final Label entry;
    private final LocalVariableConversion conversion;

    public CaseNode(long token, int finish, Expression test, Block body) {
        super(token, finish);
        this.test = test;
        this.body = body;
        this.entry = new Label("entry");
        this.conversion = null;
    }

    CaseNode(CaseNode caseNode, Expression test, Block body, LocalVariableConversion conversion) {
        super(caseNode);
        this.test = test;
        this.body = body;
        this.entry = new Label(caseNode.entry);
        this.conversion = conversion;
    }

    @Override
    public boolean isTerminal() {
        return this.body.isTerminal();
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterCaseNode(this)) {
            Expression newTest = this.test == null ? null : (Expression)this.test.accept(visitor);
            Block newBody = this.body == null ? null : (Block)this.body.accept(visitor);
            return visitor.leaveCaseNode(this.setTest(newTest).setBody(newBody));
        }
        return this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printTypes) {
        if (this.test != null) {
            sb.append("case ");
            this.test.toString(sb, printTypes);
            sb.append(':');
        } else {
            sb.append("default:");
        }
    }

    public Block getBody() {
        return this.body;
    }

    public Label getEntry() {
        return this.entry;
    }

    public Expression getTest() {
        return this.test;
    }

    public CaseNode setTest(Expression test) {
        if (this.test == test) {
            return this;
        }
        return new CaseNode(this, test, this.body, this.conversion);
    }

    @Override
    public JoinPredecessor setLocalVariableConversion(LexicalContext lc, LocalVariableConversion conversion) {
        if (this.conversion == conversion) {
            return this;
        }
        return new CaseNode(this, this.test, this.body, conversion);
    }

    @Override
    public LocalVariableConversion getLocalVariableConversion() {
        return this.conversion;
    }

    private CaseNode setBody(Block body) {
        if (this.body == body) {
            return this;
        }
        return new CaseNode(this, this.test, body, this.conversion);
    }

    @Override
    public List<Label> getLabels() {
        return List.of(this.entry);
    }
}
