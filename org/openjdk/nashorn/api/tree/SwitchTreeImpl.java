package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.CaseTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.SwitchTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.SwitchNode;

final class SwitchTreeImpl
extends StatementTreeImpl
implements SwitchTree {
    private final ExpressionTree expr;
    private final List<? extends CaseTree> cases;

    SwitchTreeImpl(SwitchNode node, ExpressionTree expr, List<? extends CaseTree> cases) {
        super(node);
        this.expr = expr;
        this.cases = cases;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.SWITCH;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expr;
    }

    @Override
    public List<? extends CaseTree> getCases() {
        return this.cases;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitSwitch(this, data);
    }
}
