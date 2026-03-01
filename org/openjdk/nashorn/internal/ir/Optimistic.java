package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.codegen.types.Type;

public interface Optimistic {
    public int getProgramPoint();

    public Optimistic setProgramPoint(int var1);

    public boolean canBeOptimistic();

    public Type getMostOptimisticType();

    public Type getMostPessimisticType();

    public Optimistic setType(Type var1);
}
