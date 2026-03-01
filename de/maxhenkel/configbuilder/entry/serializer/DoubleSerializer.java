package de.maxhenkel.configbuilder.entry.serializer;

import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class DoubleSerializer
implements ValueSerializer<Double> {
    public static final DoubleSerializer INSTANCE = new DoubleSerializer();

    @Override
    @Nullable
    public Double deserialize(String str) {
        try {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String serialize(Double val) {
        return String.valueOf(val);
    }
}
