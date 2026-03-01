package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.TreeImpl;
import org.openjdk.nashorn.internal.ir.Expression;

abstract class ExpressionTreeImpl
extends TreeImpl
implements ExpressionTree {
    ExpressionTreeImpl(Expression expr) {
        super(expr);
    }
}
