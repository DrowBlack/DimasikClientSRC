package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectIterator;

public interface ObjectIterable<K>
extends Iterable<K> {
    @Override
    public ObjectIterator<K> iterator();
}
