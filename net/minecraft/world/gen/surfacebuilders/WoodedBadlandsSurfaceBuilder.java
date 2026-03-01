package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.BadlandsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class WoodedBadlandsSurfaceBuilder
extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

    public WoodedBadlandsSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232138_1_) {
        super(p_i232138_1_);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        int i = x & 0xF;
        int j = z & 0xF;
        BlockState blockstate = WHITE_TERRACOTTA;
        ISurfaceBuilderConfig isurfacebuilderconfig = biomeIn.getGenerationSettings().getSurfaceBuilderConfig();
        BlockState blockstate1 = isurfacebuilderconfig.getUnder();
        BlockState blockstate2 = isurfacebuilderconfig.getTop();
        BlockState blockstate3 = blockstate1;
        int k = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean flag = Math.cos(noise / 3.0 * Math.PI) > 0.0;
        int l = -1;
        boolean flag1 = false;
        int i1 = 0;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int j1 = startHeight; j1 >= 0; --j1) {
            if (i1 >= 15) continue;
            blockpos$mutable.setPos(i, j1, j);
            BlockState blockstate4 = chunkIn.getBlockState(blockpos$mutable);
            if (blockstate4.isAir()) {
                l = -1;
                continue;
            }
            if (!blockstate4.isIn(defaultBlock.getBlock())) continue;
            if (l == -1) {
                flag1 = false;
                if (k <= 0) {
                    blockstate = Blocks.AIR.getDefaultState();
                    blockstate3 = defaultBlock;
                } else if (j1 >= seaLevel - 4 && j1 <= seaLevel + 1) {
                    blockstate = WHITE_TERRACOTTA;
                    blockstate3 = blockstate1;
                }
                if (j1 < seaLevel && (blockstate == null || blockstate.isAir())) {
                    blockstate = defaultFluid;
                }
                l = k + Math.max(0, j1 - seaLevel);
                if (j1 >= seaLevel - 1) {
                    if (j1 > 86 + k * 2) {
                        if (flag) {
                            chunkIn.setBlockState(blockpos$mutable, Blocks.COARSE_DIRT.getDefaultState(), false);
                        } else {
                            chunkIn.setBlockState(blockpos$mutable, Blocks.GRASS_BLOCK.getDefaultState(), false);
                        }
                    } else if (j1 > seaLevel + 3 + k) {
                        BlockState blockstate5 = j1 >= 64 && j1 <= 127 ? (flag ? TERRACOTTA : this.func_215431_a(x, j1, z)) : ORANGE_TERRACOTTA;
                        chunkIn.setBlockState(blockpos$mutable, blockstate5, false);
                    } else {
                        chunkIn.setBlockState(blockpos$mutable, blockstate2, false);
                        flag1 = true;
                    }
                } else {
                    chunkIn.setBlockState(blockpos$mutable, blockstate3, false);
                    if (blockstate3 == WHITE_TERRACOTTA) {
                        chunkIn.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                    }
                }
            } else if (l > 0) {
                --l;
                if (flag1) {
                    chunkIn.setBlockState(blockpos$mutable, ORANGE_TERRACOTTA, false);
                } else {
                    chunkIn.setBlockState(blockpos$mutable, this.func_215431_a(x, j1, z), false);
                }
            }
            ++i1;
        }
    }
}
