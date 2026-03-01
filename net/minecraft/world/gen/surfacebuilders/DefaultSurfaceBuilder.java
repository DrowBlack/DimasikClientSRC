package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class DefaultSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderConfig> {
    public DefaultSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232124_1_) {
        super(p_i232124_1_);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        this.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, config.getTop(), config.getUnder(), config.getUnderWaterMaterial(), seaLevel);
    }

    protected void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, BlockState top, BlockState middle, BlockState bottom, int sealevel) {
        BlockState blockstate = top;
        BlockState blockstate1 = middle;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int i = -1;
        int j = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int k = x & 0xF;
        int l = z & 0xF;
        for (int i1 = startHeight; i1 >= 0; --i1) {
            blockpos$mutable.setPos(k, i1, l);
            BlockState blockstate2 = chunkIn.getBlockState(blockpos$mutable);
            if (blockstate2.isAir()) {
                i = -1;
                continue;
            }
            if (!blockstate2.isIn(defaultBlock.getBlock())) continue;
            if (i == -1) {
                if (j <= 0) {
                    blockstate = Blocks.AIR.getDefaultState();
                    blockstate1 = defaultBlock;
                } else if (i1 >= sealevel - 4 && i1 <= sealevel + 1) {
                    blockstate = top;
                    blockstate1 = middle;
                }
                if (i1 < sealevel && (blockstate == null || blockstate.isAir())) {
                    blockstate = biomeIn.getTemperature(blockpos$mutable.setPos(x, i1, z)) < 0.15f ? Blocks.ICE.getDefaultState() : defaultFluid;
                    blockpos$mutable.setPos(k, i1, l);
                }
                i = j;
                if (i1 >= sealevel - 1) {
                    chunkIn.setBlockState(blockpos$mutable, blockstate, false);
                    continue;
                }
                if (i1 < sealevel - 7 - j) {
                    blockstate = Blocks.AIR.getDefaultState();
                    blockstate1 = defaultBlock;
                    chunkIn.setBlockState(blockpos$mutable, bottom, false);
                    continue;
                }
                chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
                continue;
            }
            if (i <= 0) continue;
            chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
            if (--i != 0 || !blockstate1.isIn(Blocks.SAND) || j <= 1) continue;
            i = random.nextInt(4) + Math.max(0, i1 - 63);
            blockstate1 = blockstate1.isIn(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
        }
    }
}
