package com.ibm.icu.util;

public interface Freezable<T>
extends Cloneable {
    public boolean isFrozen();

    public T freeze();

    public T cloneAsThawed();
}
