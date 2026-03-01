package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import net.minecraft.world.gen.layer.traits.IDimOffset1Transformer;

public interface IC1Transformer
extends IAreaTransformer1,
IDimOffset1Transformer {
    public int apply(INoiseRandom var1, int var2);

    @Override
    default public int apply(IExtendedNoiseRandom<?> context, IArea area, int x, int z) {
        int i = area.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z + 1));
        return this.apply(context, i);
    }
}
