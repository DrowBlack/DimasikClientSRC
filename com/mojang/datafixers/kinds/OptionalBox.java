package com.mojang.datafixers.kinds;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.Traversable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class OptionalBox<T>
implements App<Mu, T> {
    private final Optional<T> value;

    public static <T> Optional<T> unbox(App<Mu, T> box) {
        return ((OptionalBox)box).value;
    }

    public static <T> OptionalBox<T> create(Optional<T> value) {
        return new OptionalBox<T>(value);
    }

    private OptionalBox(Optional<T> value) {
        this.value = value;
    }

    public static enum Instance implements Applicative<com.mojang.datafixers.kinds.OptionalBox$Mu, Mu>,
    Traversable<com.mojang.datafixers.kinds.OptionalBox$Mu, Mu>
    {
        INSTANCE;


        @Override
        public <T, R> App<com.mojang.datafixers.kinds.OptionalBox$Mu, R> map(Function<? super T, ? extends R> func, App<com.mojang.datafixers.kinds.OptionalBox$Mu, T> ts) {
            return OptionalBox.create(OptionalBox.unbox(ts).map(func));
        }

        @Override
        public <A> App<com.mojang.datafixers.kinds.OptionalBox$Mu, A> point(A a) {
            return OptionalBox.create(Optional.of(a));
        }

        @Override
        public <A, R> Function<App<com.mojang.datafixers.kinds.OptionalBox$Mu, A>, App<com.mojang.datafixers.kinds.OptionalBox$Mu, R>> lift1(App<com.mojang.datafixers.kinds.OptionalBox$Mu, Function<A, R>> function) {
            return a -> OptionalBox.create(OptionalBox.unbox(function).flatMap(f -> OptionalBox.unbox(a).map(f)));
        }

        @Override
        public <A, B, R> BiFunction<App<com.mojang.datafixers.kinds.OptionalBox$Mu, A>, App<com.mojang.datafixers.kinds.OptionalBox$Mu, B>, App<com.mojang.datafixers.kinds.OptionalBox$Mu, R>> lift2(App<com.mojang.datafixers.kinds.OptionalBox$Mu, BiFunction<A, B, R>> function) {
            return (a, b) -> OptionalBox.create(OptionalBox.unbox(function).flatMap(f -> OptionalBox.unbox(a).flatMap(av -> OptionalBox.unbox(b).map(bv -> f.apply(av, bv)))));
        }

        @Override
        public <F extends K1, A, B> App<F, App<com.mojang.datafixers.kinds.OptionalBox$Mu, B>> traverse(Applicative<F, ?> applicative, Function<A, App<F, B>> function, App<com.mojang.datafixers.kinds.OptionalBox$Mu, A> input) {
            Optional<App<F, B>> traversed = OptionalBox.unbox(input).map(function);
            if (traversed.isPresent()) {
                return applicative.map((? super T b) -> OptionalBox.create(Optional.of(b)), traversed.get());
            }
            return applicative.point(OptionalBox.create(Optional.empty()));
        }

        public static final class Mu
        implements Applicative.Mu,
        Traversable.Mu {
        }
    }

    public static final class Mu
    implements K1 {
    }
}
