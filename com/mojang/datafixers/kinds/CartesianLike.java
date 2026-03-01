package com.mojang.datafixers.kinds;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.Functor;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.Traversable;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

public interface CartesianLike<T extends K1, C, Mu extends Mu>
extends Functor<T, Mu>,
Traversable<T, Mu> {
    public static <F extends K1, C, Mu extends Mu> CartesianLike<F, C, Mu> unbox(App<Mu, F> proofBox) {
        return (CartesianLike)proofBox;
    }

    public <A> App<Pair.Mu<C>, A> to(App<T, A> var1);

    public <A> App<T, A> from(App<Pair.Mu<C>, A> var1);

    @Override
    default public <F extends K1, A, B> App<F, App<T, B>> traverse(Applicative<F, ?> applicative, Function<A, App<F, B>> function, App<T, A> input) {
        return applicative.map(this::from, new Pair.Instance<C>().traverse(applicative, function, this.to(input)));
    }

    public static interface Mu
    extends Functor.Mu,
    Traversable.Mu {
    }
}
