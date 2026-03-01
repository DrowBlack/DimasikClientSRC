package com.mojang.datafixers.kinds;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.Traversable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ListBox<T>
implements App<Mu, T> {
    private final List<T> value;

    public static <T> List<T> unbox(App<Mu, T> box) {
        return ((ListBox)box).value;
    }

    public static <T> ListBox<T> create(List<T> value) {
        return new ListBox<T>(value);
    }

    private ListBox(List<T> value) {
        this.value = value;
    }

    public static <F extends K1, A, B> App<F, List<B>> traverse(Applicative<F, ?> applicative, Function<A, App<F, B>> function, List<A> input) {
        return applicative.map(ListBox::unbox, Instance.INSTANCE.traverse(applicative, function, ListBox.create(input)));
    }

    public static <F extends K1, A> App<F, List<A>> flip(Applicative<F, ?> applicative, List<App<F, A>> input) {
        return applicative.map(ListBox::unbox, Instance.INSTANCE.flip(applicative, ListBox.create(input)));
    }

    public static enum Instance implements Traversable<com.mojang.datafixers.kinds.ListBox$Mu, Mu>
    {
        INSTANCE;


        @Override
        public <T, R> App<com.mojang.datafixers.kinds.ListBox$Mu, R> map(Function<? super T, ? extends R> func, App<com.mojang.datafixers.kinds.ListBox$Mu, T> ts) {
            return ListBox.create(ListBox.unbox(ts).stream().map(func).collect(Collectors.toList()));
        }

        @Override
        public <F extends K1, A, B> App<F, App<com.mojang.datafixers.kinds.ListBox$Mu, B>> traverse(Applicative<F, ?> applicative, Function<A, App<F, B>> function, App<com.mojang.datafixers.kinds.ListBox$Mu, A> input) {
            List<A> list = ListBox.unbox(input);
            App result = applicative.point(ImmutableList.builder());
            for (A a : list) {
                App<F, B> fb = function.apply(a);
                result = applicative.ap2(applicative.point(ImmutableList.Builder::add), result, fb);
            }
            return applicative.map((? super T b) -> ListBox.create(b.build()), result);
        }

        public static final class Mu
        implements Traversable.Mu {
        }
    }

    public static final class Mu
    implements K1 {
    }
}
