package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum SmoothLayer implements ICastleTransformer
{
    INSTANCE;


    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
        boolean flag1;
        boolean flag = west == east;
        boolean bl = flag1 = north == south;
        if (flag == flag1) {
            if (flag) {
                return context.random(2) == 0 ? east : north;
            }
            return center;
        }
        return flag ? east : north;
    }
}
