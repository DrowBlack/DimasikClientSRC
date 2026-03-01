package com.mojang.datafixers.optics.profunctors;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.Bicontravariant;
import com.mojang.datafixers.optics.profunctors.Profunctor;
import java.util.function.Function;

public interface GetterP<P extends K2, Mu extends Mu>
extends Profunctor<P, Mu>,
Bicontravariant<P, Mu> {
    public static <P extends K2, Proof extends Mu> GetterP<P, Proof> unbox(App<Proof, P> proofBox) {
        return (GetterP)proofBox;
    }

    default public <A, B, C> App2<P, C, A> secondPhantom(App2<P, C, B> input) {
        return this.cimap(() -> this.rmap(input, b -> null), Function.identity(), a -> null);
    }

    public static interface Mu
    extends Profunctor.Mu,
    Bicontravariant.Mu {
    }
}
