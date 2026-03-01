package com.mojang.datafixers.functions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFree;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

final class Comp<A, B, C>
extends PointFree<Function<A, C>> {
    protected final Type<B> middleType;
    protected final PointFree<Function<B, C>> first;
    protected final PointFree<Function<A, B>> second;

    public Comp(Type<B> middleType, PointFree<Function<B, C>> first, PointFree<Function<A, B>> second) {
        this.middleType = middleType;
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString(int level) {
        return "(\n" + Comp.indent(level + 1) + this.first.toString(level + 1) + "\n" + Comp.indent(level + 1) + "\u25e6\n" + Comp.indent(level + 1) + this.second.toString(level + 1) + "\n" + Comp.indent(level) + ")";
    }

    @Override
    public Optional<? extends PointFree<Function<A, C>>> all(PointFreeRule rule, Type<Function<A, C>> type) {
        Func funcType = (Func)type;
        return Optional.of(Functions.comp(this.middleType, rule.rewrite(DSL.func(this.middleType, funcType.second()), this.first).map(f -> f).orElse(this.first), rule.rewrite(DSL.func(funcType.first(), this.middleType), this.second).map(f1 -> f1).orElse(this.second)));
    }

    @Override
    public Optional<? extends PointFree<Function<A, C>>> one(PointFreeRule rule, Type<Function<A, C>> type) {
        Func funcType = (Func)type;
        return rule.rewrite(DSL.func(this.middleType, funcType.second()), this.first).map(f -> Optional.of(Functions.comp(this.middleType, f, this.second))).orElseGet(() -> rule.rewrite(DSL.func(funcType.first(), this.middleType), this.second).map(s -> Functions.comp(this.middleType, this.first, s)));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Comp comp = (Comp)o;
        return Objects.equals(this.first, comp.first) && Objects.equals(this.second, comp.second);
    }

    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    @Override
    public Function<DynamicOps<?>, Function<A, C>> eval() {
        return ops -> input -> {
            Function<A, B> s = this.second.evalCached().apply((DynamicOps<?>)ops);
            Function<B, C> f = this.first.evalCached().apply((DynamicOps<?>)ops);
            return f.apply(s.apply(input));
        };
    }
}
