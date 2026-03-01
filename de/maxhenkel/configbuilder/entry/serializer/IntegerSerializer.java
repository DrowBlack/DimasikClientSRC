package de.maxhenkel.configbuilder.entry.serializer;

import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class IntegerSerializer
implements ValueSerializer<Integer> {
    public static final IntegerSerializer INSTANCE = new IntegerSerializer();

    @Override
    @Nullable
    public Integer deserialize(String str) {
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String serialize(Integer val) {
        return String.valueOf(val);
    }
}
