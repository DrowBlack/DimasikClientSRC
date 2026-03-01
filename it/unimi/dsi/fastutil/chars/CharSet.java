package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.util.Set;

public interface CharSet
extends CharCollection,
Set<Character> {
    @Override
    public CharIterator iterator();

    public boolean remove(char var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return CharCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Character o) {
        return CharCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return CharCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(char k) {
        return this.remove(k);
    }
}
