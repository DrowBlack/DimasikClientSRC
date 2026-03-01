package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class EndIslandFeature
extends Feature<NoFeatureConfig> {
    public EndIslandFeature(Codec<NoFeatureConfig> p_i231952_1_) {
        super(p_i231952_1_);
    }

    @Override
    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_) {
        float f = p_241855_3_.nextInt(3) + 4;
        int i = 0;
        while (f > 0.5f) {
            for (int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); ++j) {
                for (int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); ++k) {
                    if (!((float)(j * j + k * k) <= (f + 1.0f) * (f + 1.0f))) continue;
                    this.setBlockState(p_241855_1_, p_241855_4_.add(j, i, k), Blocks.END_STONE.getDefaultState());
                }
            }
            f = (float)((double)f - ((double)p_241855_3_.nextInt(2) + 0.5));
            --i;
        }
        return true;
    }
}
