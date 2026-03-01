package com.mojang.datafixers.optics;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Functor;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.Optics;
import java.util.function.Function;

interface PStore<I, J, X>
extends App<Mu<I, J>, X> {
    public static <I, J, X> PStore<I, J, X> unbox(App<Mu<I, J>, X> box) {
        return (PStore)box;
    }

    public X peek(J var1);

    public I pos();

    public static final class Instance<I, J>
    implements Functor<com.mojang.datafixers.optics.PStore$Mu<I, J>, Mu<I, J>> {
        @Override
        public <T, R> App<com.mojang.datafixers.optics.PStore$Mu<I, J>, R> map(Function<? super T, ? extends R> func, App<com.mojang.datafixers.optics.PStore$Mu<I, J>, T> ts) {
            PStore<I, J, T> input = PStore.unbox(ts);
            return Optics.pStore(func.compose(input::peek)::apply, input::pos);
        }

        public static final class Mu<I, J>
        implements Functor.Mu {
        }
    }

    public static final class Mu<I, J>
    implements K1 {
    }
}
