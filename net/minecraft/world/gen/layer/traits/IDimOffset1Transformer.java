package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.layer.traits.IDimTransformer;

public interface IDimOffset1Transformer
extends IDimTransformer {
    @Override
    default public int getOffsetX(int x) {
        return x - 1;
    }

    @Override
    default public int getOffsetZ(int z) {
        return z - 1;
    }
}
