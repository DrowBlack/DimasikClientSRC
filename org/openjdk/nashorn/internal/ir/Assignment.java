package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.Node;

public interface Assignment<D extends Expression> {
    public D getAssignmentDest();

    public Expression getAssignmentSource();

    public Node setAssignmentDest(D var1);
}
