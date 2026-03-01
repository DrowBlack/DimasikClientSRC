package com.mojang.datafixers.functions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFree;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

final class Apply<A, B>
extends PointFree<B> {
    protected final PointFree<Function<A, B>> func;
    protected final PointFree<A> arg;
    protected final Type<A> argType;

    public Apply(PointFree<Function<A, B>> func, PointFree<A> arg, Type<A> argType) {
        this.func = func;
        this.arg = arg;
        this.argType = argType;
    }

    @Override
    public Function<DynamicOps<?>, B> eval() {
        return ops -> this.func.evalCached().apply((DynamicOps<?>)ops).apply(this.arg.evalCached().apply((DynamicOps<?>)ops));
    }

    @Override
    public String toString(int level) {
        return "(ap " + this.func.toString(level + 1) + "\n" + Apply.indent(level + 1) + this.arg.toString(level + 1) + "\n" + Apply.indent(level) + ")";
    }

    @Override
    public Optional<? extends PointFree<B>> all(PointFreeRule rule, Type<B> type) {
        return Optional.of(Functions.app(rule.rewrite(DSL.func(this.argType, type), this.func).map(f1 -> f1).orElse(this.func), rule.rewrite(this.argType, this.arg).map(f -> f).orElse(this.arg), this.argType));
    }

    @Override
    public Optional<? extends PointFree<B>> one(PointFreeRule rule, Type<B> type) {
        return rule.rewrite(DSL.func(this.argType, type), this.func).map(f -> Optional.of(Functions.app(f, this.arg, this.argType))).orElseGet(() -> rule.rewrite(this.argType, this.arg).map(a -> Functions.app(this.func, a, this.argType)));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Apply)) {
            return false;
        }
        Apply apply = (Apply)o;
        return Objects.equals(this.func, apply.func) && Objects.equals(this.arg, apply.arg);
    }

    public int hashCode() {
        return Objects.hash(this.func, this.arg);
    }
}
