package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface RegExpLiteralTree
extends ExpressionTree {
    public String getPattern();

    public String getOptions();
}
