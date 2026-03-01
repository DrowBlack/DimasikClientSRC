package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassInheritanceMultiMap<T>
extends AbstractCollection<T> {
    private final Map<Class<?>, List<T>> map = Maps.newHashMap();
    private final Class<T> baseClass;
    private final List<T> values = Lists.newArrayList();

    public ClassInheritanceMultiMap(Class<T> baseClassIn) {
        this.baseClass = baseClassIn;
        this.map.put(baseClassIn, this.values);
    }

    @Override
    public boolean add(T p_add_1_) {
        boolean flag = false;
        for (Map.Entry<Class<?>, List<T>> entry : this.map.entrySet()) {
            if (!entry.getKey().isInstance(p_add_1_)) continue;
            flag |= entry.getValue().add(p_add_1_);
        }
        return flag;
    }

    @Override
    public boolean remove(Object p_remove_1_) {
        boolean flag = false;
        for (Map.Entry<Class<?>, List<T>> entry : this.map.entrySet()) {
            if (!entry.getKey().isInstance(p_remove_1_)) continue;
            List<T> list = entry.getValue();
            flag |= list.remove(p_remove_1_);
        }
        return flag;
    }

    @Override
    public boolean contains(Object p_contains_1_) {
        return this.getByClass(p_contains_1_.getClass()).contains(p_contains_1_);
    }

    public <S> Collection<S> getByClass(Class<S> p_219790_1_) {
        if (!this.baseClass.isAssignableFrom(p_219790_1_)) {
            throw new IllegalArgumentException("Don't know how to search for " + String.valueOf(p_219790_1_));
        }
        List list = this.map.computeIfAbsent(p_219790_1_, p_219791_1_ -> this.values.stream().filter(p_219791_1_::isInstance).collect(Collectors.toList()));
        return Collections.unmodifiableCollection(list);
    }

    @Override
    public Iterator<T> iterator() {
        return this.values.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.values.iterator());
    }

    public List<T> func_241289_a_() {
        return ImmutableList.copyOf(this.values);
    }

    @Override
    public int size() {
        return this.values.size();
    }
}
