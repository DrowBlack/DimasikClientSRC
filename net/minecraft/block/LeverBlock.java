package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LeverBlock
extends HorizontalFaceBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape LEVER_NORTH_AABB = Block.makeCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
    protected static final VoxelShape LEVER_SOUTH_AABB = Block.makeCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
    protected static final VoxelShape LEVER_WEST_AABB = Block.makeCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
    protected static final VoxelShape LEVER_EAST_AABB = Block.makeCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
    protected static final VoxelShape FLOOR_Z_SHAPE = Block.makeCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
    protected static final VoxelShape FLOOR_X_SHAPE = Block.makeCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
    protected static final VoxelShape CEILING_Z_SHAPE = Block.makeCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
    protected static final VoxelShape CEILING_X_SHAPE = Block.makeCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

    protected LeverBlock(AbstractBlock.Properties builder) {
        super(builder);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(POWERED, false)).with(FACE, AttachFace.WALL));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch ((AttachFace)state.get(FACE)) {
            case FLOOR: {
                switch (state.get(HORIZONTAL_FACING).getAxis()) {
                    case X: {
                        return FLOOR_X_SHAPE;
                    }
                }
                return FLOOR_Z_SHAPE;
            }
            case WALL: {
                switch (state.get(HORIZONTAL_FACING)) {
                    case EAST: {
                        return LEVER_EAST_AABB;
                    }
                    case WEST: {
                        return LEVER_WEST_AABB;
                    }
                    case SOUTH: {
                        return LEVER_SOUTH_AABB;
                    }
                }
                return LEVER_NORTH_AABB;
            }
        }
        switch (state.get(HORIZONTAL_FACING).getAxis()) {
            case X: {
                return CEILING_X_SHAPE;
            }
        }
        return CEILING_Z_SHAPE;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            BlockState blockstate1 = (BlockState)state.func_235896_a_(POWERED);
            if (blockstate1.get(POWERED).booleanValue()) {
                LeverBlock.addParticles(blockstate1, worldIn, pos, 1.0f);
            }
            return ActionResultType.SUCCESS;
        }
        BlockState blockstate = this.setPowered(state, worldIn, pos);
        float f = blockstate.get(POWERED) != false ? 0.6f : 0.5f;
        worldIn.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3f, f);
        return ActionResultType.CONSUME;
    }

    public BlockState setPowered(BlockState state, World world, BlockPos pos) {
        state = (BlockState)state.func_235896_a_(POWERED);
        world.setBlockState(pos, state, 3);
        this.updateNeighbors(state, world, pos);
        return state;
    }

    private static void addParticles(BlockState state, IWorld worldIn, BlockPos pos, float alpha) {
        Direction direction = state.get(HORIZONTAL_FACING).getOpposite();
        Direction direction1 = LeverBlock.getFacing(state).getOpposite();
        double d0 = (double)pos.getX() + 0.5 + 0.1 * (double)direction.getXOffset() + 0.2 * (double)direction1.getXOffset();
        double d1 = (double)pos.getY() + 0.5 + 0.1 * (double)direction.getYOffset() + 0.2 * (double)direction1.getYOffset();
        double d2 = (double)pos.getZ() + 0.5 + 0.1 * (double)direction.getZOffset() + 0.2 * (double)direction1.getZOffset();
        worldIn.addParticle(new RedstoneParticleData(1.0f, 0.0f, 0.0f, alpha), d0, d1, d2, 0.0, 0.0, 0.0);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(POWERED).booleanValue() && rand.nextFloat() < 0.25f) {
            LeverBlock.addParticles(stateIn, worldIn, pos, 0.5f);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.isIn(newState.getBlock())) {
            if (state.get(POWERED).booleanValue()) {
                this.updateNeighbors(state, worldIn, pos);
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
        return blockState.get(POWERED) != false && LeverBlock.getFacing(blockState) == side ? 15 : 0;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, this);
        world.notifyNeighborsOfStateChange(pos.offset(LeverBlock.getFacing(state).getOpposite()), this);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACE, HORIZONTAL_FACING, POWERED);
    }
}
