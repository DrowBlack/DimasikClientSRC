package net.minecraft.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.IBiomeMagnifier;

public enum DefaultBiomeMagnifier implements IBiomeMagnifier
{
    INSTANCE;


    @Override
    public Biome getBiome(long seed, int x, int y, int z, BiomeManager.IBiomeReader biomeReader) {
        return biomeReader.getNoiseBiome(x >> 2, y >> 2, z >> 2);
    }
}
