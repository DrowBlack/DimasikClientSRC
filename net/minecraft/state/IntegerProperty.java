package net.minecraft.state;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import net.minecraft.state.Property;

public class IntegerProperty
extends Property<Integer> {
    private final ImmutableSet<Integer> allowedValues;

    protected IntegerProperty(String name, int min, int max) {
        super(name, Integer.class);
        if (min < 0) {
            throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
        }
        if (max <= min) {
            throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
        }
        HashSet<Integer> set = Sets.newHashSet();
        for (int i = min; i <= max; ++i) {
            set.add(i);
        }
        this.allowedValues = ImmutableSet.copyOf(set);
    }

    @Override
    public Collection<Integer> getAllowedValues() {
        return this.allowedValues;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ instanceof IntegerProperty && super.equals(p_equals_1_)) {
            IntegerProperty integerproperty = (IntegerProperty)p_equals_1_;
            return this.allowedValues.equals(integerproperty.allowedValues);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.allowedValues.hashCode();
    }

    public static IntegerProperty create(String name, int min, int max) {
        return new IntegerProperty(name, min, max);
    }

    @Override
    public Optional<Integer> parseValue(String value) {
        try {
            Integer integer = Integer.valueOf(value);
            return this.allowedValues.contains(integer) ? Optional.of(integer) : Optional.empty();
        }
        catch (NumberFormatException numberformatexception) {
            return Optional.empty();
        }
    }

    @Override
    public String getName(Integer value) {
        return value.toString();
    }
}
