package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class BlockBlobFeature
extends Feature<BlockStateFeatureConfig> {
    public BlockBlobFeature(Codec<BlockStateFeatureConfig> p_i231931_1_) {
        super(p_i231931_1_);
    }

    @Override
    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BlockStateFeatureConfig p_241855_5_) {
        while (true) {
            Block block;
            if (p_241855_4_.getY() <= 3 || !p_241855_1_.isAirBlock(p_241855_4_.down()) && (BlockBlobFeature.isDirt(block = p_241855_1_.getBlockState(p_241855_4_.down()).getBlock()) || BlockBlobFeature.isStone(block))) {
                if (p_241855_4_.getY() <= 3) {
                    return false;
                }
                for (int l = 0; l < 3; ++l) {
                    int i = p_241855_3_.nextInt(2);
                    int j = p_241855_3_.nextInt(2);
                    int k = p_241855_3_.nextInt(2);
                    float f = (float)(i + j + k) * 0.333f + 0.5f;
                    for (BlockPos blockpos : BlockPos.getAllInBoxMutable(p_241855_4_.add(-i, -j, -k), p_241855_4_.add(i, j, k))) {
                        if (!(blockpos.distanceSq(p_241855_4_) <= (double)(f * f))) continue;
                        p_241855_1_.setBlockState(blockpos, p_241855_5_.state, 4);
                    }
                    p_241855_4_ = p_241855_4_.add(-1 + p_241855_3_.nextInt(2), -p_241855_3_.nextInt(2), -1 + p_241855_3_.nextInt(2));
                }
                return true;
            }
            p_241855_4_ = p_241855_4_.down();
        }
    }
}
