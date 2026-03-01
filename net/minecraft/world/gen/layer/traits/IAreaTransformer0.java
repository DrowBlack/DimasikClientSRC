package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer0 {
    default public <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context) {
        return () -> context.makeArea((p_202820_2_, p_202820_3_) -> {
            context.setPosition(p_202820_2_, p_202820_3_);
            return this.apply(context, p_202820_2_, p_202820_3_);
        });
    }

    public int apply(INoiseRandom var1, int var2, int var3);
}
