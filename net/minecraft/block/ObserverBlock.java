package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ObserverBlock
extends DirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ObserverBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.SOUTH)).with(POWERED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
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
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (state.get(POWERED).booleanValue()) {
            worldIn.setBlockState(pos, (BlockState)state.with(POWERED, false), 2);
        } else {
            worldIn.setBlockState(pos, (BlockState)state.with(POWERED, true), 2);
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
        }
        this.updateNeighborsInFront(worldIn, pos, state);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(FACING) == facing && !stateIn.get(POWERED).booleanValue()) {
            this.startSignal(worldIn, currentPos);
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    private void startSignal(IWorld worldIn, BlockPos pos) {
        if (!worldIn.isRemote() && !worldIn.getPendingBlockTicks().isTickScheduled(pos, this)) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
        }
    }

    protected void updateNeighborsInFront(World worldIn, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockpos = pos.offset(direction.getOpposite());
        worldIn.neighborChanged(blockpos, this, pos);
        worldIn.notifyNeighborsOfStateExcept(blockpos, this, direction);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(POWERED) != false && blockState.get(FACING) == side ? 15 : 0;
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!state.isIn(oldState.getBlock()) && !worldIn.isRemote() && state.get(POWERED).booleanValue() && !worldIn.getPendingBlockTicks().isTickScheduled(pos, this)) {
            BlockState blockstate = (BlockState)state.with(POWERED, false);
            worldIn.setBlockState(pos, blockstate, 18);
            this.updateNeighborsInFront(worldIn, pos, blockstate);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock()) && !worldIn.isRemote && state.get(POWERED).booleanValue() && worldIn.getPendingBlockTicks().isTickScheduled(pos, this)) {
            this.updateNeighborsInFront(worldIn, pos, (BlockState)state.with(POWERED, false));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return (BlockState)this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite().getOpposite());
    }
}
