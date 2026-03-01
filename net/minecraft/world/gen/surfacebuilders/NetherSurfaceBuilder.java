package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class NetherSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderConfig> {
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
    protected long field_205552_a;
    protected OctavesNoiseGenerator field_205553_b;

    public NetherSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232132_1_) {
        super(p_i232132_1_);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        int i = seaLevel;
        int j = x & 0xF;
        int k = z & 0xF;
        double d0 = 0.03125;
        boolean flag = this.field_205553_b.func_205563_a((double)x * 0.03125, (double)z * 0.03125, 0.0) * 75.0 + random.nextDouble() > 0.0;
        boolean flag1 = this.field_205553_b.func_205563_a((double)x * 0.03125, 109.0, (double)z * 0.03125) * 75.0 + random.nextDouble() > 0.0;
        int l = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int i1 = -1;
        BlockState blockstate = config.getTop();
        BlockState blockstate1 = config.getUnder();
        for (int j1 = 127; j1 >= 0; --j1) {
            blockpos$mutable.setPos(j, j1, k);
            BlockState blockstate2 = chunkIn.getBlockState(blockpos$mutable);
            if (blockstate2.isAir()) {
                i1 = -1;
                continue;
            }
            if (!blockstate2.isIn(defaultBlock.getBlock())) continue;
            if (i1 == -1) {
                boolean flag2 = false;
                if (l <= 0) {
                    flag2 = true;
                    blockstate1 = config.getUnder();
                } else if (j1 >= i - 4 && j1 <= i + 1) {
                    blockstate = config.getTop();
                    blockstate1 = config.getUnder();
                    if (flag1) {
                        blockstate = GRAVEL;
                        blockstate1 = config.getUnder();
                    }
                    if (flag) {
                        blockstate = SOUL_SAND;
                        blockstate1 = SOUL_SAND;
                    }
                }
                if (j1 < i && flag2) {
                    blockstate = defaultFluid;
                }
                i1 = l;
                if (j1 >= i - 1) {
                    chunkIn.setBlockState(blockpos$mutable, blockstate, false);
                    continue;
                }
                chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
                continue;
            }
            if (i1 <= 0) continue;
            --i1;
            chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
        }
    }

    @Override
    public void setSeed(long seed) {
        if (this.field_205552_a != seed || this.field_205553_b == null) {
            this.field_205553_b = new OctavesNoiseGenerator(new SharedSeedRandom(seed), IntStream.rangeClosed(-3, 0));
        }
        this.field_205552_a = seed;
    }
}
