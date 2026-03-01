package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.PropertyTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface ClassDeclarationTree
extends StatementTree {
    public IdentifierTree getName();

    public ExpressionTree getClassHeritage();

    public PropertyTree getConstructor();

    public List<? extends PropertyTree> getClassElements();
}
