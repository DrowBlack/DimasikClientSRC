package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ScaffoldingBlock
extends Block
implements IWaterLoggable {
    private static final VoxelShape TOP_SLAB_SHAPE;
    private static final VoxelShape FULL_SHAPE;
    private static final VoxelShape BOTTOM_SLAB_SHAPE;
    private static final VoxelShape field_220124_g;
    public static final IntegerProperty DISTANCE;
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty BOTTOM;

    protected ScaffoldingBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(DISTANCE, 7)).with(WATERLOGGED, false)).with(BOTTOM, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, WATERLOGGED, BOTTOM);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (!context.hasItem(state.getBlock().asItem())) {
            return state.get(BOTTOM) != false ? FULL_SHAPE : TOP_SLAB_SHAPE;
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        return useContext.getItem().getItem() == this.asItem();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        int i = ScaffoldingBlock.getDistance(world, blockpos);
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, world.getFluidState(blockpos).getFluid() == Fluids.WATER)).with(DISTANCE, i)).with(BOTTOM, this.hasScaffoldingBelow(world, blockpos, i));
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!worldIn.isRemote) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED).booleanValue()) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        if (!worldIn.isRemote()) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }
        return stateIn;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        int i = ScaffoldingBlock.getDistance(worldIn, pos);
        BlockState blockstate = (BlockState)((BlockState)state.with(DISTANCE, i)).with(BOTTOM, this.hasScaffoldingBelow(worldIn, pos, i));
        if (blockstate.get(DISTANCE) == 7) {
            if (state.get(DISTANCE) == 7) {
                worldIn.addEntity(new FallingBlockEntity(worldIn, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, (BlockState)blockstate.with(WATERLOGGED, false)));
            } else {
                worldIn.destroyBlock(pos, true);
            }
        } else if (state != blockstate) {
            worldIn.setBlockState(pos, blockstate, 3);
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return ScaffoldingBlock.getDistance(worldIn, pos) < 7;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (context.func_216378_a(VoxelShapes.fullCube(), pos, true) && !context.getPosY()) {
            return TOP_SLAB_SHAPE;
        }
        return state.get(DISTANCE) != 0 && state.get(BOTTOM) != false && context.func_216378_a(field_220124_g, pos, true) ? BOTTOM_SLAB_SHAPE : VoxelShapes.empty();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) != false ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    private boolean hasScaffoldingBelow(IBlockReader blockReader, BlockPos pos, int distance) {
        return distance > 0 && !blockReader.getBlockState(pos.down()).isIn(this);
    }

    public static int getDistance(IBlockReader blockReader, BlockPos pos) {
        Direction direction;
        BlockState blockstate1;
        BlockPos.Mutable blockpos$mutable = pos.toMutable().move(Direction.DOWN);
        BlockState blockstate = blockReader.getBlockState(blockpos$mutable);
        int i = 7;
        if (blockstate.isIn(Blocks.SCAFFOLDING)) {
            i = blockstate.get(DISTANCE);
        } else if (blockstate.isSolidSide(blockReader, blockpos$mutable, Direction.UP)) {
            return 0;
        }
        Iterator<Direction> iterator = Direction.Plane.HORIZONTAL.iterator();
        while (iterator.hasNext() && (!(blockstate1 = blockReader.getBlockState(blockpos$mutable.setAndMove(pos, direction = iterator.next()))).isIn(Blocks.SCAFFOLDING) || (i = Math.min(i, blockstate1.get(DISTANCE) + 1)) != 1)) {
        }
        return i;
    }

    static {
        BOTTOM_SLAB_SHAPE = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        field_220124_g = VoxelShapes.fullCube().withOffset(0.0, -1.0, 0.0);
        DISTANCE = BlockStateProperties.DISTANCE_0_7;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        BOTTOM = BlockStateProperties.BOTTOM;
        VoxelShape voxelshape = Block.makeCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape voxelshape1 = Block.makeCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
        VoxelShape voxelshape2 = Block.makeCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
        VoxelShape voxelshape3 = Block.makeCuboidShape(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
        VoxelShape voxelshape4 = Block.makeCuboidShape(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
        TOP_SLAB_SHAPE = VoxelShapes.or(voxelshape, voxelshape1, voxelshape2, voxelshape3, voxelshape4);
        VoxelShape voxelshape5 = Block.makeCuboidShape(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
        VoxelShape voxelshape6 = Block.makeCuboidShape(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        VoxelShape voxelshape7 = Block.makeCuboidShape(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
        VoxelShape voxelshape8 = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
        FULL_SHAPE = VoxelShapes.or(BOTTOM_SLAB_SHAPE, TOP_SLAB_SHAPE, voxelshape6, voxelshape5, voxelshape8, voxelshape7);
    }
}
