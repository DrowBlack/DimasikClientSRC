package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;

public class StateContainer<O, S extends StateHolder<O, S>> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> properties;
    private final ImmutableList<S> validStates;

    protected StateContainer(Function<O, S> p_i231877_1_, O p_i231877_2_, IFactory<O, S> p_i231877_3_, Map<String, Property<?>> p_i231877_4_) {
        this.owner = p_i231877_2_;
        this.properties = ImmutableSortedMap.copyOf(p_i231877_4_);
        Supplier<StateHolder> supplier = () -> (StateHolder)p_i231877_1_.apply(p_i231877_2_);
        MapCodec<StateHolder> mapcodec = MapCodec.of(Encoder.empty(), Decoder.unit(supplier));
        for (Map.Entry entry : this.properties.entrySet()) {
            mapcodec = StateContainer.func_241487_a_(mapcodec, supplier, (String)entry.getKey(), (Property)entry.getValue());
        }
        MapCodec<StateHolder> mapcodec1 = mapcodec;
        LinkedHashMap map = Maps.newLinkedHashMap();
        ArrayList<StateHolder> list = Lists.newArrayList();
        Stream<List<List<Object>>> stream = Stream.of(Collections.emptyList());
        for (Property property : this.properties.values()) {
            stream = stream.flatMap(p_200999_1_ -> property.getAllowedValues().stream().map(p_200998_2_ -> {
                ArrayList<Pair<Property, Comparable>> list1 = Lists.newArrayList(p_200999_1_);
                list1.add(Pair.of(property, p_200998_2_));
                return list1;
            }));
        }
        stream.forEach(p_201000_5_ -> {
            ImmutableMap<Property<?>, Comparable<?>> immutablemap = p_201000_5_.stream().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
            StateHolder s1 = (StateHolder)p_i231877_3_.create(p_i231877_2_, immutablemap, mapcodec1);
            map.put(immutablemap, s1);
            list.add(s1);
        });
        for (StateHolder s : list) {
            s.func_235899_a_(map);
        }
        this.validStates = ImmutableList.copyOf(list);
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> MapCodec<S> func_241487_a_(MapCodec<S> p_241487_0_, Supplier<S> p_241487_1_, String p_241487_2_, Property<T> p_241487_3_) {
        return Codec.mapPair(p_241487_0_, ((MapCodec)p_241487_3_.func_241492_e_().fieldOf(p_241487_2_)).setPartial(() -> p_241487_3_.func_241489_a_((StateHolder)p_241487_1_.get()))).xmap(p_241485_1_ -> (StateHolder)((StateHolder)p_241485_1_.getFirst()).with(p_241487_3_, ((Property.ValuePair)p_241485_1_.getSecond()).func_241493_b_()), p_241484_1_ -> Pair.of(p_241484_1_, p_241487_3_.func_241489_a_((StateHolder<?, ?>)p_241484_1_)));
    }

    public ImmutableList<S> getValidStates() {
        return this.validStates;
    }

    public S getBaseState() {
        return (S)((StateHolder)this.validStates.get(0));
    }

    public O getOwner() {
        return this.owner;
    }

    public Collection<Property<?>> getProperties() {
        return this.properties.values();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.properties.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }

    @Nullable
    public Property<?> getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }

    public static interface IFactory<O, S> {
        public S create(O var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3);
    }

    public static class Builder<O, S extends StateHolder<O, S>> {
        private final O owner;
        private final Map<String, Property<?>> properties = Maps.newHashMap();

        public Builder(O object) {
            this.owner = object;
        }

        public Builder<O, S> add(Property<?> ... propertiesIn) {
            for (Property<?> property : propertiesIn) {
                this.validateProperty(property);
                this.properties.put(property.getName(), property);
            }
            return this;
        }

        private <T extends Comparable<T>> void validateProperty(Property<T> property) {
            String s = property.getName();
            if (!NAME_PATTERN.matcher(s).matches()) {
                throw new IllegalArgumentException(String.valueOf(this.owner) + " has invalidly named property: " + s);
            }
            Collection<T> collection = property.getAllowedValues();
            if (collection.size() <= 1) {
                throw new IllegalArgumentException(String.valueOf(this.owner) + " attempted use property " + s + " with <= 1 possible values");
            }
            for (Comparable t : collection) {
                String s1 = property.getName(t);
                if (NAME_PATTERN.matcher(s1).matches()) continue;
                throw new IllegalArgumentException(String.valueOf(this.owner) + " has property: " + s + " with invalidly named value: " + s1);
            }
            if (this.properties.containsKey(s)) {
                throw new IllegalArgumentException(String.valueOf(this.owner) + " has duplicate property: " + s);
            }
        }

        public StateContainer<O, S> func_235882_a_(Function<O, S> p_235882_1_, IFactory<O, S> p_235882_2_) {
            return new StateContainer<O, S>(p_235882_1_, this.owner, p_235882_2_, this.properties);
        }
    }
}
