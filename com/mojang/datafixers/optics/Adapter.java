package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.Profunctor;
import java.util.function.Function;

public interface Adapter<S, T, A, B>
extends App2<Mu<A, B>, S, T>,
Optic<Profunctor.Mu, S, T, A, B> {
    public static <S, T, A, B> Adapter<S, T, A, B> unbox(App2<Mu<A, B>, S, T> box) {
        return (Adapter)box;
    }

    public A from(S var1);

    public T to(B var1);

    @Override
    default public <P extends K2> FunctionType<App2<P, A, B>, App2<P, S, T>> eval(App<? extends Profunctor.Mu, P> proofBox) {
        Profunctor proof = Profunctor.unbox(proofBox);
        return a -> proof.dimap(a, this::from, this::to);
    }

    public static final class Instance<A2, B2>
    implements Profunctor<Mu<A2, B2>, Profunctor.Mu> {
        @Override
        public <A, B, C, D> FunctionType<App2<Mu<A2, B2>, A, B>, App2<Mu<A2, B2>, C, D>> dimap(Function<C, A> g, Function<B, D> h) {
            return a -> Optics.adapter(c -> Adapter.unbox(a).from(g.apply(c)), b2 -> h.apply(Adapter.unbox(a).to(b2)));
        }
    }

    public static final class Mu<A, B>
    implements K2 {
    }
}
