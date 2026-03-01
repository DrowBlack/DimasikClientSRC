package net.minecraft.state;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.state.Property;
import net.minecraft.util.IStringSerializable;

public class EnumProperty<T extends Enum<T>>
extends Property<T> {
    private final ImmutableSet<T> allowedValues;
    private final Map<String, T> nameToValue = Maps.newHashMap();

    protected EnumProperty(String name, Class<T> valueClass, Collection<T> allowedValues) {
        super(name, valueClass);
        this.allowedValues = ImmutableSet.copyOf(allowedValues);
        for (Enum t : allowedValues) {
            String s = ((IStringSerializable)((Object)t)).getString();
            if (this.nameToValue.containsKey(s)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + s + "'");
            }
            this.nameToValue.put(s, t);
        }
    }

    @Override
    public Collection<T> getAllowedValues() {
        return this.allowedValues;
    }

    @Override
    public Optional<T> parseValue(String value) {
        return Optional.ofNullable((Enum)this.nameToValue.get(value));
    }

    @Override
    public String getName(T value) {
        return ((IStringSerializable)value).getString();
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ instanceof EnumProperty && super.equals(p_equals_1_)) {
            EnumProperty enumproperty = (EnumProperty)p_equals_1_;
            return this.allowedValues.equals(enumproperty.allowedValues) && this.nameToValue.equals(enumproperty.nameToValue);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int i = super.computeHashCode();
        i = 31 * i + this.allowedValues.hashCode();
        return 31 * i + this.nameToValue.hashCode();
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String name, Class<T> clazz) {
        return EnumProperty.create(name, clazz, Predicates.alwaysTrue());
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String name, Class<T> clazz, Predicate<T> filter) {
        return EnumProperty.create(name, clazz, Arrays.stream((Enum[])clazz.getEnumConstants()).filter(filter).collect(Collectors.toList()));
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String name, Class<T> clazz, T ... values) {
        return EnumProperty.create(name, clazz, Lists.newArrayList(values));
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String name, Class<T> clazz, Collection<T> values) {
        return new EnumProperty<T>(name, clazz, values);
    }
}
