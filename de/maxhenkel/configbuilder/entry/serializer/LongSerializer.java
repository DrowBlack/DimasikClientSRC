package de.maxhenkel.configbuilder.entry.serializer;

import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class LongSerializer
implements ValueSerializer<Long> {
    public static final LongSerializer INSTANCE = new LongSerializer();

    @Override
    @Nullable
    public Long deserialize(String str) {
        try {
            return Long.parseLong(str);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String serialize(Long val) {
        return String.valueOf(val);
    }
}
