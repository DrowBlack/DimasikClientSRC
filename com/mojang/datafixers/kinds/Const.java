package com.mojang.datafixers.kinds;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.Monoid;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Const<C, T>
implements App<Mu<C>, T> {
    private final C value;

    public static <C, T> C unbox(App<Mu<C>, T> box) {
        return ((Const)box).value;
    }

    public static <C, T> Const<C, T> create(C value) {
        return new Const<C, T>(value);
    }

    Const(C value) {
        this.value = value;
    }

    public static final class Instance<C>
    implements Applicative<com.mojang.datafixers.kinds.Const$Mu<C>, Mu<C>> {
        private final Monoid<C> monoid;

        public Instance(Monoid<C> monoid) {
            this.monoid = monoid;
        }

        @Override
        public <T, R> App<com.mojang.datafixers.kinds.Const$Mu<C>, R> map(Function<? super T, ? extends R> func, App<com.mojang.datafixers.kinds.Const$Mu<C>, T> ts) {
            return Const.create(Const.unbox(ts));
        }

        @Override
        public <A> App<com.mojang.datafixers.kinds.Const$Mu<C>, A> point(A a) {
            return Const.create(this.monoid.point());
        }

        @Override
        public <A, R> Function<App<com.mojang.datafixers.kinds.Const$Mu<C>, A>, App<com.mojang.datafixers.kinds.Const$Mu<C>, R>> lift1(App<com.mojang.datafixers.kinds.Const$Mu<C>, Function<A, R>> function) {
            return a -> Const.create(this.monoid.add(Const.unbox(function), Const.unbox(a)));
        }

        @Override
        public <A, B, R> BiFunction<App<com.mojang.datafixers.kinds.Const$Mu<C>, A>, App<com.mojang.datafixers.kinds.Const$Mu<C>, B>, App<com.mojang.datafixers.kinds.Const$Mu<C>, R>> lift2(App<com.mojang.datafixers.kinds.Const$Mu<C>, BiFunction<A, B, R>> function) {
            return (a, b) -> Const.create(this.monoid.add(Const.unbox(function), this.monoid.add(Const.unbox(a), Const.unbox(b))));
        }

        public static final class Mu<C>
        implements Applicative.Mu {
        }
    }

    public static final class Mu<C>
    implements K1 {
    }
}
