package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class LadderBlock
extends Block
implements IWaterLoggable {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape LADDER_EAST_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    protected static final VoxelShape LADDER_WEST_AABB = Block.makeCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape LADDER_SOUTH_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape LADDER_NORTH_AABB = Block.makeCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);

    protected LadderBlock(AbstractBlock.Properties builder) {
        super(builder);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            case NORTH: {
                return LADDER_NORTH_AABB;
            }
            case SOUTH: {
                return LADDER_SOUTH_AABB;
            }
            case WEST: {
                return LADDER_WEST_AABB;
            }
        }
        return LADDER_EAST_AABB;
    }

    private boolean canAttachTo(IBlockReader blockReader, BlockPos pos, Direction direction) {
        BlockState blockstate = blockReader.getBlockState(pos);
        return blockstate.isSolidSide(blockReader, pos, direction);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        Direction direction = state.get(FACING);
        return this.canAttachTo(worldIn, pos.offset(direction.getOpposite()), direction);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (stateIn.get(WATERLOGGED).booleanValue()) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate;
        if (!context.replacingClickedOnBlock() && (blockstate = context.getWorld().getBlockState(context.getPos().offset(context.getFace().getOpposite()))).isIn(this) && blockstate.get(FACING) == context.getFace()) {
            return null;
        }
        BlockState blockstate1 = this.getDefaultState();
        World iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        for (Direction direction : context.getNearestLookingDirections()) {
            if (!direction.getAxis().isHorizontal() || !(blockstate1 = (BlockState)blockstate1.with(FACING, direction.getOpposite())).isValidPosition(iworldreader, blockpos)) continue;
            return (BlockState)blockstate1.with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) != false ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }
}
