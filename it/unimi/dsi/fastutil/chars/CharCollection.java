package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.CharIterable;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface CharCollection
extends Collection<Character>,
CharIterable {
    @Override
    public CharIterator iterator();

    @Override
    public boolean add(char var1);

    public boolean contains(char var1);

    public boolean rem(char var1);

    @Override
    @Deprecated
    default public boolean add(Character key) {
        return this.add(key.charValue());
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains(((Character)key).charValue());
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem(((Character)key).charValue());
    }

    public char[] toCharArray();

    @Deprecated
    public char[] toCharArray(char[] var1);

    public char[] toArray(char[] var1);

    public boolean addAll(CharCollection var1);

    public boolean containsAll(CharCollection var1);

    public boolean removeAll(CharCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Character> filter) {
        return this.removeIf((int key) -> filter.test(Character.valueOf(SafeMath.safeIntToChar(key))));
    }

    default public boolean removeIf(IntPredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        CharIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextChar())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    public boolean retainAll(CharCollection var1);
}
