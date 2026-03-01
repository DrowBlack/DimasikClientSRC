package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SixWayBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class VineBlock
extends Block {
    public static final BooleanProperty UP = SixWayBlock.UP;
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter(facingProperty -> facingProperty.getKey() != Direction.DOWN).collect(Util.toMapCollector());
    private static final VoxelShape UP_AABB = Block.makeCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape WEST_AABB = Block.makeCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private final Map<BlockState, VoxelShape> stateToShapeMap;

    public VineBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(UP, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
        this.stateToShapeMap = ImmutableMap.copyOf(this.stateContainer.getValidStates().stream().collect(Collectors.toMap(Function.identity(), VineBlock::getShapeForState)));
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelshape = VoxelShapes.empty();
        if (state.get(UP).booleanValue()) {
            voxelshape = UP_AABB;
        }
        if (state.get(NORTH).booleanValue()) {
            voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);
        }
        if (state.get(SOUTH).booleanValue()) {
            voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);
        }
        if (state.get(EAST).booleanValue()) {
            voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);
        }
        if (state.get(WEST).booleanValue()) {
            voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);
        }
        return voxelshape;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.stateToShapeMap.get(state);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return this.getBlocksAttachedTo(this.func_196545_h(state, worldIn, pos));
    }

    private boolean getBlocksAttachedTo(BlockState state) {
        return this.countBlocksVineIsAttachedTo(state) > 0;
    }

    private int countBlocksVineIsAttachedTo(BlockState state) {
        int i = 0;
        for (BooleanProperty booleanproperty : FACING_TO_PROPERTY_MAP.values()) {
            if (!state.get(booleanproperty).booleanValue()) continue;
            ++i;
        }
        return i;
    }

    private boolean hasAttachment(IBlockReader blockReader, BlockPos pos, Direction direction) {
        if (direction == Direction.DOWN) {
            return false;
        }
        BlockPos blockpos = pos.offset(direction);
        if (VineBlock.canAttachTo(blockReader, blockpos, direction)) {
            return true;
        }
        if (direction.getAxis() == Direction.Axis.Y) {
            return false;
        }
        BooleanProperty booleanproperty = FACING_TO_PROPERTY_MAP.get(direction);
        BlockState blockstate = blockReader.getBlockState(pos.up());
        return blockstate.isIn(this) && blockstate.get(booleanproperty) != false;
    }

    public static boolean canAttachTo(IBlockReader blockReader, BlockPos worldIn, Direction neighborPos) {
        BlockState blockstate = blockReader.getBlockState(worldIn);
        return Block.doesSideFillSquare(blockstate.getCollisionShape(blockReader, worldIn), neighborPos.getOpposite());
    }

    private BlockState func_196545_h(BlockState state, IBlockReader blockReader, BlockPos pos) {
        BlockPos blockpos = pos.up();
        if (state.get(UP).booleanValue()) {
            state = (BlockState)state.with(UP, VineBlock.canAttachTo(blockReader, blockpos, Direction.DOWN));
        }
        AbstractBlock.AbstractBlockState blockstate = null;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty booleanproperty = VineBlock.getPropertyFor(direction);
            if (!state.get(booleanproperty).booleanValue()) continue;
            boolean flag = this.hasAttachment(blockReader, pos, direction);
            if (!flag) {
                if (blockstate == null) {
                    blockstate = blockReader.getBlockState(blockpos);
                }
                flag = blockstate.isIn(this) && blockstate.get(booleanproperty) != false;
            }
            state = (BlockState)state.with(booleanproperty, flag);
        }
        return state;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN) {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        BlockState blockstate = this.func_196545_h(stateIn, worldIn, currentPos);
        return !this.getBlocksAttachedTo(blockstate) ? Blocks.AIR.getDefaultState() : blockstate;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (worldIn.rand.nextInt(4) == 0) {
            Direction direction = Direction.getRandomDirection(random);
            BlockPos blockpos = pos.up();
            if (direction.getAxis().isHorizontal() && !state.get(VineBlock.getPropertyFor(direction)).booleanValue()) {
                if (this.hasVineBelow(worldIn, pos)) {
                    BlockPos blockpos4 = pos.offset(direction);
                    BlockState blockstate4 = worldIn.getBlockState(blockpos4);
                    if (blockstate4.isAir()) {
                        Direction direction3 = direction.rotateY();
                        Direction direction4 = direction.rotateYCCW();
                        boolean flag = state.get(VineBlock.getPropertyFor(direction3));
                        boolean flag1 = state.get(VineBlock.getPropertyFor(direction4));
                        BlockPos blockpos2 = blockpos4.offset(direction3);
                        BlockPos blockpos3 = blockpos4.offset(direction4);
                        if (flag && VineBlock.canAttachTo(worldIn, blockpos2, direction3)) {
                            worldIn.setBlockState(blockpos4, (BlockState)this.getDefaultState().with(VineBlock.getPropertyFor(direction3), true), 2);
                        } else if (flag1 && VineBlock.canAttachTo(worldIn, blockpos3, direction4)) {
                            worldIn.setBlockState(blockpos4, (BlockState)this.getDefaultState().with(VineBlock.getPropertyFor(direction4), true), 2);
                        } else {
                            Direction direction1 = direction.getOpposite();
                            if (flag && worldIn.isAirBlock(blockpos2) && VineBlock.canAttachTo(worldIn, pos.offset(direction3), direction1)) {
                                worldIn.setBlockState(blockpos2, (BlockState)this.getDefaultState().with(VineBlock.getPropertyFor(direction1), true), 2);
                            } else if (flag1 && worldIn.isAirBlock(blockpos3) && VineBlock.canAttachTo(worldIn, pos.offset(direction4), direction1)) {
                                worldIn.setBlockState(blockpos3, (BlockState)this.getDefaultState().with(VineBlock.getPropertyFor(direction1), true), 2);
                            } else if ((double)worldIn.rand.nextFloat() < 0.05 && VineBlock.canAttachTo(worldIn, blockpos4.up(), Direction.UP)) {
                                worldIn.setBlockState(blockpos4, (BlockState)this.getDefaultState().with(UP, true), 2);
                            }
                        }
                    } else if (VineBlock.canAttachTo(worldIn, blockpos4, direction)) {
                        worldIn.setBlockState(pos, (BlockState)state.with(VineBlock.getPropertyFor(direction), true), 2);
                    }
                }
            } else {
                BlockState blockstate2;
                BlockState blockstate1;
                BlockPos blockpos1;
                BlockState blockstate;
                if (direction == Direction.UP && pos.getY() < 255) {
                    if (this.hasAttachment(worldIn, pos, direction)) {
                        worldIn.setBlockState(pos, (BlockState)state.with(UP, true), 2);
                        return;
                    }
                    if (worldIn.isAirBlock(blockpos)) {
                        if (!this.hasVineBelow(worldIn, pos)) {
                            return;
                        }
                        BlockState blockstate3 = state;
                        for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                            if (!random.nextBoolean() && VineBlock.canAttachTo(worldIn, blockpos.offset(direction2), Direction.UP)) continue;
                            blockstate3 = (BlockState)blockstate3.with(VineBlock.getPropertyFor(direction2), false);
                        }
                        if (this.isFacingCardinal(blockstate3)) {
                            worldIn.setBlockState(blockpos, blockstate3, 2);
                        }
                        return;
                    }
                }
                if (pos.getY() > 0 && ((blockstate = worldIn.getBlockState(blockpos1 = pos.down())).isAir() || blockstate.isIn(this)) && (blockstate1 = blockstate.isAir() ? this.getDefaultState() : blockstate) != (blockstate2 = this.func_196544_a(state, blockstate1, random)) && this.isFacingCardinal(blockstate2)) {
                    worldIn.setBlockState(blockpos1, blockstate2, 2);
                }
            }
        }
    }

    private BlockState func_196544_a(BlockState state, BlockState state2, Random rand) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty booleanproperty;
            if (!rand.nextBoolean() || !state.get(booleanproperty = VineBlock.getPropertyFor(direction)).booleanValue()) continue;
            state2 = (BlockState)state2.with(booleanproperty, true);
        }
        return state2;
    }

    private boolean isFacingCardinal(BlockState state) {
        return state.get(NORTH) != false || state.get(EAST) != false || state.get(SOUTH) != false || state.get(WEST) != false;
    }

    private boolean hasVineBelow(IBlockReader blockReader, BlockPos pos) {
        int i = 4;
        Iterable<BlockPos> iterable = BlockPos.getAllInBoxMutable(pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4, pos.getX() + 4, pos.getY() + 1, pos.getZ() + 4);
        int j = 5;
        for (BlockPos blockpos : iterable) {
            if (!blockReader.getBlockState(blockpos).isIn(this) || --j > 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        BlockState blockstate = useContext.getWorld().getBlockState(useContext.getPos());
        if (blockstate.isIn(this)) {
            return this.countBlocksVineIsAttachedTo(blockstate) < FACING_TO_PROPERTY_MAP.size();
        }
        return super.isReplaceable(state, useContext);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos());
        boolean flag = blockstate.isIn(this);
        BlockState blockstate1 = flag ? blockstate : this.getDefaultState();
        for (Direction direction : context.getNearestLookingDirections()) {
            boolean flag1;
            if (direction == Direction.DOWN) continue;
            BooleanProperty booleanproperty = VineBlock.getPropertyFor(direction);
            boolean bl = flag1 = flag && blockstate.get(booleanproperty) != false;
            if (flag1 || !this.hasAttachment(context.getWorld(), context.getPos(), direction)) continue;
            return (BlockState)blockstate1.with(booleanproperty, true);
        }
        return flag ? blockstate1 : null;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH, EAST, SOUTH, WEST);
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

    public static BooleanProperty getPropertyFor(Direction side) {
        return FACING_TO_PROPERTY_MAP.get(side);
    }
}
