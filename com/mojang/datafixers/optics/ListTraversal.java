package com.mojang.datafixers.optics;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.Traversal;
import java.util.List;

public final class ListTraversal<A, B>
implements Traversal<List<A>, List<B>, A, B> {
    @Override
    public <F extends K1> FunctionType<List<A>, App<F, List<B>>> wander(Applicative<F, ?> applicative, FunctionType<A, App<F, B>> input) {
        return as -> {
            App result = applicative.point(ImmutableList.builder());
            for (Object a : as) {
                result = applicative.ap2(applicative.point(ImmutableList.Builder::add), result, (App)input.apply((Object)a));
            }
            return applicative.map(ImmutableList.Builder::build, result);
        };
    }

    public boolean equals(Object obj) {
        return obj instanceof ListTraversal;
    }

    public String toString() {
        return "ListTraversal";
    }
}
