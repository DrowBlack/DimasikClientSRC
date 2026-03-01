package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FourWayBlock;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.TripWireHookBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TripWireBlock
extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    public static final BooleanProperty DISARMED = BlockStateProperties.DISARMED;
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = FourWayBlock.FACING_TO_PROPERTY_MAP;
    protected static final VoxelShape AABB = Block.makeCuboidShape(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
    protected static final VoxelShape TRIP_WRITE_ATTACHED_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final TripWireHookBlock hook;

    public TripWireBlock(TripWireHookBlock hook, AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWERED, false)).with(ATTACHED, false)).with(DISARMED, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
        this.hook = hook;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.get(ATTACHED) != false ? AABB : TRIP_WRITE_ATTACHED_AABB;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(NORTH, this.shouldConnectTo(iblockreader.getBlockState(blockpos.north()), Direction.NORTH))).with(EAST, this.shouldConnectTo(iblockreader.getBlockState(blockpos.east()), Direction.EAST))).with(SOUTH, this.shouldConnectTo(iblockreader.getBlockState(blockpos.south()), Direction.SOUTH))).with(WEST, this.shouldConnectTo(iblockreader.getBlockState(blockpos.west()), Direction.WEST));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing.getAxis().isHorizontal() ? (BlockState)stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), this.shouldConnectTo(facingState, facing)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.isIn(state.getBlock())) {
            this.notifyHook(worldIn, pos, state);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.isIn(newState.getBlock())) {
            this.notifyHook(worldIn, pos, (BlockState)state.with(POWERED, true));
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isRemote && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == Items.SHEARS) {
            worldIn.setBlockState(pos, (BlockState)state.with(DISARMED, true), 4);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    private void notifyHook(World worldIn, BlockPos pos, BlockState state) {
        block0: for (Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
            for (int i = 1; i < 42; ++i) {
                BlockPos blockpos = pos.offset(direction, i);
                BlockState blockstate = worldIn.getBlockState(blockpos);
                if (blockstate.isIn(this.hook)) {
                    if (blockstate.get(TripWireHookBlock.FACING) != direction.getOpposite()) continue block0;
                    this.hook.calculateState(worldIn, blockpos, blockstate, false, true, i, state);
                    continue block0;
                }
                if (!blockstate.isIn(this)) continue block0;
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote && !state.get(POWERED).booleanValue()) {
            this.updateState(worldIn, pos);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (worldIn.getBlockState(pos).get(POWERED).booleanValue()) {
            this.updateState(worldIn, pos);
        }
    }

    private void updateState(World worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        boolean flag = blockstate.get(POWERED);
        boolean flag1 = false;
        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(null, blockstate.getShape(worldIn, pos).getBoundingBox().offset(pos));
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (entity.doesEntityNotTriggerPressurePlate()) continue;
                flag1 = true;
                break;
            }
        }
        if (flag1 != flag) {
            blockstate = (BlockState)blockstate.with(POWERED, flag1);
            worldIn.setBlockState(pos, blockstate, 3);
            this.notifyHook(worldIn, pos, blockstate);
        }
        if (flag1) {
            worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, 10);
        }
    }

    public boolean shouldConnectTo(BlockState state, Direction direction) {
        Block block = state.getBlock();
        if (block == this.hook) {
            return state.get(TripWireHookBlock.FACING) == direction.getOpposite();
        }
        return block == this;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, state.get(SOUTH))).with(EAST, state.get(WEST))).with(SOUTH, state.get(NORTH))).with(WEST, state.get(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, state.get(EAST))).with(EAST, state.get(SOUTH))).with(SOUTH, state.get(WEST))).with(WEST, state.get(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, state.get(WEST))).with(EAST, state.get(NORTH))).with(SOUTH, state.get(EAST))).with(WEST, state.get(SOUTH));
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)state.with(NORTH, state.get(SOUTH))).with(SOUTH, state.get(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)state.with(EAST, state.get(WEST))).with(WEST, state.get(EAST));
            }
        }
        return super.mirror(state, mirrorIn);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
    }
}
