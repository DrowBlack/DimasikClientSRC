package net.minecraft.util;

import javax.annotation.Nullable;

public interface IObjectIntIterable<T>
extends Iterable<T> {
    public int getId(T var1);

    @Nullable
    public T getByValue(int var1);
}
