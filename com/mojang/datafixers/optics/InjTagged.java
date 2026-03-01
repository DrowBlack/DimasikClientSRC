package com.mojang.datafixers.optics;

import com.mojang.datafixers.optics.Prism;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public final class InjTagged<K, A, B>
implements Prism<Pair<K, ?>, Pair<K, ?>, A, B> {
    private final K key;

    public InjTagged(K key) {
        this.key = key;
    }

    @Override
    public Either<Pair<K, ?>, A> match(Pair<K, ?> pair) {
        return Objects.equals(this.key, pair.getFirst()) ? Either.right(pair.getSecond()) : Either.left(pair);
    }

    @Override
    public Pair<K, ?> build(B b) {
        return Pair.of(this.key, b);
    }

    public String toString() {
        return "inj[" + this.key + "]";
    }

    public boolean equals(Object obj) {
        return obj instanceof InjTagged && Objects.equals(((InjTagged)obj).key, this.key);
    }
}
