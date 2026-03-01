package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class PlainFlowerBlockStateProvider
extends BlockStateProvider {
    public static final Codec<PlainFlowerBlockStateProvider> CODEC;
    public static final PlainFlowerBlockStateProvider PROVIDER;
    private static final BlockState[] RARE_FLOWERS;
    private static final BlockState[] COMMON_FLOWERS;

    @Override
    protected BlockStateProviderType<?> getProviderType() {
        return BlockStateProviderType.PLAIN_FLOWER_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random randomIn, BlockPos blockPosIn) {
        double d0 = Biome.INFO_NOISE.noiseAt((double)blockPosIn.getX() / 200.0, (double)blockPosIn.getZ() / 200.0, false);
        if (d0 < -0.8) {
            return Util.getRandomObject(RARE_FLOWERS, randomIn);
        }
        return randomIn.nextInt(3) > 0 ? Util.getRandomObject(COMMON_FLOWERS, randomIn) : Blocks.DANDELION.getDefaultState();
    }

    static {
        PROVIDER = new PlainFlowerBlockStateProvider();
        RARE_FLOWERS = new BlockState[]{Blocks.ORANGE_TULIP.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState()};
        COMMON_FLOWERS = new BlockState[]{Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState()};
        CODEC = Codec.unit(() -> PROVIDER);
    }
}
