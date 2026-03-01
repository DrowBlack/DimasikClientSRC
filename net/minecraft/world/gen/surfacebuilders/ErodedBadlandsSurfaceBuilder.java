package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.BadlandsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class ErodedBadlandsSurfaceBuilder
extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

    public ErodedBadlandsSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232125_1_) {
        super(p_i232125_1_);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        double d0 = 0.0;
        double d1 = Math.min(Math.abs(noise), this.field_215435_c.noiseAt((double)x * 0.25, (double)z * 0.25, false) * 15.0);
        if (d1 > 0.0) {
            double d2 = 0.001953125;
            d0 = d1 * d1 * 2.5;
            double d3 = Math.abs(this.field_215437_d.noiseAt((double)x * 0.001953125, (double)z * 0.001953125, false));
            double d4 = Math.ceil(d3 * 50.0) + 14.0;
            if (d0 > d4) {
                d0 = d4;
            }
            d0 += 64.0;
        }
        int i1 = x & 0xF;
        int i = z & 0xF;
        BlockState blockstate3 = WHITE_TERRACOTTA;
        ISurfaceBuilderConfig isurfacebuilderconfig = biomeIn.getGenerationSettings().getSurfaceBuilderConfig();
        BlockState blockstate4 = isurfacebuilderconfig.getUnder();
        BlockState blockstate = isurfacebuilderconfig.getTop();
        BlockState blockstate1 = blockstate4;
        int j = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean flag = Math.cos(noise / 3.0 * Math.PI) > 0.0;
        int k = -1;
        boolean flag1 = false;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int l = Math.max(startHeight, (int)d0 + 1); l >= 0; --l) {
            BlockState blockstate2;
            blockpos$mutable.setPos(i1, l, i);
            if (chunkIn.getBlockState(blockpos$mutable).isAir() && l < (int)d0) {
                chunkIn.setBlockState(blockpos$mutable, defaultBlock, false);
            }
            if ((blockstate2 = chunkIn.getBlockState(blockpos$mutable)).isAir()) {
                k = -1;
                continue;
            }
            if (!blockstate2.isIn(defaultBlock.getBlock())) continue;
            if (k == -1) {
                flag1 = false;
                if (j <= 0) {
                    blockstate3 = Blocks.AIR.getDefaultState();
                    blockstate1 = defaultBlock;
                } else if (l >= seaLevel - 4 && l <= seaLevel + 1) {
                    blockstate3 = WHITE_TERRACOTTA;
                    blockstate1 = blockstate4;
                }
                if (l < seaLevel && (blockstate3 == null || blockstate3.isAir())) {
                    blockstate3 = defaultFluid;
                }
                k = j + Math.max(0, l - seaLevel);
                if (l >= seaLevel - 1) {
                    if (l <= seaLevel + 3 + j) {
                        chunkIn.setBlockState(blockpos$mutable, blockstate, false);
                        flag1 = true;
                        continue;
                    }
                    BlockState blockstate5 = l >= 64 && l <= 127 ? (flag ? TERRACOTTA : this.func_215431_a(x, l, z)) : ORANGE_TERRACOTTA;
                    chunkIn.setBlockState(blockpos$mutable, blockstate5, false);
                    continue;
                }
                chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
                Block block = blockstate1.getBlock();
                if (block != Blocks.WHITE_TERRACOTTA && block != Blocks.ORANGE_TERRACOTTA && block != Blocks.MAGENTA_TERRACOTTA && block != Blocks.LIGHT_BLUE_TERRACOTTA && block != Blocks.YELLOW_TERRACOTTA && block != Blocks.LIME_TERRACOTTA && block != Blocks.PINK_TERRACOTTA && block != Blocks.GRAY_TERRACOTTA && block != Blocks.LIGHT_GRAY_TERRACOTTA && block != Blocks.CYAN_TERRACOTTA && block != Blocks.PURPLE_TERRACOTTA && block != Blocks.BLUE_TERRACOTTA && block != Blocks.BROWN_TERRACOTTA && block != Blocks.GREEN_TERRACOTTA && block != Blocks.RED_TERRACOTTA && block != Blocks.BLACK_TERRACOTTA) continue;
                chunkIn.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                continue;
            }
            if (k <= 0) continue;
            --k;
            if (flag1) {
                chunkIn.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                continue;
            }
            chunkIn.setBlockState(blockpos$mutable, this.func_215431_a(x, l, z), false);
        }
    }
}
