package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class BambooFeature
extends Feature<ProbabilityConfig> {
    private static final BlockState BAMBOO_BASE = (BlockState)((BlockState)((BlockState)Blocks.BAMBOO.getDefaultState().with(BambooBlock.PROPERTY_AGE, 1)).with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.NONE)).with(BambooBlock.PROPERTY_STAGE, 0);
    private static final BlockState BAMBOO_LARGE_LEAVES_GROWN = (BlockState)((BlockState)BAMBOO_BASE.with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.LARGE)).with(BambooBlock.PROPERTY_STAGE, 1);
    private static final BlockState BAMBOO_LARGE_LEAVES = (BlockState)BAMBOO_BASE.with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.LARGE);
    private static final BlockState BAMBOO_SMALL_LEAVES = (BlockState)BAMBOO_BASE.with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.SMALL);

    public BambooFeature(Codec<ProbabilityConfig> p_i231924_1_) {
        super(p_i231924_1_);
    }

    @Override
    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, ProbabilityConfig p_241855_5_) {
        int i = 0;
        BlockPos.Mutable blockpos$mutable = p_241855_4_.toMutable();
        BlockPos.Mutable blockpos$mutable1 = p_241855_4_.toMutable();
        if (p_241855_1_.isAirBlock(blockpos$mutable)) {
            if (Blocks.BAMBOO.getDefaultState().isValidPosition(p_241855_1_, blockpos$mutable)) {
                int j = p_241855_3_.nextInt(12) + 5;
                if (p_241855_3_.nextFloat() < p_241855_5_.probability) {
                    int k = p_241855_3_.nextInt(4) + 1;
                    for (int l = p_241855_4_.getX() - k; l <= p_241855_4_.getX() + k; ++l) {
                        for (int i1 = p_241855_4_.getZ() - k; i1 <= p_241855_4_.getZ() + k; ++i1) {
                            int k1;
                            int j1 = l - p_241855_4_.getX();
                            if (j1 * j1 + (k1 = i1 - p_241855_4_.getZ()) * k1 > k * k) continue;
                            blockpos$mutable1.setPos(l, p_241855_1_.getHeight(Heightmap.Type.WORLD_SURFACE, l, i1) - 1, i1);
                            if (!BambooFeature.isDirt(p_241855_1_.getBlockState(blockpos$mutable1).getBlock())) continue;
                            p_241855_1_.setBlockState(blockpos$mutable1, Blocks.PODZOL.getDefaultState(), 2);
                        }
                    }
                }
                for (int l1 = 0; l1 < j && p_241855_1_.isAirBlock(blockpos$mutable); ++l1) {
                    p_241855_1_.setBlockState(blockpos$mutable, BAMBOO_BASE, 2);
                    blockpos$mutable.move(Direction.UP, 1);
                }
                if (blockpos$mutable.getY() - p_241855_4_.getY() >= 3) {
                    p_241855_1_.setBlockState(blockpos$mutable, BAMBOO_LARGE_LEAVES_GROWN, 2);
                    p_241855_1_.setBlockState(blockpos$mutable.move(Direction.DOWN, 1), BAMBOO_LARGE_LEAVES, 2);
                    p_241855_1_.setBlockState(blockpos$mutable.move(Direction.DOWN, 1), BAMBOO_SMALL_LEAVES, 2);
                }
            }
            ++i;
        }
        return i > 0;
    }
}
