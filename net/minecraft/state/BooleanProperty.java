package net.minecraft.state;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.state.Property;

public class BooleanProperty
extends Property<Boolean> {
    private final ImmutableSet<Boolean> allowedValues = ImmutableSet.of(Boolean.valueOf(true), Boolean.valueOf(false));

    protected BooleanProperty(String name) {
        super(name, Boolean.class);
    }

    @Override
    public Collection<Boolean> getAllowedValues() {
        return this.allowedValues;
    }

    public static BooleanProperty create(String name) {
        return new BooleanProperty(name);
    }

    @Override
    public Optional<Boolean> parseValue(String value) {
        return !"true".equals(value) && !"false".equals(value) ? Optional.empty() : Optional.of(Boolean.valueOf(value));
    }

    @Override
    public String getName(Boolean value) {
        return value.toString();
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ instanceof BooleanProperty && super.equals(p_equals_1_)) {
            BooleanProperty booleanproperty = (BooleanProperty)p_equals_1_;
            return this.allowedValues.equals(booleanproperty.allowedValues);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.allowedValues.hashCode();
    }
}
