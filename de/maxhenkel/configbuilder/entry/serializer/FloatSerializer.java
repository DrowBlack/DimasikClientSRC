package de.maxhenkel.configbuilder.entry.serializer;

import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class FloatSerializer
implements ValueSerializer<Float> {
    public static final FloatSerializer INSTANCE = new FloatSerializer();

    @Override
    @Nullable
    public Float deserialize(String str) {
        try {
            return Float.valueOf(Float.parseFloat(str));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String serialize(Float val) {
        return String.valueOf(val);
    }
}
