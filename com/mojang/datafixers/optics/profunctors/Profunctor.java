package com.mojang.datafixers.optics.profunctors;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.kinds.Kind2;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Profunctor<P extends K2, Mu extends Mu>
extends Kind2<P, Mu> {
    public static <P extends K2, Proof extends Mu> Profunctor<P, Proof> unbox(App<Proof, P> proofBox) {
        return (Profunctor)proofBox;
    }

    public <A, B, C, D> FunctionType<App2<P, A, B>, App2<P, C, D>> dimap(Function<C, A> var1, Function<B, D> var2);

    default public <A, B, C, D> App2<P, C, D> dimap(App2<P, A, B> arg, Function<C, A> g, Function<B, D> h) {
        return this.dimap(g, h).apply(arg);
    }

    default public <A, B, C, D> App2<P, C, D> dimap(Supplier<App2<P, A, B>> arg, Function<C, A> g, Function<B, D> h) {
        return this.dimap(g, h).apply(arg.get());
    }

    default public <A, B, C> App2<P, C, B> lmap(App2<P, A, B> input, Function<C, A> g) {
        return this.dimap(input, g, Function.identity());
    }

    default public <A, B, D> App2<P, A, D> rmap(App2<P, A, B> input, Function<B, D> h) {
        return this.dimap(input, Function.identity(), h);
    }

    public static interface Mu
    extends Kind2.Mu {
        public static final TypeToken<Mu> TYPE_TOKEN = new TypeToken<Mu>(){};
    }
}
