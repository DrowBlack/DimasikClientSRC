package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class ForestFlowerBlockStateProvider
extends BlockStateProvider {
    public static final Codec<ForestFlowerBlockStateProvider> CODEC;
    private static final BlockState[] STATES;
    public static final ForestFlowerBlockStateProvider PROVIDER;

    @Override
    protected BlockStateProviderType<?> getProviderType() {
        return BlockStateProviderType.FOREST_FLOWER_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random randomIn, BlockPos blockPosIn) {
        double d0 = MathHelper.clamp((1.0 + Biome.INFO_NOISE.noiseAt((double)blockPosIn.getX() / 48.0, (double)blockPosIn.getZ() / 48.0, false)) / 2.0, 0.0, 0.9999);
        return STATES[(int)(d0 * (double)STATES.length)];
    }

    static {
        STATES = new BlockState[]{Blocks.DANDELION.getDefaultState(), Blocks.POPPY.getDefaultState(), Blocks.ALLIUM.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.ORANGE_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState(), Blocks.LILY_OF_THE_VALLEY.getDefaultState()};
        PROVIDER = new ForestFlowerBlockStateProvider();
        CODEC = Codec.unit(() -> PROVIDER);
    }
}
