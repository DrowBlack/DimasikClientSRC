package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TrapDoorBlock
extends HorizontalBlock
implements IWaterLoggable {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape EAST_OPEN_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    protected static final VoxelShape WEST_OPEN_AABB = Block.makeCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_OPEN_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_OPEN_AABB = Block.makeCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape BOTTOM_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape TOP_AABB = Block.makeCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

    protected TrapDoorBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(OPEN, false)).with(HALF, Half.BOTTOM)).with(POWERED, false)).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (!state.get(OPEN).booleanValue()) {
            return state.get(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
        }
        switch (state.get(HORIZONTAL_FACING)) {
            default: {
                return NORTH_OPEN_AABB;
            }
            case SOUTH: {
                return SOUTH_OPEN_AABB;
            }
            case WEST: {
                return WEST_OPEN_AABB;
            }
            case EAST: 
        }
        return EAST_OPEN_AABB;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        switch (type) {
            case LAND: {
                return state.get(OPEN);
            }
            case WATER: {
                return state.get(WATERLOGGED);
            }
            case AIR: {
                return state.get(OPEN);
            }
        }
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (this.material == Material.IRON) {
            return ActionResultType.PASS;
        }
        state = (BlockState)state.func_235896_a_(OPEN);
        worldIn.setBlockState(pos, state, 2);
        if (state.get(WATERLOGGED).booleanValue()) {
            worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        this.playSound(player, worldIn, pos, state.get(OPEN));
        return ActionResultType.func_233537_a_(worldIn.isRemote);
    }

    protected void playSound(@Nullable PlayerEntity player, World worldIn, BlockPos pos, boolean isOpened) {
        if (isOpened) {
            int i = this.material == Material.IRON ? 1037 : 1007;
            worldIn.playEvent(player, i, pos, 0);
        } else {
            int j = this.material == Material.IRON ? 1036 : 1013;
            worldIn.playEvent(player, j, pos, 0);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean flag;
        if (!worldIn.isRemote && (flag = worldIn.isBlockPowered(pos)) != state.get(POWERED)) {
            if (state.get(OPEN) != flag) {
                state = (BlockState)state.with(OPEN, flag);
                this.playSound(null, worldIn, pos, flag);
            }
            worldIn.setBlockState(pos, (BlockState)state.with(POWERED, flag), 2);
            if (state.get(WATERLOGGED).booleanValue()) {
                worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.getDefaultState();
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        Direction direction = context.getFace();
        blockstate = !context.replacingClickedOnBlock() && direction.getAxis().isHorizontal() ? (BlockState)((BlockState)blockstate.with(HORIZONTAL_FACING, direction)).with(HALF, context.getHitVec().y - (double)context.getPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM) : (BlockState)((BlockState)blockstate.with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite())).with(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
        if (context.getWorld().isBlockPowered(context.getPos())) {
            blockstate = (BlockState)((BlockState)blockstate.with(OPEN, true)).with(POWERED, true);
        }
        return (BlockState)blockstate.with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, OPEN, HALF, POWERED, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) != false ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED).booleanValue()) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}
