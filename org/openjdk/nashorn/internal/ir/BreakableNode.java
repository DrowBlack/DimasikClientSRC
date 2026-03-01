package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.codegen.Label;
import org.openjdk.nashorn.internal.ir.JoinPredecessor;
import org.openjdk.nashorn.internal.ir.Labels;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LexicalContextNode;
import org.openjdk.nashorn.internal.ir.Node;

public interface BreakableNode
extends LexicalContextNode,
JoinPredecessor,
Labels {
    public Node ensureUniqueLabels(LexicalContext var1);

    public boolean isBreakableWithoutLabel();

    public Label getBreakLabel();
}
