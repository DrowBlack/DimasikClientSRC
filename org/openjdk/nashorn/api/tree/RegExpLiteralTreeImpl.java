package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.RegExpLiteralTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.LiteralNode;
import org.openjdk.nashorn.internal.parser.Lexer;

final class RegExpLiteralTreeImpl
extends ExpressionTreeImpl
implements RegExpLiteralTree {
    private final String pattern;
    private final String options;

    RegExpLiteralTreeImpl(LiteralNode<?> node) {
        super(node);
        assert (node.getValue() instanceof Lexer.RegexToken) : "regexp expected";
        Lexer.RegexToken regex = (Lexer.RegexToken)node.getValue();
        this.pattern = regex.getExpression();
        this.options = regex.getOptions();
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.REGEXP_LITERAL;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public String getOptions() {
        return this.options;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitRegExpLiteral(this, data);
    }
}
