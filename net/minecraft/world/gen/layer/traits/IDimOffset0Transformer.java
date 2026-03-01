package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.layer.traits.IDimTransformer;

public interface IDimOffset0Transformer
extends IDimTransformer {
    @Override
    default public int getOffsetX(int x) {
        return x;
    }

    @Override
    default public int getOffsetZ(int z) {
        return z;
    }
}
