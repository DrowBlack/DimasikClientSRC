package com.mojang.datafixers.functions;

import com.mojang.datafixers.functions.PointFree;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;

final class In<A>
extends PointFree<Function<A, A>> {
    protected final RecursivePoint.RecursivePointType<A> type;

    public In(RecursivePoint.RecursivePointType<A> type) {
        this.type = type;
    }

    @Override
    public String toString(int level) {
        return "In[" + this.type + "]";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof In && Objects.equals(this.type, ((In)obj).type);
    }

    public int hashCode() {
        return Objects.hash(this.type);
    }

    @Override
    public Function<DynamicOps<?>, Function<A, A>> eval() {
        return ops -> Function.identity();
    }
}
