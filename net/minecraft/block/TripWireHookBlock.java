package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.TripWireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TripWireHookBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    protected static final VoxelShape HOOK_NORTH_AABB = Block.makeCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape HOOK_SOUTH_AABB = Block.makeCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
    protected static final VoxelShape HOOK_WEST_AABB = Block.makeCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape HOOK_EAST_AABB = Block.makeCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

    public TripWireHookBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(ATTACHED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            default: {
                return HOOK_EAST_AABB;
            }
            case WEST: {
                return HOOK_WEST_AABB;
            }
            case SOUTH: {
                return HOOK_SOUTH_AABB;
            }
            case NORTH: 
        }
        return HOOK_NORTH_AABB;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockpos = pos.offset(direction.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return direction.getAxis().isHorizontal() && blockstate.isSolidSide(worldIn, blockpos, direction);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction[] adirection;
        BlockState blockstate = (BlockState)((BlockState)this.getDefaultState().with(POWERED, false)).with(ATTACHED, false);
        World iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        for (Direction direction : adirection = context.getNearestLookingDirections()) {
            Direction direction1;
            if (!direction.getAxis().isHorizontal() || !(blockstate = (BlockState)blockstate.with(FACING, direction1 = direction.getOpposite())).isValidPosition(iworldreader, blockpos)) continue;
            return blockstate;
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        this.calculateState(worldIn, pos, state, false, false, -1, null);
    }

    public void calculateState(World worldIn, BlockPos pos, BlockState hookState, boolean attaching, boolean shouldNotifyNeighbours, int searchRange, @Nullable BlockState state) {
        Direction direction = hookState.get(FACING);
        boolean flag = hookState.get(ATTACHED);
        boolean flag1 = hookState.get(POWERED);
        boolean flag2 = !attaching;
        boolean flag3 = false;
        int i = 0;
        BlockState[] ablockstate = new BlockState[42];
        for (int j = 1; j < 42; ++j) {
            BlockPos blockpos = pos.offset(direction, j);
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.isIn(Blocks.TRIPWIRE_HOOK)) {
                if (blockstate.get(FACING) != direction.getOpposite()) break;
                i = j;
                break;
            }
            if (!blockstate.isIn(Blocks.TRIPWIRE) && j != searchRange) {
                ablockstate[j] = null;
                flag2 = false;
                continue;
            }
            if (j == searchRange) {
                blockstate = MoreObjects.firstNonNull(state, blockstate);
            }
            boolean flag4 = blockstate.get(TripWireBlock.DISARMED) == false;
            boolean flag5 = blockstate.get(TripWireBlock.POWERED);
            flag3 |= flag4 && flag5;
            ablockstate[j] = blockstate;
            if (j != searchRange) continue;
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 10);
            flag2 &= flag4;
        }
        BlockState blockstate1 = (BlockState)((BlockState)this.getDefaultState().with(ATTACHED, flag2)).with(POWERED, flag3 &= (flag2 &= i > 1));
        if (i > 0) {
            BlockPos blockpos1 = pos.offset(direction, i);
            Direction direction1 = direction.getOpposite();
            worldIn.setBlockState(blockpos1, (BlockState)blockstate1.with(FACING, direction1), 3);
            this.notifyNeighbors(worldIn, blockpos1, direction1);
            this.playSound(worldIn, blockpos1, flag2, flag3, flag, flag1);
        }
        this.playSound(worldIn, pos, flag2, flag3, flag, flag1);
        if (!attaching) {
            worldIn.setBlockState(pos, (BlockState)blockstate1.with(FACING, direction), 3);
            if (shouldNotifyNeighbours) {
                this.notifyNeighbors(worldIn, pos, direction);
            }
        }
        if (flag != flag2) {
            for (int k = 1; k < i; ++k) {
                BlockPos blockpos2 = pos.offset(direction, k);
                BlockState blockstate2 = ablockstate[k];
                if (blockstate2 == null) continue;
                worldIn.setBlockState(blockpos2, (BlockState)blockstate2.with(ATTACHED, flag2), 3);
                if (worldIn.getBlockState(blockpos2).isAir()) continue;
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        this.calculateState(worldIn, pos, state, false, true, -1, null);
    }

    private void playSound(World worldIn, BlockPos pos, boolean attaching, boolean activated, boolean detaching, boolean deactivating) {
        if (activated && !deactivating) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4f, 0.6f);
        } else if (!activated && deactivating) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4f, 0.5f);
        } else if (attaching && !detaching) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4f, 0.7f);
        } else if (!attaching && detaching) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4f, 1.2f / (worldIn.rand.nextFloat() * 0.2f + 0.9f));
        }
    }

    private void notifyNeighbors(World worldIn, BlockPos pos, Direction side) {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.offset(side.getOpposite()), this);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.isIn(newState.getBlock())) {
            boolean flag = state.get(ATTACHED);
            boolean flag1 = state.get(POWERED);
            if (flag || flag1) {
                this.calculateState(worldIn, pos, state, true, false, -1, null);
            }
            if (flag1) {
                worldIn.notifyNeighborsOfStateChange(pos, this);
                worldIn.notifyNeighborsOfStateChange(pos.offset(state.get(FACING).getOpposite()), this);
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        if (!blockState.get(POWERED).booleanValue()) {
            return 0;
        }
        return blockState.get(FACING) == side ? 15 : 0;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
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
        builder.add(FACING, POWERED, ATTACHED);
    }
}
