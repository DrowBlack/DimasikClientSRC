package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.WallHeight;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class WallBlock
extends Block
implements IWaterLoggable {
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final EnumProperty<WallHeight> WALL_HEIGHT_EAST = BlockStateProperties.WALL_HEIGHT_EAST;
    public static final EnumProperty<WallHeight> WALL_HEIGHT_NORTH = BlockStateProperties.WALL_HEIGHT_NORTH;
    public static final EnumProperty<WallHeight> WALL_HEIGHT_SOUTH = BlockStateProperties.WALL_HEIGHT_SOUTH;
    public static final EnumProperty<WallHeight> WALL_HEIGHT_WEST = BlockStateProperties.WALL_HEIGHT_WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Map<BlockState, VoxelShape> stateToShapeMap;
    private final Map<BlockState, VoxelShape> stateToCollisionShapeMap;
    private static final VoxelShape CENTER_POLE_SHAPE = Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape WALL_CONNECTION_NORTH_SIDE_SHAPE = Block.makeCuboidShape(7.0, 0.0, 0.0, 9.0, 16.0, 9.0);
    private static final VoxelShape WALL_CONNECTION_SOUTH_SIDE_SHAPE = Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 16.0);
    private static final VoxelShape WALL_CONNECTION_WEST_SIDE_SHAPE = Block.makeCuboidShape(0.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape WALL_CONNECTION_EAST_SIDE_SHAPE = Block.makeCuboidShape(7.0, 0.0, 7.0, 16.0, 16.0, 9.0);

    public WallBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(UP, true)).with(WALL_HEIGHT_NORTH, WallHeight.NONE)).with(WALL_HEIGHT_EAST, WallHeight.NONE)).with(WALL_HEIGHT_SOUTH, WallHeight.NONE)).with(WALL_HEIGHT_WEST, WallHeight.NONE)).with(WATERLOGGED, false));
        this.stateToShapeMap = this.makeShapes(4.0f, 3.0f, 16.0f, 0.0f, 14.0f, 16.0f);
        this.stateToCollisionShapeMap = this.makeShapes(4.0f, 3.0f, 24.0f, 0.0f, 24.0f, 24.0f);
    }

    private static VoxelShape getHeightAlteredShape(VoxelShape baseShape, WallHeight height, VoxelShape lowShape, VoxelShape tallShape) {
        if (height == WallHeight.TALL) {
            return VoxelShapes.or(baseShape, tallShape);
        }
        return height == WallHeight.LOW ? VoxelShapes.or(baseShape, lowShape) : baseShape;
    }

    private Map<BlockState, VoxelShape> makeShapes(float p_235624_1_, float p_235624_2_, float p_235624_3_, float p_235624_4_, float p_235624_5_, float p_235624_6_) {
        float f = 8.0f - p_235624_1_;
        float f1 = 8.0f + p_235624_1_;
        float f2 = 8.0f - p_235624_2_;
        float f3 = 8.0f + p_235624_2_;
        VoxelShape voxelshape = Block.makeCuboidShape(f, 0.0, f, f1, p_235624_3_, f1);
        VoxelShape voxelshape1 = Block.makeCuboidShape(f2, p_235624_4_, 0.0, f3, p_235624_5_, f3);
        VoxelShape voxelshape2 = Block.makeCuboidShape(f2, p_235624_4_, f2, f3, p_235624_5_, 16.0);
        VoxelShape voxelshape3 = Block.makeCuboidShape(0.0, p_235624_4_, f2, f3, p_235624_5_, f3);
        VoxelShape voxelshape4 = Block.makeCuboidShape(f2, p_235624_4_, f2, 16.0, p_235624_5_, f3);
        VoxelShape voxelshape5 = Block.makeCuboidShape(f2, p_235624_4_, 0.0, f3, p_235624_6_, f3);
        VoxelShape voxelshape6 = Block.makeCuboidShape(f2, p_235624_4_, f2, f3, p_235624_6_, 16.0);
        VoxelShape voxelshape7 = Block.makeCuboidShape(0.0, p_235624_4_, f2, f3, p_235624_6_, f3);
        VoxelShape voxelshape8 = Block.makeCuboidShape(f2, p_235624_4_, f2, 16.0, p_235624_6_, f3);
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (Boolean obool : UP.getAllowedValues()) {
            for (WallHeight wallheight : WALL_HEIGHT_EAST.getAllowedValues()) {
                for (WallHeight wallheight1 : WALL_HEIGHT_NORTH.getAllowedValues()) {
                    for (WallHeight wallheight2 : WALL_HEIGHT_WEST.getAllowedValues()) {
                        for (WallHeight wallheight3 : WALL_HEIGHT_SOUTH.getAllowedValues()) {
                            VoxelShape voxelshape9 = VoxelShapes.empty();
                            voxelshape9 = WallBlock.getHeightAlteredShape(voxelshape9, wallheight, voxelshape4, voxelshape8);
                            voxelshape9 = WallBlock.getHeightAlteredShape(voxelshape9, wallheight2, voxelshape3, voxelshape7);
                            voxelshape9 = WallBlock.getHeightAlteredShape(voxelshape9, wallheight1, voxelshape1, voxelshape5);
                            voxelshape9 = WallBlock.getHeightAlteredShape(voxelshape9, wallheight3, voxelshape2, voxelshape6);
                            if (obool.booleanValue()) {
                                voxelshape9 = VoxelShapes.or(voxelshape9, voxelshape);
                            }
                            BlockState blockstate = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(UP, obool)).with(WALL_HEIGHT_EAST, wallheight)).with(WALL_HEIGHT_WEST, wallheight2)).with(WALL_HEIGHT_NORTH, wallheight1)).with(WALL_HEIGHT_SOUTH, wallheight3);
                            builder.put((BlockState)blockstate.with(WATERLOGGED, false), voxelshape9);
                            builder.put((BlockState)blockstate.with(WATERLOGGED, true), voxelshape9);
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.stateToShapeMap.get(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.stateToCollisionShapeMap.get(state);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    private boolean shouldConnect(BlockState state, boolean sideSolid, Direction direction) {
        Block block = state.getBlock();
        boolean flag = block instanceof FenceGateBlock && FenceGateBlock.isParallel(state, direction);
        return state.isIn(BlockTags.WALLS) || !WallBlock.cannotAttach(block) && sideSolid || block instanceof PaneBlock || flag;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        BlockPos blockpos5 = blockpos.up();
        BlockState blockstate = iworldreader.getBlockState(blockpos1);
        BlockState blockstate1 = iworldreader.getBlockState(blockpos2);
        BlockState blockstate2 = iworldreader.getBlockState(blockpos3);
        BlockState blockstate3 = iworldreader.getBlockState(blockpos4);
        BlockState blockstate4 = iworldreader.getBlockState(blockpos5);
        boolean flag = this.shouldConnect(blockstate, blockstate.isSolidSide(iworldreader, blockpos1, Direction.SOUTH), Direction.SOUTH);
        boolean flag1 = this.shouldConnect(blockstate1, blockstate1.isSolidSide(iworldreader, blockpos2, Direction.WEST), Direction.WEST);
        boolean flag2 = this.shouldConnect(blockstate2, blockstate2.isSolidSide(iworldreader, blockpos3, Direction.NORTH), Direction.NORTH);
        boolean flag3 = this.shouldConnect(blockstate3, blockstate3.isSolidSide(iworldreader, blockpos4, Direction.EAST), Direction.EAST);
        BlockState blockstate5 = (BlockState)this.getDefaultState().with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
        return this.func_235626_a_(iworldreader, blockstate5, blockpos5, blockstate4, flag, flag1, flag2, flag3);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED).booleanValue()) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        if (facing == Direction.DOWN) {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        return facing == Direction.UP ? this.func_235625_a_(worldIn, stateIn, facingPos, facingState) : this.func_235627_a_(worldIn, currentPos, stateIn, facingPos, facingState, facing);
    }

    private static boolean hasHeightForProperty(BlockState state, Property<WallHeight> heightProperty) {
        return state.get(heightProperty) != WallHeight.NONE;
    }

    private static boolean compareShapes(VoxelShape shape1, VoxelShape shape2) {
        return !VoxelShapes.compare(shape2, shape1, IBooleanFunction.ONLY_FIRST);
    }

    private BlockState func_235625_a_(IWorldReader reader, BlockState state1, BlockPos pos, BlockState state2) {
        boolean flag = WallBlock.hasHeightForProperty(state1, WALL_HEIGHT_NORTH);
        boolean flag1 = WallBlock.hasHeightForProperty(state1, WALL_HEIGHT_EAST);
        boolean flag2 = WallBlock.hasHeightForProperty(state1, WALL_HEIGHT_SOUTH);
        boolean flag3 = WallBlock.hasHeightForProperty(state1, WALL_HEIGHT_WEST);
        return this.func_235626_a_(reader, state1, pos, state2, flag, flag1, flag2, flag3);
    }

    private BlockState func_235627_a_(IWorldReader reader, BlockPos p_235627_2_, BlockState p_235627_3_, BlockPos p_235627_4_, BlockState p_235627_5_, Direction directionIn) {
        Direction direction = directionIn.getOpposite();
        boolean flag = directionIn == Direction.NORTH ? this.shouldConnect(p_235627_5_, p_235627_5_.isSolidSide(reader, p_235627_4_, direction), direction) : WallBlock.hasHeightForProperty(p_235627_3_, WALL_HEIGHT_NORTH);
        boolean flag1 = directionIn == Direction.EAST ? this.shouldConnect(p_235627_5_, p_235627_5_.isSolidSide(reader, p_235627_4_, direction), direction) : WallBlock.hasHeightForProperty(p_235627_3_, WALL_HEIGHT_EAST);
        boolean flag2 = directionIn == Direction.SOUTH ? this.shouldConnect(p_235627_5_, p_235627_5_.isSolidSide(reader, p_235627_4_, direction), direction) : WallBlock.hasHeightForProperty(p_235627_3_, WALL_HEIGHT_SOUTH);
        boolean flag3 = directionIn == Direction.WEST ? this.shouldConnect(p_235627_5_, p_235627_5_.isSolidSide(reader, p_235627_4_, direction), direction) : WallBlock.hasHeightForProperty(p_235627_3_, WALL_HEIGHT_WEST);
        BlockPos blockpos = p_235627_2_.up();
        BlockState blockstate = reader.getBlockState(blockpos);
        return this.func_235626_a_(reader, p_235627_3_, blockpos, blockstate, flag, flag1, flag2, flag3);
    }

    private BlockState func_235626_a_(IWorldReader reader, BlockState state, BlockPos pos, BlockState collisionState, boolean connectedSouth, boolean connectedWest, boolean connectedNorth, boolean connectedEast) {
        VoxelShape voxelshape = collisionState.getCollisionShape(reader, pos).project(Direction.DOWN);
        BlockState blockstate = this.func_235630_a_(state, connectedSouth, connectedWest, connectedNorth, connectedEast, voxelshape);
        return (BlockState)blockstate.with(UP, this.func_235628_a_(blockstate, collisionState, voxelshape));
    }

    private boolean func_235628_a_(BlockState p_235628_1_, BlockState p_235628_2_, VoxelShape shape) {
        boolean flag6;
        boolean flag5;
        boolean flag;
        boolean bl = flag = p_235628_2_.getBlock() instanceof WallBlock && p_235628_2_.get(UP) != false;
        if (flag) {
            return true;
        }
        WallHeight wallheight = p_235628_1_.get(WALL_HEIGHT_NORTH);
        WallHeight wallheight1 = p_235628_1_.get(WALL_HEIGHT_SOUTH);
        WallHeight wallheight2 = p_235628_1_.get(WALL_HEIGHT_EAST);
        WallHeight wallheight3 = p_235628_1_.get(WALL_HEIGHT_WEST);
        boolean flag1 = wallheight1 == WallHeight.NONE;
        boolean flag2 = wallheight3 == WallHeight.NONE;
        boolean flag3 = wallheight2 == WallHeight.NONE;
        boolean flag4 = wallheight == WallHeight.NONE;
        boolean bl2 = flag5 = flag4 && flag1 && flag2 && flag3 || flag4 != flag1 || flag2 != flag3;
        if (flag5) {
            return true;
        }
        boolean bl3 = flag6 = wallheight == WallHeight.TALL && wallheight1 == WallHeight.TALL || wallheight2 == WallHeight.TALL && wallheight3 == WallHeight.TALL;
        if (flag6) {
            return false;
        }
        return p_235628_2_.getBlock().isIn(BlockTags.WALL_POST_OVERRIDE) || WallBlock.compareShapes(shape, CENTER_POLE_SHAPE);
    }

    private BlockState func_235630_a_(BlockState state, boolean connectedSouth, boolean connectedWest, boolean connectedNorth, boolean connectedEast, VoxelShape shape) {
        return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WALL_HEIGHT_NORTH, this.func_235633_a_(connectedSouth, shape, WALL_CONNECTION_NORTH_SIDE_SHAPE))).with(WALL_HEIGHT_EAST, this.func_235633_a_(connectedWest, shape, WALL_CONNECTION_EAST_SIDE_SHAPE))).with(WALL_HEIGHT_SOUTH, this.func_235633_a_(connectedNorth, shape, WALL_CONNECTION_SOUTH_SIDE_SHAPE))).with(WALL_HEIGHT_WEST, this.func_235633_a_(connectedEast, shape, WALL_CONNECTION_WEST_SIDE_SHAPE));
    }

    private WallHeight func_235633_a_(boolean p_235633_1_, VoxelShape p_235633_2_, VoxelShape p_235633_3_) {
        if (p_235633_1_) {
            return WallBlock.compareShapes(p_235633_2_, p_235633_3_) ? WallHeight.TALL : WallHeight.LOW;
        }
        return WallHeight.NONE;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) != false ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return state.get(WATERLOGGED) == false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(UP, WALL_HEIGHT_NORTH, WALL_HEIGHT_EAST, WALL_HEIGHT_WEST, WALL_HEIGHT_SOUTH, WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WALL_HEIGHT_NORTH, state.get(WALL_HEIGHT_SOUTH))).with(WALL_HEIGHT_EAST, state.get(WALL_HEIGHT_WEST))).with(WALL_HEIGHT_SOUTH, state.get(WALL_HEIGHT_NORTH))).with(WALL_HEIGHT_WEST, state.get(WALL_HEIGHT_EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WALL_HEIGHT_NORTH, state.get(WALL_HEIGHT_EAST))).with(WALL_HEIGHT_EAST, state.get(WALL_HEIGHT_SOUTH))).with(WALL_HEIGHT_SOUTH, state.get(WALL_HEIGHT_WEST))).with(WALL_HEIGHT_WEST, state.get(WALL_HEIGHT_NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WALL_HEIGHT_NORTH, state.get(WALL_HEIGHT_WEST))).with(WALL_HEIGHT_EAST, state.get(WALL_HEIGHT_NORTH))).with(WALL_HEIGHT_SOUTH, state.get(WALL_HEIGHT_EAST))).with(WALL_HEIGHT_WEST, state.get(WALL_HEIGHT_SOUTH));
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)state.with(WALL_HEIGHT_NORTH, state.get(WALL_HEIGHT_SOUTH))).with(WALL_HEIGHT_SOUTH, state.get(WALL_HEIGHT_NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)state.with(WALL_HEIGHT_EAST, state.get(WALL_HEIGHT_WEST))).with(WALL_HEIGHT_WEST, state.get(WALL_HEIGHT_EAST));
            }
        }
        return super.mirror(state, mirrorIn);
    }
}
