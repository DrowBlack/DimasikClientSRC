package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WetSpongeBlock
extends Block {
    protected WetSpongeBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (worldIn.getDimensionType().isUltrawarm()) {
            worldIn.setBlockState(pos, Blocks.SPONGE.getDefaultState(), 3);
            worldIn.playEvent(2009, pos, 0);
            worldIn.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, (1.0f + worldIn.getRandom().nextFloat() * 0.2f) * 0.7f);
        }
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        Direction direction = Direction.getRandomDirection(rand);
        if (direction != Direction.UP) {
            BlockPos blockpos = pos.offset(direction);
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (!stateIn.isSolid() || !blockstate.isSolidSide(worldIn, blockpos, direction.getOpposite())) {
                double d0 = pos.getX();
                double d1 = pos.getY();
                double d2 = pos.getZ();
                if (direction == Direction.DOWN) {
                    d1 -= 0.05;
                    d0 += rand.nextDouble();
                    d2 += rand.nextDouble();
                } else {
                    d1 += rand.nextDouble() * 0.8;
                    if (direction.getAxis() == Direction.Axis.X) {
                        d2 += rand.nextDouble();
                        d0 = direction == Direction.EAST ? (d0 += 1.0) : (d0 += 0.05);
                    } else {
                        d0 += rand.nextDouble();
                        d2 = direction == Direction.SOUTH ? (d2 += 1.0) : (d2 += 0.05);
                    }
                }
                worldIn.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0, 0.0, 0.0);
            }
        }
    }
}
