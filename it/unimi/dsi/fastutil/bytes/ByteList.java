package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import java.util.List;

public interface ByteList
extends List<Byte>,
Comparable<List<? extends Byte>>,
ByteCollection {
    @Override
    public ByteListIterator iterator();

    public ByteListIterator listIterator();

    public ByteListIterator listIterator(int var1);

    public ByteList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, byte[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, byte[] var2);

    public void addElements(int var1, byte[] var2, int var3, int var4);

    @Override
    public boolean add(byte var1);

    @Override
    public void add(int var1, byte var2);

    @Override
    @Deprecated
    default public void add(int index, Byte key) {
        this.add(index, (byte)key);
    }

    public boolean addAll(int var1, ByteCollection var2);

    public boolean addAll(int var1, ByteList var2);

    public boolean addAll(ByteList var1);

    @Override
    public byte set(int var1, byte var2);

    public byte getByte(int var1);

    public int indexOf(byte var1);

    public int lastIndexOf(byte var1);

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        return ByteCollection.super.contains(key);
    }

    @Override
    @Deprecated
    default public Byte get(int index) {
        return this.getByte(index);
    }

    @Override
    @Deprecated
    default public int indexOf(Object o) {
        return this.indexOf((Byte)o);
    }

    @Override
    @Deprecated
    default public int lastIndexOf(Object o) {
        return this.lastIndexOf((Byte)o);
    }

    @Override
    @Deprecated
    default public boolean add(Byte k) {
        return this.add((byte)k);
    }

    public byte removeByte(int var1);

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        return ByteCollection.super.remove(key);
    }

    @Override
    @Deprecated
    default public Byte remove(int index) {
        return this.removeByte(index);
    }

    @Override
    @Deprecated
    default public Byte set(int index, Byte k) {
        return this.set(index, (byte)k);
    }
}
