package com.mojang.datafixers.functions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFree;
import com.mojang.datafixers.types.families.Algebra;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

final class Fold<A, B>
extends PointFree<Function<A, B>> {
    private static final Map<Pair<RecursiveTypeFamily, Algebra>, IntFunction<RewriteResult<?, ?>>> HMAP_CACHE = Maps.newConcurrentMap();
    private static final Map<Pair<IntFunction<RewriteResult<?, ?>>, Integer>, RewriteResult<?, ?>> HMAP_APPLY_CACHE = Maps.newConcurrentMap();
    protected final RecursivePoint.RecursivePointType<A> aType;
    protected final RewriteResult<?, B> function;
    protected final Algebra algebra;
    protected final int index;

    public Fold(RecursivePoint.RecursivePointType<A> aType, RewriteResult<?, B> function, Algebra algebra, int index) {
        this.aType = aType;
        this.function = function;
        this.algebra = algebra;
        this.index = index;
    }

    private <FB> PointFree<Function<A, B>> cap(RewriteResult<?, B> op, RewriteResult<?, FB> resResult) {
        return Functions.comp(resResult.view().newType(), op.view().function(), resResult.view().function());
    }

    @Override
    public Function<DynamicOps<?>, Function<A, B>> eval() {
        return ops -> a -> {
            RecursiveTypeFamily family = this.aType.family();
            IntFunction hmapped = HMAP_CACHE.computeIfAbsent(Pair.of(family, this.algebra), key -> ((RecursiveTypeFamily)key.getFirst()).template().hmap((TypeFamily)key.getFirst(), ((RecursiveTypeFamily)key.getFirst()).fold((Algebra)key.getSecond())));
            RewriteResult result = HMAP_APPLY_CACHE.computeIfAbsent(Pair.of(hmapped, this.index), key -> (RewriteResult)((IntFunction)key.getFirst()).apply((Integer)key.getSecond()));
            PointFree<Function<A, B>> eval = this.cap(this.function, result);
            return eval.evalCached().apply((DynamicOps<?>)ops).apply(a);
        };
    }

    @Override
    public String toString(int level) {
        return "fold(" + this.aType + ", " + this.index + ", \n" + Fold.indent(level + 1) + this.algebra.toString(level + 1) + "\n" + Fold.indent(level) + ")";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Fold fold = (Fold)o;
        return Objects.equals(this.aType, fold.aType) && Objects.equals(this.algebra, fold.algebra);
    }

    public int hashCode() {
        return Objects.hash(this.aType, this.algebra);
    }
}
