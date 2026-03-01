package de.maxhenkel.configbuilder.entry.serializer;

import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class StringSerializer
implements ValueSerializer<String> {
    public static final StringSerializer INSTANCE = new StringSerializer();

    @Override
    @Nullable
    public String deserialize(String str) {
        return str;
    }

    @Override
    @Nullable
    public String serialize(String val) {
        return val;
    }
}
