package net.minecraft.util;

import net.minecraft.util.RangedInteger;

public class TickRangeConverter {
    public static RangedInteger convertRange(int min, int max) {
        return new RangedInteger(min * 20, max * 20);
    }
}
