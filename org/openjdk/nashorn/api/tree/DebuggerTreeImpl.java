package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.DebuggerTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.DebuggerNode;

final class DebuggerTreeImpl
extends StatementTreeImpl
implements DebuggerTree {
    DebuggerTreeImpl(DebuggerNode node) {
        super(node);
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.DEBUGGER;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitDebugger(this, data);
    }
}
