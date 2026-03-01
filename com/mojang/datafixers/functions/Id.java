package com.mojang.datafixers.functions;

import com.mojang.datafixers.functions.PointFree;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;

final class Id<A>
extends PointFree<Function<A, A>> {
    public boolean equals(Object obj) {
        return obj instanceof Id;
    }

    public int hashCode() {
        return Id.class.hashCode();
    }

    @Override
    public String toString(int level) {
        return "id";
    }

    @Override
    public Function<DynamicOps<?>, Function<A, A>> eval() {
        return ops -> Function.identity();
    }
}
