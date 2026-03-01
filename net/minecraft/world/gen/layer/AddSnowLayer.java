package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum AddSnowLayer implements IC1Transformer
{
    INSTANCE;


    @Override
    public int apply(INoiseRandom context, int value) {
        if (LayerUtil.isShallowOcean(value)) {
            return value;
        }
        int i = context.random(6);
        if (i == 0) {
            return 4;
        }
        return i == 1 ? 3 : 1;
    }
}
