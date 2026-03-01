package net.minecraft.world.gen.blockstateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class SimpleBlockStateProvider
extends BlockStateProvider {
    public static final Codec<SimpleBlockStateProvider> CODEC = ((MapCodec)BlockState.CODEC.fieldOf("state")).xmap(SimpleBlockStateProvider::new, provider -> provider.state).codec();
    private final BlockState state;

    public SimpleBlockStateProvider(BlockState state) {
        this.state = state;
    }

    @Override
    protected BlockStateProviderType<?> getProviderType() {
        return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random randomIn, BlockPos blockPosIn) {
        return this.state;
    }
}
