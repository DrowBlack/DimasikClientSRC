package net.minecraft.world.gen.area;

import net.minecraft.world.gen.area.IArea;

public interface IAreaFactory<A extends IArea> {
    public A make();
}
