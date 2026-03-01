package de.maxhenkel.configbuilder.entry.serializer;

import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class EnumSerializer<E extends Enum<E>>
implements ValueSerializer<E> {
    protected Class<E> enumClass;

    public EnumSerializer(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    @Nullable
    public E deserialize(String str) {
        try {
            return Enum.valueOf(this.enumClass, str);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String serialize(E val) {
        return ((Enum)val).name();
    }
}
