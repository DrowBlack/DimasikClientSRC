package net.minecraft.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;

public enum ColumnFuzzedBiomeMagnifier implements IBiomeMagnifier
{
    INSTANCE;


    @Override
    public Biome getBiome(long seed, int x, int y, int z, BiomeManager.IBiomeReader biomeReader) {
        return FuzzedBiomeMagnifier.INSTANCE.getBiome(seed, x, 0, z, biomeReader);
    }
}
