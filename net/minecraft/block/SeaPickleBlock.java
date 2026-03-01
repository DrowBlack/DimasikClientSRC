package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
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

public class SeaPickleBlock
extends BushBlock
implements IGrowable,
IWaterLoggable {
    public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES_1_4;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape ONE_SHAPE = Block.makeCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
    protected static final VoxelShape TWO_SHAPE = Block.makeCuboidShape(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
    protected static final VoxelShape THREE_SHAPE = Block.makeCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    protected static final VoxelShape FOUR_SHAPE = Block.makeCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

    protected SeaPickleBlock(AbstractBlock.Properties propertiesfsp) {
        super(propertiesfsp);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(PICKLES, 1)).with(WATERLOGGED, true));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos());
        if (blockstate.isIn(this)) {
            return (BlockState)blockstate.with(PICKLES, Math.min(4, blockstate.get(PICKLES) + 1));
        }
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        boolean flag = fluidstate.getFluid() == Fluids.WATER;
        return (BlockState)super.getStateForPlacement(context).with(WATERLOGGED, flag);
    }

    public static boolean isInBadEnvironment(BlockState state) {
        return state.get(WATERLOGGED) == false;
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return !state.getCollisionShape(worldIn, pos).project(Direction.UP).isEmpty() || state.isSolidSide(worldIn, pos, Direction.UP);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return this.isValidGround(worldIn.getBlockState(blockpos), worldIn, blockpos);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.isValidPosition(worldIn, currentPos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (stateIn.get(WATERLOGGED).booleanValue()) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        return useContext.getItem().getItem() == this.asItem() && state.get(PICKLES) < 4 ? true : super.isReplaceable(state, useContext);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(PICKLES)) {
            default: {
                return ONE_SHAPE;
            }
            case 2: {
                return TWO_SHAPE;
            }
            case 3: {
                return THREE_SHAPE;
            }
            case 4: 
        }
        return FOUR_SHAPE;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) != false ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PICKLES, WATERLOGGED);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        if (!SeaPickleBlock.isInBadEnvironment(state) && worldIn.getBlockState(pos.down()).isIn(BlockTags.CORAL_BLOCKS)) {
            int i = 5;
            int j = 1;
            int k = 2;
            int l = 0;
            int i1 = pos.getX() - 2;
            int j1 = 0;
            for (int k1 = 0; k1 < 5; ++k1) {
                for (int l1 = 0; l1 < j; ++l1) {
                    int i2 = 2 + pos.getY() - 1;
                    for (int j2 = i2 - 2; j2 < i2; ++j2) {
                        BlockState blockstate;
                        BlockPos blockpos = new BlockPos(i1 + k1, j2, pos.getZ() - j1 + l1);
                        if (blockpos == pos || rand.nextInt(6) != 0 || !worldIn.getBlockState(blockpos).isIn(Blocks.WATER) || !(blockstate = worldIn.getBlockState(blockpos.down())).isIn(BlockTags.CORAL_BLOCKS)) continue;
                        worldIn.setBlockState(blockpos, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(PICKLES, rand.nextInt(4) + 1), 3);
                    }
                }
                if (l < 2) {
                    j += 2;
                    ++j1;
                } else {
                    j -= 2;
                    --j1;
                }
                ++l;
            }
            worldIn.setBlockState(pos, (BlockState)state.with(PICKLES, 4), 2);
        }
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
