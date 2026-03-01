package com.mojang.datafixers.util;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.CartesianLike;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.Traversable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Pair<F, S>
implements App<Mu<S>, F> {
    private final F first;
    private final S second;

    public static <F, S> Pair<F, S> unbox(App<Mu<S>, F> box) {
        return (Pair)box;
    }

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return this.first;
    }

    public S getSecond() {
        return this.second;
    }

    public Pair<S, F> swap() {
        return Pair.of(this.second, this.first);
    }

    public String toString() {
        return "(" + this.first + ", " + this.second + ")";
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair other = (Pair)obj;
        return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second);
    }

    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.first, this.second);
    }

    public <F2> Pair<F2, S> mapFirst(Function<? super F, ? extends F2> function) {
        return Pair.of(function.apply(this.first), this.second);
    }

    public <S2> Pair<F, S2> mapSecond(Function<? super S, ? extends S2> function) {
        return Pair.of(this.first, function.apply(this.second));
    }

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<F, S>(first, second);
    }

    public static <F, S> Collector<Pair<F, S>, ?, Map<F, S>> toMap() {
        return Collectors.toMap(Pair::getFirst, Pair::getSecond);
    }

    public static final class Instance<S2>
    implements Traversable<com.mojang.datafixers.util.Pair$Mu<S2>, Mu<S2>>,
    CartesianLike<com.mojang.datafixers.util.Pair$Mu<S2>, S2, Mu<S2>> {
        @Override
        public <T, R> App<com.mojang.datafixers.util.Pair$Mu<S2>, R> map(Function<? super T, ? extends R> func, App<com.mojang.datafixers.util.Pair$Mu<S2>, T> ts) {
            return Pair.unbox(ts).mapFirst(func);
        }

        @Override
        public <F extends K1, A, B> App<F, App<com.mojang.datafixers.util.Pair$Mu<S2>, B>> traverse(Applicative<F, ?> applicative, Function<A, App<F, B>> function, App<com.mojang.datafixers.util.Pair$Mu<S2>, A> input) {
            Pair pair = Pair.unbox(input);
            return applicative.ap(b -> Pair.of(b, pair.second), function.apply(pair.first));
        }

        @Override
        public <A> App<com.mojang.datafixers.util.Pair$Mu<S2>, A> to(App<com.mojang.datafixers.util.Pair$Mu<S2>, A> input) {
            return input;
        }

        @Override
        public <A> App<com.mojang.datafixers.util.Pair$Mu<S2>, A> from(App<com.mojang.datafixers.util.Pair$Mu<S2>, A> input) {
            return input;
        }

        public static final class Mu<S2>
        implements Traversable.Mu,
        CartesianLike.Mu {
        }
    }

    public static final class Mu<S>
    implements K1 {
    }
}
