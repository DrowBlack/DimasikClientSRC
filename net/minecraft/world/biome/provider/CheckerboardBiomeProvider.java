package net.minecraft.world.biome.provider;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

public class CheckerboardBiomeProvider
extends BiomeProvider {
    public static final Codec<CheckerboardBiomeProvider> CODEC = RecordCodecBuilder.create(checkerProviderCodecInstance -> checkerProviderCodecInstance.group(((MapCodec)Biome.BIOMES_CODEC.fieldOf("biomes")).forGetter(checkerProvider -> checkerProvider.biomes), ((MapCodec)Codec.intRange(0, 62).fieldOf("scale")).orElse(2).forGetter(checkerProvider -> checkerProvider.biomeScale)).apply((Applicative<CheckerboardBiomeProvider, ?>)checkerProviderCodecInstance, CheckerboardBiomeProvider::new));
    private final List<Supplier<Biome>> biomes;
    private final int biomeScaleShift;
    private final int biomeScale;

    public CheckerboardBiomeProvider(List<Supplier<Biome>> biomes, int biomeScale) {
        super(biomes.stream());
        this.biomes = biomes;
        this.biomeScaleShift = biomeScale + 2;
        this.biomeScale = biomeScale;
    }

    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }

    @Override
    public BiomeProvider getBiomeProvider(long seed) {
        return this;
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        return this.biomes.get(Math.floorMod((x >> this.biomeScaleShift) + (z >> this.biomeScaleShift), this.biomes.size())).get();
    }
}
