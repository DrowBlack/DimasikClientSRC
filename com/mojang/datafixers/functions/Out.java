package com.mojang.datafixers.functions;

import com.mojang.datafixers.functions.PointFree;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;

final class Out<A>
extends PointFree<Function<A, A>> {
    private final RecursivePoint.RecursivePointType<A> type;

    public Out(RecursivePoint.RecursivePointType<A> type) {
        this.type = type;
    }

    @Override
    public String toString(int level) {
        return "Out[" + this.type + "]";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Out && Objects.equals(this.type, ((Out)obj).type);
    }

    public int hashCode() {
        return Objects.hash(this.type);
    }

    @Override
    public Function<DynamicOps<?>, Function<A, A>> eval() {
        return ops -> Function.identity();
    }
}
