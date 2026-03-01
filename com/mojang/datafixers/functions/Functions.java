package com.mojang.datafixers.functions;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.functions.Apply;
import com.mojang.datafixers.functions.Bang;
import com.mojang.datafixers.functions.Comp;
import com.mojang.datafixers.functions.Fold;
import com.mojang.datafixers.functions.FunctionWrapper;
import com.mojang.datafixers.functions.Id;
import com.mojang.datafixers.functions.In;
import com.mojang.datafixers.functions.Out;
import com.mojang.datafixers.functions.PointFree;
import com.mojang.datafixers.functions.ProfunctorTransformer;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.Algebra;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.function.Function;

public abstract class Functions {
    private static final Id<?> ID = new Id();

    public static <A, B, C> PointFree<Function<A, C>> comp(Type<B> middleType, PointFree<Function<B, C>> f1, PointFree<Function<A, B>> f2) {
        if (Objects.equals(f1, Functions.id())) {
            return f2;
        }
        if (Objects.equals(f2, Functions.id())) {
            return f1;
        }
        return new Comp(middleType, f1, f2);
    }

    public static <A, B> PointFree<Function<A, B>> fun(String name, Function<DynamicOps<?>, Function<A, B>> fun) {
        return new FunctionWrapper<A, B>(name, fun);
    }

    public static <A, B> PointFree<B> app(PointFree<Function<A, B>> fun, PointFree<A> arg, Type<A> argType) {
        return new Apply<A, B>(fun, arg, argType);
    }

    public static <S, T, A, B> PointFree<Function<Function<A, B>, Function<S, T>>> profunctorTransformer(Optic<? super FunctionType.Instance.Mu, S, T, A, B> lens) {
        return new ProfunctorTransformer<S, T, A, B>(lens);
    }

    public static <A> Bang<A> bang() {
        return new Bang();
    }

    public static <A> PointFree<Function<A, A>> in(RecursivePoint.RecursivePointType<A> type) {
        return new In<A>(type);
    }

    public static <A> PointFree<Function<A, A>> out(RecursivePoint.RecursivePointType<A> type) {
        return new Out<A>(type);
    }

    public static <A, B> PointFree<Function<A, B>> fold(RecursivePoint.RecursivePointType<A> aType, RewriteResult<?, B> function, Algebra algebra, int index) {
        return new Fold<A, B>(aType, function, algebra, index);
    }

    public static <A> PointFree<Function<A, A>> id() {
        return ID;
    }
}
