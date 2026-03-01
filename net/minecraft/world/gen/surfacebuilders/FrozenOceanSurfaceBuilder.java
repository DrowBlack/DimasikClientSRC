package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class FrozenOceanSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderConfig> {
    protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
    protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final BlockState ICE = Blocks.ICE.getDefaultState();
    private PerlinNoiseGenerator field_205199_h;
    private PerlinNoiseGenerator field_205200_i;
    private long seed;

    public FrozenOceanSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232126_1_) {
        super(p_i232126_1_);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        double d0 = 0.0;
        double d1 = 0.0;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        float f = biomeIn.getTemperature(blockpos$mutable.setPos(x, 63, z));
        double d2 = Math.min(Math.abs(noise), this.field_205199_h.noiseAt((double)x * 0.1, (double)z * 0.1, false) * 15.0);
        if (d2 > 1.8) {
            double d3 = 0.09765625;
            d0 = d2 * d2 * 1.2;
            double d4 = Math.abs(this.field_205200_i.noiseAt((double)x * 0.09765625, (double)z * 0.09765625, false));
            double d5 = Math.ceil(d4 * 40.0) + 14.0;
            if (d0 > d5) {
                d0 = d5;
            }
            if (f > 0.1f) {
                d0 -= 2.0;
            }
            if (d0 > 2.0) {
                d1 = (double)seaLevel - d0 - 7.0;
                d0 += (double)seaLevel;
            } else {
                d0 = 0.0;
            }
        }
        int l1 = x & 0xF;
        int i = z & 0xF;
        ISurfaceBuilderConfig isurfacebuilderconfig = biomeIn.getGenerationSettings().getSurfaceBuilderConfig();
        BlockState blockstate = isurfacebuilderconfig.getUnder();
        BlockState blockstate4 = isurfacebuilderconfig.getTop();
        BlockState blockstate1 = blockstate;
        BlockState blockstate2 = blockstate4;
        int j = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int k = -1;
        int l = 0;
        int i1 = 2 + random.nextInt(4);
        int j1 = seaLevel + 18 + random.nextInt(10);
        for (int k1 = Math.max(startHeight, (int)d0 + 1); k1 >= 0; --k1) {
            blockpos$mutable.setPos(l1, k1, i);
            if (chunkIn.getBlockState(blockpos$mutable).isAir() && k1 < (int)d0 && random.nextDouble() > 0.01) {
                chunkIn.setBlockState(blockpos$mutable, PACKED_ICE, false);
            } else if (chunkIn.getBlockState(blockpos$mutable).getMaterial() == Material.WATER && k1 > (int)d1 && k1 < seaLevel && d1 != 0.0 && random.nextDouble() > 0.15) {
                chunkIn.setBlockState(blockpos$mutable, PACKED_ICE, false);
            }
            BlockState blockstate3 = chunkIn.getBlockState(blockpos$mutable);
            if (blockstate3.isAir()) {
                k = -1;
                continue;
            }
            if (!blockstate3.isIn(defaultBlock.getBlock())) {
                if (!blockstate3.isIn(Blocks.PACKED_ICE) || l > i1 || k1 <= j1) continue;
                chunkIn.setBlockState(blockpos$mutable, SNOW_BLOCK, false);
                ++l;
                continue;
            }
            if (k == -1) {
                if (j <= 0) {
                    blockstate2 = AIR;
                    blockstate1 = defaultBlock;
                } else if (k1 >= seaLevel - 4 && k1 <= seaLevel + 1) {
                    blockstate2 = blockstate4;
                    blockstate1 = blockstate;
                }
                if (k1 < seaLevel && (blockstate2 == null || blockstate2.isAir())) {
                    blockstate2 = biomeIn.getTemperature(blockpos$mutable.setPos(x, k1, z)) < 0.15f ? ICE : defaultFluid;
                }
                k = j;
                if (k1 >= seaLevel - 1) {
                    chunkIn.setBlockState(blockpos$mutable, blockstate2, false);
                    continue;
                }
                if (k1 < seaLevel - 7 - j) {
                    blockstate2 = AIR;
                    blockstate1 = defaultBlock;
                    chunkIn.setBlockState(blockpos$mutable, GRAVEL, false);
                    continue;
                }
                chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
                continue;
            }
            if (k <= 0) continue;
            chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
            if (--k != 0 || !blockstate1.isIn(Blocks.SAND) || j <= 1) continue;
            k = random.nextInt(4) + Math.max(0, k1 - 63);
            blockstate1 = blockstate1.isIn(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
        }
    }

    @Override
    public void setSeed(long seed) {
        if (this.seed != seed || this.field_205199_h == null || this.field_205200_i == null) {
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
            this.field_205199_h = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-3, 0));
            this.field_205200_i = new PerlinNoiseGenerator(sharedseedrandom, ImmutableList.of(Integer.valueOf(0)));
        }
        this.seed = seed;
    }
}
