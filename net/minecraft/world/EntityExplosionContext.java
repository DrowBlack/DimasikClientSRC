package net.minecraft.world;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.IBlockReader;

public class EntityExplosionContext
extends ExplosionContext {
    private final Entity entity;

    public EntityExplosionContext(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Optional<Float> getExplosionResistance(Explosion explosion, IBlockReader reader, BlockPos pos, BlockState state, FluidState fluid) {
        return super.getExplosionResistance(explosion, reader, pos, state, fluid).map(explosionPower -> Float.valueOf(this.entity.getExplosionResistance(explosion, reader, pos, state, fluid, explosionPower.floatValue())));
    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosion, IBlockReader reader, BlockPos pos, BlockState state, float power) {
        return this.entity.canExplosionDestroyBlock(explosion, reader, pos, state, power);
    }
}
