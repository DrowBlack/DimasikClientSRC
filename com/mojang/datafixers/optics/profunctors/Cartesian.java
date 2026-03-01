package com.mojang.datafixers.optics.profunctors;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.CartesianLike;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.FunctorProfunctor;
import com.mojang.datafixers.optics.profunctors.Profunctor;
import com.mojang.datafixers.util.Pair;

public interface Cartesian<P extends K2, Mu extends Mu>
extends Profunctor<P, Mu> {
    public static <P extends K2, Proof extends Mu> Cartesian<P, Proof> unbox(App<Proof, P> proofBox) {
        return (Cartesian)proofBox;
    }

    public <A, B, C> App2<P, Pair<A, C>, Pair<B, C>> first(App2<P, A, B> var1);

    default public <A, B, C> App2<P, Pair<C, A>, Pair<C, B>> second(App2<P, A, B> input) {
        return this.dimap(this.first(input), Pair::swap, Pair::swap);
    }

    default public FunctorProfunctor<CartesianLike.Mu, P, FunctorProfunctor.Mu<CartesianLike.Mu>> toFP2() {
        return new FunctorProfunctor<CartesianLike.Mu, P, FunctorProfunctor.Mu<CartesianLike.Mu>>(){

            @Override
            public <A, B, F extends K1> App2<P, App<F, A>, App<F, B>> distribute(App<? extends CartesianLike.Mu, F> proof, App2<P, A, B> input) {
                return this.cap(CartesianLike.unbox(proof), input);
            }

            private <A, B, F extends K1, C> App2<P, App<F, A>, App<F, B>> cap(CartesianLike<F, C, ?> cLike, App2<P, A, B> input) {
                return Cartesian.this.dimap(Cartesian.this.first(input), p -> Pair.unbox(cLike.to(p)), cLike::from);
            }
        };
    }

    public static interface Mu
    extends Profunctor.Mu {
        public static final TypeToken<Mu> TYPE_TOKEN = new TypeToken<Mu>(){};
    }
}
