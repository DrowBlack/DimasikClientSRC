package net.minecraft.world.gen;

import net.minecraft.world.gen.ImprovedNoiseGenerator;

public interface INoiseRandom {
    public int random(int var1);

    public ImprovedNoiseGenerator getNoiseGenerator();
}
