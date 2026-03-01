package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ByteIterable
extends Iterable<Byte> {
    public ByteIterator iterator();

    default public void forEach(IntConsumer action) {
        Objects.requireNonNull(action);
        ByteIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.nextByte());
        }
    }

    @Override
    @Deprecated
    default public void forEach(final Consumer<? super Byte> action) {
        this.forEach(new IntConsumer(){

            @Override
            public void accept(int key) {
                action.accept((byte)key);
            }
        });
    }
}
