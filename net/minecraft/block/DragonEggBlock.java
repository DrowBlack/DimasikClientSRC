package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DragonEggBlock
extends FallingBlock {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public DragonEggBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        this.teleport(state, worldIn, pos);
        return ActionResultType.func_233537_a_(worldIn.isRemote);
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        this.teleport(state, worldIn, pos);
    }

    private void teleport(BlockState state, World world, BlockPos pos) {
        for (int i = 0; i < 1000; ++i) {
            BlockPos blockpos = pos.add(world.rand.nextInt(16) - world.rand.nextInt(16), world.rand.nextInt(8) - world.rand.nextInt(8), world.rand.nextInt(16) - world.rand.nextInt(16));
            if (!world.getBlockState(blockpos).isAir()) continue;
            if (world.isRemote) {
                for (int j = 0; j < 128; ++j) {
                    double d0 = world.rand.nextDouble();
                    float f = (world.rand.nextFloat() - 0.5f) * 0.2f;
                    float f1 = (world.rand.nextFloat() - 0.5f) * 0.2f;
                    float f2 = (world.rand.nextFloat() - 0.5f) * 0.2f;
                    double d1 = MathHelper.lerp(d0, (double)blockpos.getX(), (double)pos.getX()) + (world.rand.nextDouble() - 0.5) + 0.5;
                    double d2 = MathHelper.lerp(d0, (double)blockpos.getY(), (double)pos.getY()) + world.rand.nextDouble() - 0.5;
                    double d3 = MathHelper.lerp(d0, (double)blockpos.getZ(), (double)pos.getZ()) + (world.rand.nextDouble() - 0.5) + 0.5;
                    world.addParticle(ParticleTypes.PORTAL, d1, d2, d3, f, f1, f2);
                }
            } else {
                world.setBlockState(blockpos, state, 2);
                world.removeBlock(pos, false);
            }
            return;
        }
    }

    @Override
    protected int getFallDelay() {
        return 5;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
