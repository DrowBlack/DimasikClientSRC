package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.Cocartesian;
import com.mojang.datafixers.util.Either;
import java.util.function.Function;

interface ReForgetE<R, A, B>
extends App2<Mu<R>, A, B> {
    public static <R, A, B> ReForgetE<R, A, B> unbox(App2<Mu<R>, A, B> box) {
        return (ReForgetE)box;
    }

    public B run(Either<A, R> var1);

    public static final class Instance<R>
    implements Cocartesian<com.mojang.datafixers.optics.ReForgetE$Mu<R>, Mu<R>>,
    App<Mu<R>, com.mojang.datafixers.optics.ReForgetE$Mu<R>> {
        @Override
        public <A, B, C, D> FunctionType<App2<com.mojang.datafixers.optics.ReForgetE$Mu<R>, A, B>, App2<com.mojang.datafixers.optics.ReForgetE$Mu<R>, C, D>> dimap(Function<C, A> g, Function<B, D> h) {
            return input -> Optics.reForgetE("dimap", e -> {
                Either either = e.mapLeft(g);
                Object b = ReForgetE.unbox(input).run(either);
                Object d = h.apply(b);
                return d;
            });
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForgetE$Mu<R>, Either<A, C>, Either<B, C>> left(App2<com.mojang.datafixers.optics.ReForgetE$Mu<R>, A, B> input) {
            ReForgetE reForgetE = ReForgetE.unbox(input);
            return Optics.reForgetE("left", e -> e.map(e2 -> e2.map(a -> Either.left(reForgetE.run(Either.left(a))), Either::right), r -> Either.left(reForgetE.run(Either.right(r)))));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForgetE$Mu<R>, Either<C, A>, Either<C, B>> right(App2<com.mojang.datafixers.optics.ReForgetE$Mu<R>, A, B> input) {
            ReForgetE reForgetE = ReForgetE.unbox(input);
            return Optics.reForgetE("right", e -> e.map(e2 -> e2.map(Either::left, a -> Either.right(reForgetE.run(Either.left(a)))), r -> Either.right(reForgetE.run(Either.right(r)))));
        }

        static final class Mu<R>
        implements Cocartesian.Mu {
            Mu() {
            }
        }
    }

    public static final class Mu<R>
    implements K2 {
    }
}
