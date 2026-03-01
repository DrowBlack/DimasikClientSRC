package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractPressurePlateBlock
extends Block {
    protected static final VoxelShape PRESSED_AABB = Block.makeCuboidShape(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
    protected static final VoxelShape UNPRESSED_AABB = Block.makeCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
    protected static final AxisAlignedBB PRESSURE_AABB = new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

    protected AbstractPressurePlateBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.getRedstoneStrength(state) > 0 ? PRESSED_AABB : UNPRESSED_AABB;
    }

    protected int getPoweredDuration() {
        return 20;
    }

    @Override
    public boolean canSpawnInBlock() {
        return true;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return AbstractPressurePlateBlock.hasSolidSideOnTop(worldIn, blockpos) || AbstractPressurePlateBlock.hasEnoughSolidSide(worldIn, blockpos, Direction.UP);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        int i = this.getRedstoneStrength(state);
        if (i > 0) {
            this.updateState(worldIn, pos, state, i);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        int i;
        if (!worldIn.isRemote && (i = this.getRedstoneStrength(state)) == 0) {
            this.updateState(worldIn, pos, state, i);
        }
    }

    protected void updateState(World worldIn, BlockPos pos, BlockState state, int oldRedstoneStrength) {
        boolean flag1;
        int i = this.computeRedstoneStrength(worldIn, pos);
        boolean flag = oldRedstoneStrength > 0;
        boolean bl = flag1 = i > 0;
        if (oldRedstoneStrength != i) {
            BlockState blockstate = this.setRedstoneStrength(state, i);
            worldIn.setBlockState(pos, blockstate, 2);
            this.updateNeighbors(worldIn, pos);
            worldIn.markBlockRangeForRenderUpdate(pos, state, blockstate);
        }
        if (!flag1 && flag) {
            this.playClickOffSound(worldIn, pos);
        } else if (flag1 && !flag) {
            this.playClickOnSound(worldIn, pos);
        }
        if (flag1) {
            worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.getPoweredDuration());
        }
    }

    protected abstract void playClickOnSound(IWorld var1, BlockPos var2);

    protected abstract void playClickOffSound(IWorld var1, BlockPos var2);

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.isIn(newState.getBlock())) {
            if (this.getRedstoneStrength(state) > 0) {
                this.updateNeighbors(worldIn, pos);
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    protected void updateNeighbors(World worldIn, BlockPos pos) {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.down(), this);
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return this.getRedstoneStrength(blockState);
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return side == Direction.UP ? this.getRedstoneStrength(blockState) : 0;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    protected abstract int computeRedstoneStrength(World var1, BlockPos var2);

    protected abstract int getRedstoneStrength(BlockState var1);

    protected abstract BlockState setRedstoneStrength(BlockState var1, int var2);
}
