package net.minecraft.world.gen.blockplacer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

public class SimpleBlockPlacer
extends BlockPlacer {
    public static final Codec<SimpleBlockPlacer> CODEC;
    public static final SimpleBlockPlacer PLACER;

    @Override
    protected BlockPlacerType<?> getBlockPlacerType() {
        return BlockPlacerType.SIMPLE_BLOCK;
    }

    @Override
    public void place(IWorld world, BlockPos pos, BlockState state, Random random) {
        world.setBlockState(pos, state, 2);
    }

    static {
        PLACER = new SimpleBlockPlacer();
        CODEC = Codec.unit(() -> PLACER);
    }
}
