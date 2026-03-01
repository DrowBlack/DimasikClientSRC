package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.CaseTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeImpl;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.CaseNode;

final class CaseTreeImpl
extends TreeImpl
implements CaseTree {
    private final ExpressionTree expression;
    private final List<? extends StatementTree> statements;

    public CaseTreeImpl(CaseNode node, ExpressionTree expression, List<? extends StatementTree> statements) {
        super(node);
        this.expression = expression;
        this.statements = statements;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.CASE;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expression;
    }

    @Override
    public List<? extends StatementTree> getStatements() {
        return this.statements;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitCase(this, data);
    }
}
