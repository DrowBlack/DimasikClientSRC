package com.mojang.datafixers;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.Functor;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.kinds.Representable;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.Procompose;
import com.mojang.datafixers.optics.Wander;
import com.mojang.datafixers.optics.profunctors.Mapping;
import com.mojang.datafixers.optics.profunctors.MonoidProfunctor;
import com.mojang.datafixers.optics.profunctors.Monoidal;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public interface FunctionType<A, B>
extends Function<A, B>,
App2<Mu, A, B>,
App<ReaderMu<A>, B> {
    public static <A, B> FunctionType<A, B> create(Function<? super A, ? extends B> function) {
        return function::apply;
    }

    public static <A, B> Function<A, B> unbox(App2<Mu, A, B> box) {
        return (FunctionType)box;
    }

    public static <A, B> Function<A, B> unbox(App<ReaderMu<A>, B> box) {
        return (FunctionType)box;
    }

    @Override
    @Nonnull
    public B apply(@Nonnull A var1);

    public static enum Instance implements TraversalP<com.mojang.datafixers.FunctionType$Mu, Mu>,
    MonoidProfunctor<com.mojang.datafixers.FunctionType$Mu, Mu>,
    Mapping<com.mojang.datafixers.FunctionType$Mu, Mu>,
    Monoidal<com.mojang.datafixers.FunctionType$Mu, Mu>,
    App<Mu, com.mojang.datafixers.FunctionType$Mu>
    {
        INSTANCE;


        @Override
        public <A, B, C, D> FunctionType<App2<com.mojang.datafixers.FunctionType$Mu, A, B>, App2<com.mojang.datafixers.FunctionType$Mu, C, D>> dimap(Function<C, A> g, Function<B, D> h) {
            return f -> FunctionType.create(h.compose(Optics.getFunc(f)).compose(g));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.FunctionType$Mu, Pair<A, C>, Pair<B, C>> first(App2<com.mojang.datafixers.FunctionType$Mu, A, B> input) {
            return FunctionType.create(p -> Pair.of(Optics.getFunc(input).apply(p.getFirst()), p.getSecond()));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.FunctionType$Mu, Pair<C, A>, Pair<C, B>> second(App2<com.mojang.datafixers.FunctionType$Mu, A, B> input) {
            return FunctionType.create(p -> Pair.of(p.getFirst(), Optics.getFunc(input).apply(p.getSecond())));
        }

        @Override
        public <S, T, A, B> App2<com.mojang.datafixers.FunctionType$Mu, S, T> wander(Wander<S, T, A, B> wander, App2<com.mojang.datafixers.FunctionType$Mu, A, B> input) {
            return FunctionType.create(s -> IdF.get(wander.wander(IdF.Instance.INSTANCE, (A a) -> IdF.create(Optics.getFunc(input).apply(a))).apply(s)));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.FunctionType$Mu, Either<A, C>, Either<B, C>> left(App2<com.mojang.datafixers.FunctionType$Mu, A, B> input) {
            return FunctionType.create(either -> either.mapLeft(Optics.getFunc(input)));
        }

        @Override
        public <A, B, C> App2<com.mojang.datafixers.FunctionType$Mu, Either<C, A>, Either<C, B>> right(App2<com.mojang.datafixers.FunctionType$Mu, A, B> input) {
            return FunctionType.create(either -> either.mapRight(Optics.getFunc(input)));
        }

        @Override
        public <A, B, C, D> App2<com.mojang.datafixers.FunctionType$Mu, Pair<A, C>, Pair<B, D>> par(App2<com.mojang.datafixers.FunctionType$Mu, A, B> first, Supplier<App2<com.mojang.datafixers.FunctionType$Mu, C, D>> second) {
            return FunctionType.create(pair -> Pair.of(Optics.getFunc(first).apply(pair.getFirst()), Optics.getFunc((App2)second.get()).apply(pair.getSecond())));
        }

        @Override
        public App2<com.mojang.datafixers.FunctionType$Mu, Void, Void> empty() {
            return FunctionType.create(Function.identity());
        }

        @Override
        public <A, B> App2<com.mojang.datafixers.FunctionType$Mu, A, B> zero(App2<com.mojang.datafixers.FunctionType$Mu, A, B> func) {
            return func;
        }

        @Override
        public <A, B> App2<com.mojang.datafixers.FunctionType$Mu, A, B> plus(App2<Procompose.Mu<com.mojang.datafixers.FunctionType$Mu, com.mojang.datafixers.FunctionType$Mu>, A, B> input) {
            Procompose<com.mojang.datafixers.FunctionType$Mu, com.mojang.datafixers.FunctionType$Mu, A, B, ?> cmp = Procompose.unbox(input);
            return this.cap(cmp);
        }

        private <A, B, C> App2<com.mojang.datafixers.FunctionType$Mu, A, B> cap(Procompose<com.mojang.datafixers.FunctionType$Mu, com.mojang.datafixers.FunctionType$Mu, A, B, C> cmp) {
            return FunctionType.create(Optics.getFunc(cmp.second()).compose(Optics.getFunc(cmp.first().get())));
        }

        @Override
        public <A, B, F extends K1> App2<com.mojang.datafixers.FunctionType$Mu, App<F, A>, App<F, B>> mapping(Functor<F, ?> functor, App2<com.mojang.datafixers.FunctionType$Mu, A, B> input) {
            return FunctionType.create(fa -> functor.map(Optics.getFunc(input), fa));
        }

        public static final class Mu
        implements TraversalP.Mu,
        MonoidProfunctor.Mu,
        Mapping.Mu,
        Monoidal.Mu {
            public static final TypeToken<Mu> TYPE_TOKEN = new TypeToken<Mu>(){};
        }
    }

    public static final class ReaderInstance<R>
    implements Representable<ReaderMu<R>, R, Mu<R>> {
        @Override
        public <T, R2> App<ReaderMu<R>, R2> map(Function<? super T, ? extends R2> func, App<ReaderMu<R>, T> ts) {
            return FunctionType.create(func.compose(FunctionType.unbox(ts)));
        }

        @Override
        public <B> App<ReaderMu<R>, B> to(App<ReaderMu<R>, B> input) {
            return input;
        }

        @Override
        public <B> App<ReaderMu<R>, B> from(App<ReaderMu<R>, B> input) {
            return input;
        }

        public static final class Mu<A>
        implements Representable.Mu {
        }
    }

    public static final class ReaderMu<A>
    implements K1 {
    }

    public static final class Mu
    implements K2 {
    }
}
