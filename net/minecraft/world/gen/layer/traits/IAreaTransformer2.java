package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.traits.IDimTransformer;

public interface IAreaTransformer2
extends IDimTransformer {
    default public <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context, IAreaFactory<R> areaFactory, IAreaFactory<R> areaFactoryIn) {
        return () -> {
            Object r = areaFactory.make();
            Object r1 = areaFactoryIn.make();
            return context.makeArea((p_215724_4_, p_215724_5_) -> {
                context.setPosition(p_215724_4_, p_215724_5_);
                return this.apply(context, (IArea)r, (IArea)r1, p_215724_4_, p_215724_5_);
            }, r, r1);
        };
    }

    public int apply(INoiseRandom var1, IArea var2, IArea var3, int var4, int var5);
}
