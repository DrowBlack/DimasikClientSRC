package net.minecraft.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorldReader;

public interface IDayTimeReader
extends IWorldReader {
    public long func_241851_ab();

    default public float getMoonFactor() {
        return DimensionType.MOON_PHASE_FACTORS[this.getDimensionType().getMoonPhase(this.func_241851_ab())];
    }

    default public float func_242415_f(float p_242415_1_) {
        return this.getDimensionType().getCelestrialAngleByTime(this.func_241851_ab());
    }

    default public int getMoonPhase() {
        return this.getDimensionType().getMoonPhase(this.func_241851_ab());
    }
}
