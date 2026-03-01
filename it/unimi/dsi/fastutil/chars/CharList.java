package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import java.util.List;

public interface CharList
extends List<Character>,
Comparable<List<? extends Character>>,
CharCollection {
    @Override
    public CharListIterator iterator();

    public CharListIterator listIterator();

    public CharListIterator listIterator(int var1);

    public CharList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, char[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, char[] var2);

    public void addElements(int var1, char[] var2, int var3, int var4);

    @Override
    public boolean add(char var1);

    @Override
    public void add(int var1, char var2);

    @Override
    @Deprecated
    default public void add(int index, Character key) {
        this.add(index, key.charValue());
    }

    public boolean addAll(int var1, CharCollection var2);

    public boolean addAll(int var1, CharList var2);

    public boolean addAll(CharList var1);

    @Override
    public char set(int var1, char var2);

    public char getChar(int var1);

    public int indexOf(char var1);

    public int lastIndexOf(char var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return CharCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Character get(int index) {
        return Character.valueOf(this.getChar(index));
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf(((Character)o).charValue());
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf(((Character)o).charValue());
    }

    @Override
    @Deprecated
    default public boolean add(Character k) {
        return this.add(k.charValue());
    }

    public char removeChar(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return CharCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Character remove(int index) {
        return Character.valueOf(this.removeChar(index));
    }

    @Override
    @Deprecated
    default public Character set(int index, Character k) {
        return Character.valueOf(this.set(index, k.charValue()));
    }
}
