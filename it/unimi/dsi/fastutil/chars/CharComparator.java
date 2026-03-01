package it.unimi.dsi.fastutil.chars;

import java.util.Comparator;

@FunctionalInterface
public interface CharComparator
extends Comparator<Character> {
    @Override
    public int compare(char var1, char var2);

    @Override
    @Deprecated
    default public int compare(Character ok1, Character ok2) {
        return this.compare(ok1.charValue(), ok2.charValue());
    }
}
