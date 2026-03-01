package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CryingObsidianBlock
extends Block {
    public CryingObsidianBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        Direction direction;
        if (rand.nextInt(5) == 0 && (direction = Direction.getRandomDirection(rand)) != Direction.UP) {
            BlockPos blockpos = pos.offset(direction);
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (!stateIn.isSolid() || !blockstate.isSolidSide(worldIn, blockpos, direction.getOpposite())) {
                double d0 = direction.getXOffset() == 0 ? rand.nextDouble() : 0.5 + (double)direction.getXOffset() * 0.6;
                double d1 = direction.getYOffset() == 0 ? rand.nextDouble() : 0.5 + (double)direction.getYOffset() * 0.6;
                double d2 = direction.getZOffset() == 0 ? rand.nextDouble() : 0.5 + (double)direction.getZOffset() * 0.6;
                worldIn.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, 0.0, 0.0, 0.0);
            }
        }
    }
}
