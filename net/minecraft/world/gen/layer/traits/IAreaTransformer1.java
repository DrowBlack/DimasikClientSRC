package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.traits.IDimTransformer;

public interface IAreaTransformer1
extends IDimTransformer {
    default public <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context, IAreaFactory<R> areaFactory) {
        return () -> {
            Object r = areaFactory.make();
            return context.makeArea((p_202711_3_, p_202711_4_) -> {
                context.setPosition(p_202711_3_, p_202711_4_);
                return this.apply(context, (IArea)r, p_202711_3_, p_202711_4_);
            }, r);
        };
    }

    public int apply(IExtendedNoiseRandom<?> var1, IArea var2, int var3, int var4);
}
