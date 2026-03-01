package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.ImprovedNoiseGenerator;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum OceanLayer implements IAreaTransformer0
{
    INSTANCE;


    @Override
    public int apply(INoiseRandom p_215735_1_, int p_215735_2_, int p_215735_3_) {
        ImprovedNoiseGenerator improvednoisegenerator = p_215735_1_.getNoiseGenerator();
        double d0 = improvednoisegenerator.func_215456_a((double)p_215735_2_ / 8.0, (double)p_215735_3_ / 8.0, 0.0, 0.0, 0.0);
        if (d0 > 0.4) {
            return 44;
        }
        if (d0 > 0.2) {
            return 45;
        }
        if (d0 < -0.4) {
            return 10;
        }
        return d0 < -0.2 ? 46 : 0;
    }
}
