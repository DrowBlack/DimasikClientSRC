package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteIterable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface ByteCollection
extends Collection<Byte>,
ByteIterable {
    @Override
    public ByteIterator iterator();

    @Override
    public boolean add(byte var1);

    public boolean contains(byte var1);

    public boolean rem(byte var1);

    @Override
    @Deprecated
    default public boolean add(Byte key) {
        return this.add((byte)key);
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains((Byte)key);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem((Byte)key);
    }

    public byte[] toByteArray();

    @Deprecated
    public byte[] toByteArray(byte[] var1);

    public byte[] toArray(byte[] var1);

    public boolean addAll(ByteCollection var1);

    public boolean containsAll(ByteCollection var1);

    public boolean removeAll(ByteCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Byte> filter) {
        return this.removeIf((int key) -> filter.test(SafeMath.safeIntToByte(key)));
    }

    default public boolean removeIf(IntPredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        ByteIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextByte())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    public boolean retainAll(ByteCollection var1);
}
