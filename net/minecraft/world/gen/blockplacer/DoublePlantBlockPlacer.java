package net.minecraft.world.gen.blockplacer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

public class DoublePlantBlockPlacer
extends BlockPlacer {
    public static final Codec<DoublePlantBlockPlacer> CODEC;
    public static final DoublePlantBlockPlacer PLACER;

    @Override
    protected BlockPlacerType<?> getBlockPlacerType() {
        return BlockPlacerType.DOUBLE_PLANT;
    }

    @Override
    public void place(IWorld world, BlockPos pos, BlockState state, Random random) {
        ((DoublePlantBlock)state.getBlock()).placeAt(world, pos, 2);
    }

    static {
        PLACER = new DoublePlantBlockPlacer();
        CODEC = Codec.unit(() -> PLACER);
    }
}
