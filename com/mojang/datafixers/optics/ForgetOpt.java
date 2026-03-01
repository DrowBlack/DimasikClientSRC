package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Function;

public interface ForgetOpt<R, A, B>
extends App2<Mu<R>, A, B> {
    public static <R, A, B> ForgetOpt<R, A, B> unbox(App2<Mu<R>, A, B> box) {
        return (ForgetOpt)box;
    }

    public Optional<R> run(A var1);

    public static final class Instance<R>
    implements AffineP<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, Mu<R>>,
    App<Mu<R>, com.mojang.datafixers.optics.ForgetOpt$Mu<R>> {
        @Override
        public <A, B, C, D> FunctionType<App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, A, B>, App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, C, D>> dimap(Function<C, A> g, Function<B, D> h) {
            return input -> Optics.forgetOpt(c -> ForgetOpt.unbox(input).run(g.apply(c)));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, Pair<A, C>, Pair<B, C>> first(App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, A, B> input) {
            return Optics.forgetOpt(p -> ForgetOpt.unbox(input).run(p.getFirst()));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, Pair<C, A>, Pair<C, B>> second(App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, A, B> input) {
            return Optics.forgetOpt(p -> ForgetOpt.unbox(input).run(p.getSecond()));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, Either<A, C>, Either<B, C>> left(App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, A, B> input) {
            return Optics.forgetOpt(e -> e.left().flatMap(ForgetOpt.unbox(input)::run));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, Either<C, A>, Either<C, B>> right(App2<com.mojang.datafixers.optics.ForgetOpt$Mu<R>, A, B> input) {
            return Optics.forgetOpt(e -> e.right().flatMap(ForgetOpt.unbox(input)::run));
        }

        public static final class Mu<R>
        implements AffineP.Mu {
        }
    }

    public static final class Mu<R>
    implements K2 {
    }
}
