package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.BlockState;

public class PlantBlockHelper {
    public static boolean isAir(BlockState state) {
        return state.isAir();
    }

    public static int getGrowthAmount(Random rand) {
        double d0 = 1.0;
        int i = 0;
        while (rand.nextDouble() < d0) {
            d0 *= 0.826;
            ++i;
        }
        return i;
    }
}
