package net.minecraft.entity.passive.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum CoatColors {
    WHITE(0),
    CREAMY(1),
    CHESTNUT(2),
    BROWN(3),
    BLACK(4),
    GRAY(5),
    DARKBROWN(6);

    private static final CoatColors[] VALUES;
    private final int id;

    private CoatColors(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static CoatColors func_234254_a_(int p_234254_0_) {
        return VALUES[p_234254_0_ % VALUES.length];
    }

    static {
        VALUES = (CoatColors[])Arrays.stream(CoatColors.values()).sorted(Comparator.comparingInt(CoatColors::getId)).toArray(CoatColors[]::new);
    }
}
