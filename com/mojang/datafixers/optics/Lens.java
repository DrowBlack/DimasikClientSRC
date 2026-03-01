package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.Cartesian;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

public interface Lens<S, T, A, B>
extends App2<Mu<A, B>, S, T>,
Optic<Cartesian.Mu, S, T, A, B> {
    public static <S, T, A, B> Lens<S, T, A, B> unbox(App2<Mu<A, B>, S, T> box) {
        return (Lens)box;
    }

    public static <S, T, A, B> Lens<S, T, A, B> unbox2(App2<Mu2<S, T>, B, A> box) {
        return ((Box)box).lens;
    }

    public static <S, T, A, B> App2<Mu2<S, T>, B, A> box(Lens<S, T, A, B> lens) {
        return new Box<S, T, A, B>(lens);
    }

    public A view(S var1);

    public T update(B var1, S var2);

    @Override
    default public <P extends K2> FunctionType<App2<P, A, B>, App2<P, S, T>> eval(App<? extends Cartesian.Mu, P> proofBox) {
        Cartesian proof = Cartesian.unbox(proofBox);
        return a -> proof.dimap(proof.first(a), s -> Pair.of(this.view(s), s), pair -> this.update(pair.getFirst(), pair.getSecond()));
    }

    public static final class Instance<A2, B2>
    implements Cartesian<Mu<A2, B2>, Cartesian.Mu> {
        @Override
        public <A, B, C, D> FunctionType<App2<Mu<A2, B2>, A, B>, App2<Mu<A2, B2>, C, D>> dimap(Function<C, A> g, Function<B, D> h) {
            return l -> Optics.lens(c -> Lens.unbox(l).view(g.apply(c)), (b2, c) -> h.apply(Lens.unbox(l).update(b2, g.apply(c))));
        }

        @Override
        public <A, B, C> App2<Mu<A2, B2>, Pair<A, C>, Pair<B, C>> first(App2<Mu<A2, B2>, A, B> input) {
            return Optics.lens(pair -> Lens.unbox(input).view(pair.getFirst()), (b2, pair) -> Pair.of(Lens.unbox(input).update(b2, pair.getFirst()), pair.getSecond()));
        }

        @Override
        public <A, B, C> App2<Mu<A2, B2>, Pair<C, A>, Pair<C, B>> second(App2<Mu<A2, B2>, A, B> input) {
            return Optics.lens(pair -> Lens.unbox(input).view(pair.getSecond()), (b2, pair) -> Pair.of(pair.getFirst(), Lens.unbox(input).update(b2, pair.getSecond())));
        }
    }

    public static final class Box<S, T, A, B>
    implements App2<Mu2<S, T>, B, A> {
        private final Lens<S, T, A, B> lens;

        public Box(Lens<S, T, A, B> lens) {
            this.lens = lens;
        }
    }

    public static final class Mu2<S, T>
    implements K2 {
    }

    public static final class Mu<A, B>
    implements K2 {
    }
}
