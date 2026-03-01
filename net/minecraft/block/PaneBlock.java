package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FourWayBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PaneBlock
extends FourWayBlock {
    protected PaneBlock(AbstractBlock.Properties builder) {
        super(1.0f, 1.0f, 16.0f, 16.0f, 16.0f, builder);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.south();
        BlockPos blockpos3 = blockpos.west();
        BlockPos blockpos4 = blockpos.east();
        BlockState blockstate = iblockreader.getBlockState(blockpos1);
        BlockState blockstate1 = iblockreader.getBlockState(blockpos2);
        BlockState blockstate2 = iblockreader.getBlockState(blockpos3);
        BlockState blockstate3 = iblockreader.getBlockState(blockpos4);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(NORTH, this.canAttachTo(blockstate, blockstate.isSolidSide(iblockreader, blockpos1, Direction.SOUTH)))).with(SOUTH, this.canAttachTo(blockstate1, blockstate1.isSolidSide(iblockreader, blockpos2, Direction.NORTH)))).with(WEST, this.canAttachTo(blockstate2, blockstate2.isSolidSide(iblockreader, blockpos3, Direction.EAST)))).with(EAST, this.canAttachTo(blockstate3, blockstate3.isSolidSide(iblockreader, blockpos4, Direction.WEST)))).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED).booleanValue()) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return facing.getAxis().isHorizontal() ? (BlockState)stateIn.with((Property)FACING_TO_PROPERTY_MAP.get(facing), this.canAttachTo(facingState, facingState.isSolidSide(worldIn, facingPos, facing.getOpposite()))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (adjacentBlockState.isIn(this)) {
            if (!side.getAxis().isHorizontal()) {
                return true;
            }
            if (((Boolean)state.get((Property)FACING_TO_PROPERTY_MAP.get(side))).booleanValue() && ((Boolean)adjacentBlockState.get((Property)FACING_TO_PROPERTY_MAP.get(side.getOpposite()))).booleanValue()) {
                return true;
            }
        }
        return super.isSideInvisible(state, adjacentBlockState, side);
    }

    public final boolean canAttachTo(BlockState state, boolean solidSide) {
        Block block = state.getBlock();
        return !PaneBlock.cannotAttach(block) && solidSide || block instanceof PaneBlock || block.isIn(BlockTags.WALLS);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}
