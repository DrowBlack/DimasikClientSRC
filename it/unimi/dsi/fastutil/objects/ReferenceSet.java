package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Set;

public interface ReferenceSet<K>
extends ReferenceCollection<K>,
Set<K> {
    @Override
    public ObjectIterator<K> iterator();
}
