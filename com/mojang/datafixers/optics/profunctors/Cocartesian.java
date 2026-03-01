package com.mojang.datafixers.optics.profunctors;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.CocartesianLike;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.FunctorProfunctor;
import com.mojang.datafixers.optics.profunctors.Profunctor;
import com.mojang.datafixers.util.Either;

public interface Cocartesian<P extends K2, Mu extends Mu>
extends Profunctor<P, Mu> {
    public static <P extends K2, Proof extends Mu> Cocartesian<P, Proof> unbox(App<Proof, P> proofBox) {
        return (Cocartesian)proofBox;
    }

    public <A, B, C> App2<P, Either<A, C>, Either<B, C>> left(App2<P, A, B> var1);

    default public <A, B, C> App2<P, Either<C, A>, Either<C, B>> right(App2<P, A, B> input) {
        return this.dimap(this.left(input), Either::swap, Either::swap);
    }

    default public FunctorProfunctor<CocartesianLike.Mu, P, FunctorProfunctor.Mu<CocartesianLike.Mu>> toFP() {
        return new FunctorProfunctor<CocartesianLike.Mu, P, FunctorProfunctor.Mu<CocartesianLike.Mu>>(){

            @Override
            public <A, B, F extends K1> App2<P, App<F, A>, App<F, B>> distribute(App<? extends CocartesianLike.Mu, F> proof, App2<P, A, B> input) {
                return this.cap(CocartesianLike.unbox(proof), input);
            }

            private <A, B, F extends K1, C> App2<P, App<F, A>, App<F, B>> cap(CocartesianLike<F, C, ?> cLike, App2<P, A, B> input) {
                return Cocartesian.this.dimap(Cocartesian.this.left(input), e -> Either.unbox(cLike.to(e)), cLike::from);
            }
        };
    }

    public static interface Mu
    extends Profunctor.Mu {
        public static final TypeToken<Mu> TYPE_TOKEN = new TypeToken<Mu>(){};
    }
}
