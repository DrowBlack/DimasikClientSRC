package com.mojang.datafixers.functions;

import com.mojang.datafixers.functions.PointFree;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;

final class Bang<A>
extends PointFree<Function<A, Void>> {
    Bang() {
    }

    @Override
    public String toString(int level) {
        return "!";
    }

    public boolean equals(Object o) {
        return o instanceof Bang;
    }

    public int hashCode() {
        return Bang.class.hashCode();
    }

    @Override
    public Function<DynamicOps<?>, Function<A, Void>> eval() {
        return ops -> a -> null;
    }
}
