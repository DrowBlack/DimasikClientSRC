package net.minecraft.world;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;

public class ExplosionContext {
    public Optional<Float> getExplosionResistance(Explosion explosion, IBlockReader reader, BlockPos pos, BlockState state, FluidState fluid) {
        return state.isAir() && fluid.isEmpty() ? Optional.empty() : Optional.of(Float.valueOf(Math.max(state.getBlock().getExplosionResistance(), fluid.getExplosionResistance())));
    }

    public boolean canExplosionDestroyBlock(Explosion explosion, IBlockReader reader, BlockPos pos, BlockState state, float power) {
        return true;
    }
}
