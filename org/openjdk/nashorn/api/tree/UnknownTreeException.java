package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.Tree;

public class UnknownTreeException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final transient Tree tree;
    private final transient Object parameter;

    public UnknownTreeException(Tree t, Object p) {
        super("Unknown tree: " + t);
        this.tree = t;
        this.parameter = p;
    }

    public Tree getUnknownTree() {
        return this.tree;
    }

    public Object getArgument() {
        return this.parameter;
    }
}
