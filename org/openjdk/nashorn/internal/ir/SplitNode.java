package org.openjdk.nashorn.internal.ir;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import org.openjdk.nashorn.internal.codegen.CompileUnit;
import org.openjdk.nashorn.internal.ir.Block;
import org.openjdk.nashorn.internal.ir.CompileUnitHolder;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LexicalContextStatement;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

@Immutable
public class SplitNode
extends LexicalContextStatement
implements CompileUnitHolder {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final CompileUnit compileUnit;
    private final Block body;

    public SplitNode(String name, Block body, CompileUnit compileUnit) {
        super(body.getFirstStatementLineNumber(), body.getToken(), body.getFinish());
        this.name = name;
        this.body = body;
        this.compileUnit = compileUnit;
    }

    private SplitNode(SplitNode splitNode, Block body, CompileUnit compileUnit) {
        super(splitNode);
        this.name = splitNode.name;
        this.body = body;
        this.compileUnit = compileUnit;
    }

    public Block getBody() {
        return this.body;
    }

    private SplitNode setBody(LexicalContext lc, Block body) {
        if (this.body == body) {
            return this;
        }
        return Node.replaceInLexicalContext(lc, this, new SplitNode(this, body, this.compileUnit));
    }

    @Override
    public Node accept(LexicalContext lc, NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterSplitNode(this)) {
            return visitor.leaveSplitNode(this.setBody(lc, (Block)this.body.accept(visitor)));
        }
        return this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        sb.append("<split>(");
        sb.append(this.compileUnit.getClass().getSimpleName());
        sb.append(") ");
        this.body.toString(sb, printType);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public CompileUnit getCompileUnit() {
        return this.compileUnit;
    }

    public SplitNode setCompileUnit(LexicalContext lc, CompileUnit compileUnit) {
        if (this.compileUnit == compileUnit) {
            return this;
        }
        return Node.replaceInLexicalContext(lc, this, new SplitNode(this, this.body, compileUnit));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new NotSerializableException(this.getClass().getName());
    }
}
