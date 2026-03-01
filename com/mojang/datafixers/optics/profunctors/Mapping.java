package com.mojang.datafixers.optics.profunctors;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.Functor;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.TraversalP;

public interface Mapping<P extends K2, Mu extends Mu>
extends TraversalP<P, Mu> {
    public static <P extends K2, Proof extends Mu> Mapping<P, Proof> unbox(App<Proof, P> proofBox) {
        return (Mapping)proofBox;
    }

    public <A, B, F extends K1> App2<P, App<F, A>, App<F, B>> mapping(Functor<F, ?> var1, App2<P, A, B> var2);

    public static interface Mu
    extends TraversalP.Mu {
    }
}
