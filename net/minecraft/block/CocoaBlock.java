package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CocoaBlock
extends HorizontalBlock
implements IGrowable {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_2;
    protected static final VoxelShape[] COCOA_EAST_AABB = new VoxelShape[]{Block.makeCuboidShape(11.0, 7.0, 6.0, 15.0, 12.0, 10.0), Block.makeCuboidShape(9.0, 5.0, 5.0, 15.0, 12.0, 11.0), Block.makeCuboidShape(7.0, 3.0, 4.0, 15.0, 12.0, 12.0)};
    protected static final VoxelShape[] COCOA_WEST_AABB = new VoxelShape[]{Block.makeCuboidShape(1.0, 7.0, 6.0, 5.0, 12.0, 10.0), Block.makeCuboidShape(1.0, 5.0, 5.0, 7.0, 12.0, 11.0), Block.makeCuboidShape(1.0, 3.0, 4.0, 9.0, 12.0, 12.0)};
    protected static final VoxelShape[] COCOA_NORTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0, 7.0, 1.0, 10.0, 12.0, 5.0), Block.makeCuboidShape(5.0, 5.0, 1.0, 11.0, 12.0, 7.0), Block.makeCuboidShape(4.0, 3.0, 1.0, 12.0, 12.0, 9.0)};
    protected static final VoxelShape[] COCOA_SOUTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0, 7.0, 11.0, 10.0, 12.0, 15.0), Block.makeCuboidShape(5.0, 5.0, 9.0, 11.0, 12.0, 15.0), Block.makeCuboidShape(4.0, 3.0, 7.0, 12.0, 12.0, 15.0)};

    public CocoaBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(AGE, 0));
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return state.get(AGE) < 2;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        int i;
        if (worldIn.rand.nextInt(5) == 0 && (i = state.get(AGE).intValue()) < 2) {
            worldIn.setBlockState(pos, (BlockState)state.with(AGE, i + 1), 2);
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        Block block = worldIn.getBlockState(pos.offset(state.get(HORIZONTAL_FACING))).getBlock();
        return block.isIn(BlockTags.JUNGLE_LOGS);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        int i = state.get(AGE);
        switch (state.get(HORIZONTAL_FACING)) {
            case SOUTH: {
                return COCOA_SOUTH_AABB[i];
            }
            default: {
                return COCOA_NORTH_AABB[i];
            }
            case WEST: {
                return COCOA_WEST_AABB[i];
            }
            case EAST: 
        }
        return COCOA_EAST_AABB[i];
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.getDefaultState();
        World iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        for (Direction direction : context.getNearestLookingDirections()) {
            if (!direction.getAxis().isHorizontal() || !(blockstate = (BlockState)blockstate.with(HORIZONTAL_FACING, direction)).isValidPosition(iworldreader, blockpos)) continue;
            return blockstate;
        }
        return null;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing == stateIn.get(HORIZONTAL_FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) < 2;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        worldIn.setBlockState(pos, (BlockState)state.with(AGE, state.get(AGE) + 1), 2);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, AGE);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
