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

interface ReForgetP<R, A, B>
extends App2<Mu<R>, A, B> {
    public static <R, A, B> ReForgetP<R, A, B> unbox(App2<Mu<R>, A, B> box) {
        return (ReForgetP)box;
    }

    public B run(A var1, R var2);

    public static final class Instance<R>
    implements AffineP<com.mojang.datafixers.optics.ReForgetP$Mu<R>, Mu<R>>,
    App<Mu<R>, com.mojang.datafixers.optics.ReForgetP$Mu<R>> {
        @Override
        public <A, B, C, D> FunctionType<App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, A, B>, App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, C, D>> dimap(Function<C, A> g, Function<B, D> h) {
            return input -> Optics.reForgetP("dimap", (c, r) -> {
                Object a = g.apply(c);
                Object b = ReForgetP.unbox(input).run(a, r);
                Object d = h.apply(b);
                return d;
            });
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, Either<A, C>, Either<B, C>> left(App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, A, B> input) {
            return Optics.reForgetP("left", (e, r) -> e.mapLeft(a -> ReForgetP.unbox(input).run(a, r)));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, Either<C, A>, Either<C, B>> right(App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, A, B> input) {
            return Optics.reForgetP("right", (e, r) -> e.mapRight(a -> ReForgetP.unbox(input).run(a, r)));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, Pair<A, C>, Pair<B, C>> first(App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, A, B> input) {
            return Optics.reForgetP("first", (p, r) -> Pair.of(ReForgetP.unbox(input).run(p.getFirst(), r), p.getSecond()));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, Pair<C, A>, Pair<C, B>> second(App2<com.mojang.datafixers.optics.ReForgetP$Mu<R>, A, B> input) {
            return Optics.reForgetP("second", (p, r) -> Pair.of(p.getFirst(), ReForgetP.unbox(input).run(p.getSecond(), r)));
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
