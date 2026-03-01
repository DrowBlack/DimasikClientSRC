package net.minecraft.entity.passive.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum CoatTypes {
    NONE(0),
    WHITE(1),
    WHITE_FIELD(2),
    WHITE_DOTS(3),
    BLACK_DOTS(4);

    private static final CoatTypes[] VALUES;
    private final int id;

    private CoatTypes(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static CoatTypes func_234248_a_(int p_234248_0_) {
        return VALUES[p_234248_0_ % VALUES.length];
    }

    static {
        VALUES = (CoatTypes[])Arrays.stream(CoatTypes.values()).sorted(Comparator.comparingInt(CoatTypes::getId)).toArray(CoatTypes[]::new);
    }
}
