package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.Cocartesian;
import com.mojang.datafixers.optics.profunctors.ReCartesian;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

interface ReForget<R, A, B>
extends App2<Mu<R>, A, B> {
    public static <R, A, B> ReForget<R, A, B> unbox(App2<Mu<R>, A, B> box) {
        return (ReForget)box;
    }

    public B run(R var1);

    public static final class Instance<R>
    implements ReCartesian<com.mojang.datafixers.optics.ReForget$Mu<R>, Mu<R>>,
    Cocartesian<com.mojang.datafixers.optics.ReForget$Mu<R>, Mu<R>>,
    App<Mu<R>, com.mojang.datafixers.optics.ReForget$Mu<R>> {
        @Override
        public <A, B, C, D> FunctionType<App2<com.mojang.datafixers.optics.ReForget$Mu<R>, A, B>, App2<com.mojang.datafixers.optics.ReForget$Mu<R>, C, D>> dimap(Function<C, A> g, Function<B, D> h) {
            return input -> Optics.reForget(r -> h.apply(ReForget.unbox(input).run(r)));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForget$Mu<R>, A, B> unfirst(App2<com.mojang.datafixers.optics.ReForget$Mu<R>, Pair<A, C>, Pair<B, C>> input) {
            return Optics.reForget(r -> ((Pair)ReForget.unbox(input).run(r)).getFirst());
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForget$Mu<R>, A, B> unsecond(App2<com.mojang.datafixers.optics.ReForget$Mu<R>, Pair<C, A>, Pair<C, B>> input) {
            return Optics.reForget(r -> ((Pair)ReForget.unbox(input).run(r)).getSecond());
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForget$Mu<R>, Either<A, C>, Either<B, C>> left(App2<com.mojang.datafixers.optics.ReForget$Mu<R>, A, B> input) {
            return Optics.reForget(r -> Either.left(ReForget.unbox(input).run(r)));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForget$Mu<R>, Either<C, A>, Either<C, B>> right(App2<com.mojang.datafixers.optics.ReForget$Mu<R>, A, B> input) {
            return Optics.reForget(r -> Either.right(ReForget.unbox(input).run(r)));
        }

        static final class Mu<R>
        implements ReCartesian.Mu,
        Cocartesian.Mu {
            Mu() {
            }
        }
    }

    public static final class Mu<R>
    implements K2 {
    }
}
