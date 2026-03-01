package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class VoidStartPlatformFeature
extends Feature<NoFeatureConfig> {
    private static final BlockPos VOID_SPAWN_POS = new BlockPos(8, 3, 8);
    private static final ChunkPos VOID_SPAWN_CHUNK_POS = new ChunkPos(VOID_SPAWN_POS);

    public VoidStartPlatformFeature(Codec<NoFeatureConfig> p_i232003_1_) {
        super(p_i232003_1_);
    }

    private static int distance(int firstX, int firstZ, int secondX, int secondZ) {
        return Math.max(Math.abs(firstX - secondX), Math.abs(firstZ - secondZ));
    }

    @Override
    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_) {
        ChunkPos chunkpos = new ChunkPos(p_241855_4_);
        if (VoidStartPlatformFeature.distance(chunkpos.x, chunkpos.z, VoidStartPlatformFeature.VOID_SPAWN_CHUNK_POS.x, VoidStartPlatformFeature.VOID_SPAWN_CHUNK_POS.z) > 1) {
            return true;
        }
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int i = chunkpos.getZStart(); i <= chunkpos.getZEnd(); ++i) {
            for (int j = chunkpos.getXStart(); j <= chunkpos.getXEnd(); ++j) {
                if (VoidStartPlatformFeature.distance(VOID_SPAWN_POS.getX(), VOID_SPAWN_POS.getZ(), j, i) > 16) continue;
                blockpos$mutable.setPos(j, VOID_SPAWN_POS.getY(), i);
                if (blockpos$mutable.equals(VOID_SPAWN_POS)) {
                    p_241855_1_.setBlockState(blockpos$mutable, Blocks.COBBLESTONE.getDefaultState(), 2);
                    continue;
                }
                p_241855_1_.setBlockState(blockpos$mutable, Blocks.STONE.getDefaultState(), 2);
            }
        }
        return true;
    }
}
