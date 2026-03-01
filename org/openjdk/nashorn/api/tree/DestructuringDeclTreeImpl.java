package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.api.tree.VariableTree;
import org.openjdk.nashorn.internal.ir.ExpressionStatement;
import org.openjdk.nashorn.internal.parser.TokenType;

final class DestructuringDeclTreeImpl
extends StatementTreeImpl
implements VariableTree {
    private final TokenType declType;
    private final ExpressionTree lhs;
    private final ExpressionTree init;

    DestructuringDeclTreeImpl(ExpressionStatement exprStat, ExpressionTree lhs, ExpressionTree init) {
        super(exprStat);
        assert (exprStat.destructuringDeclarationType() != null) : "expecting a destructuring decl. statement";
        this.declType = exprStat.destructuringDeclarationType();
        this.lhs = lhs;
        this.init = init;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.VARIABLE;
    }

    @Override
    public ExpressionTree getBinding() {
        return this.lhs;
    }

    @Override
    public ExpressionTree getInitializer() {
        return this.init;
    }

    @Override
    public boolean isConst() {
        return this.declType == TokenType.CONST;
    }

    @Override
    public boolean isLet() {
        return this.declType == TokenType.LET;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitVariable(this, data);
    }
}
