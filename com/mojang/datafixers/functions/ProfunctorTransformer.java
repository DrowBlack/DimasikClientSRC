package com.mojang.datafixers.functions;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.functions.PointFree;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.optics.Optic;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;

final class ProfunctorTransformer<S, T, A, B>
extends PointFree<Function<Function<A, B>, Function<S, T>>> {
    protected final Optic<? super FunctionType.Instance.Mu, S, T, A, B> optic;
    protected final Function<App2<FunctionType.Mu, A, B>, App2<FunctionType.Mu, S, T>> func;
    private final Function<Function<A, B>, Function<S, T>> unwrappedFunction;

    public ProfunctorTransformer(Optic<? super FunctionType.Instance.Mu, S, T, A, B> optic) {
        this.optic = optic;
        this.func = optic.eval(FunctionType.Instance.INSTANCE);
        this.unwrappedFunction = input -> FunctionType.unbox(this.func.apply(FunctionType.create(input)));
    }

    @Override
    public String toString(int level) {
        return "Optic[" + this.optic + "]";
    }

    @Override
    public Function<DynamicOps<?>, Function<Function<A, B>, Function<S, T>>> eval() {
        return ops -> this.unwrappedFunction;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProfunctorTransformer that = (ProfunctorTransformer)o;
        return Objects.equals(this.optic, that.optic);
    }

    public int hashCode() {
        return Objects.hash(this.optic);
    }
}
