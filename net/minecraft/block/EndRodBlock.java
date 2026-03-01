package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class EndRodBlock
extends DirectionalBlock {
    protected static final VoxelShape END_ROD_VERTICAL_AABB = Block.makeCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape END_ROD_NS_AABB = Block.makeCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape END_ROD_EW_AABB = Block.makeCuboidShape(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);

    protected EndRodBlock(AbstractBlock.Properties builder) {
        super(builder);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.UP));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return (BlockState)state.with(FACING, mirrorIn.mirror(state.get(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING).getAxis()) {
            default: {
                return END_ROD_EW_AABB;
            }
            case Z: {
                return END_ROD_NS_AABB;
            }
            case Y: 
        }
        return END_ROD_VERTICAL_AABB;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getFace();
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(direction.getOpposite()));
        return blockstate.isIn(this) && blockstate.get(FACING) == direction ? (BlockState)this.getDefaultState().with(FACING, direction.getOpposite()) : (BlockState)this.getDefaultState().with(FACING, direction);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        Direction direction = stateIn.get(FACING);
        double d0 = (double)pos.getX() + 0.55 - (double)(rand.nextFloat() * 0.1f);
        double d1 = (double)pos.getY() + 0.55 - (double)(rand.nextFloat() * 0.1f);
        double d2 = (double)pos.getZ() + 0.55 - (double)(rand.nextFloat() * 0.1f);
        double d3 = 0.4f - (rand.nextFloat() + rand.nextFloat()) * 0.4f;
        if (rand.nextInt(5) == 0) {
            worldIn.addParticle(ParticleTypes.END_ROD, d0 + (double)direction.getXOffset() * d3, d1 + (double)direction.getYOffset() * d3, d2 + (double)direction.getZOffset() * d3, rand.nextGaussian() * 0.005, rand.nextGaussian() * 0.005, rand.nextGaussian() * 0.005);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
