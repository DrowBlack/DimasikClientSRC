package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

interface ForgetE<R, A, B>
extends App2<Mu<R>, A, B> {
    public static <R, A, B> ForgetE<R, A, B> unbox(App2<Mu<R>, A, B> box) {
        return (ForgetE)box;
    }

    public Either<B, R> run(A var1);

    public static final class Instance<R>
    implements AffineP<com.mojang.datafixers.optics.ForgetE$Mu<R>, Mu<R>>,
    App<Mu<R>, com.mojang.datafixers.optics.ForgetE$Mu<R>> {
        @Override
        public <A, B, C, D> FunctionType<App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, A, B>, App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, C, D>> dimap(Function<C, A> g, Function<B, D> h) {
            return input -> Optics.forgetE(c -> ForgetE.unbox(input).run(g.apply(c)).mapLeft(h));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, Pair<A, C>, Pair<B, C>> first(App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, A, B> input) {
            return Optics.forgetE(p -> ForgetE.unbox(input).run(p.getFirst()).mapLeft(b -> Pair.of(b, p.getSecond())));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, Pair<C, A>, Pair<C, B>> second(App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, A, B> input) {
            return Optics.forgetE(p -> ForgetE.unbox(input).run(p.getSecond()).mapLeft(b -> Pair.of(p.getFirst(), b)));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, Either<A, C>, Either<B, C>> left(App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, A, B> input) {
            return Optics.forgetE(e -> e.map(l -> ForgetE.unbox(input).run(l).mapLeft(Either::left), r -> Either.left(Either.right(r))));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, Either<C, A>, Either<C, B>> right(App2<com.mojang.datafixers.optics.ForgetE$Mu<R>, A, B> input) {
            return Optics.forgetE(e -> e.map(l -> Either.left(Either.left(l)), r -> ForgetE.unbox(input).run(r).mapLeft(Either::right)));
        }

        static final class Mu<R>
        implements AffineP.Mu {
            Mu() {
            }
        }
    }

    public static final class Mu<R>
    implements K2 {
    }
}
