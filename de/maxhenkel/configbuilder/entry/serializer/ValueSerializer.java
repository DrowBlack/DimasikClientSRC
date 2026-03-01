package de.maxhenkel.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public interface ValueSerializer<T> {
    @Nullable
    public T deserialize(String var1);

    @Nullable
    public String serialize(T var1);
}
