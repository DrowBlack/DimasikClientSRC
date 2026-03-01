package com.mojang.datafixers.optics;

import com.mojang.datafixers.optics.Adapter;

class IdAdapter<S, T>
implements Adapter<S, T, S, T> {
    IdAdapter() {
    }

    @Override
    public S from(S s) {
        return s;
    }

    @Override
    public T to(T b) {
        return b;
    }

    public boolean equals(Object obj) {
        return obj instanceof IdAdapter;
    }

    public String toString() {
        return "id";
    }
}
