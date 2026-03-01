package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Set;

public interface ByteSet
extends ByteCollection,
Set<Byte> {
    @Override
    public ByteIterator iterator();

    public boolean remove(byte var1);

    @Override
    @Deprecated
    default public boolean remove(Object o) {
        return ByteCollection.super.remove(o);
    }

    @Override
    @Deprecated
    default public boolean add(Byte o) {
        return ByteCollection.super.add(o);
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        return ByteCollection.super.contains(o);
    }

    @Override
    @Deprecated
    default public boolean rem(byte k) {
        return this.remove(k);
    }
}
