package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerTickList;
import net.minecraft.world.server.ServerWorld;

public abstract class RedstoneDiodeBlock
extends HorizontalBlock {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected RedstoneDiodeBlock(AbstractBlock.Properties builder) {
        super(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return RedstoneDiodeBlock.hasSolidSideOnTop(worldIn, pos.down());
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!this.isLocked(worldIn, pos, state)) {
            boolean flag = state.get(POWERED);
            boolean flag1 = this.shouldBePowered(worldIn, pos, state);
            if (flag && !flag1) {
                worldIn.setBlockState(pos, (BlockState)state.with(POWERED, false), 2);
            } else if (!flag) {
                worldIn.setBlockState(pos, (BlockState)state.with(POWERED, true), 2);
                if (!flag1) {
                    ((ServerTickList)worldIn.getPendingBlockTicks()).scheduleTick(pos, this, this.getDelay(state), TickPriority.VERY_HIGH);
                }
            }
        }
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        if (!blockState.get(POWERED).booleanValue()) {
            return 0;
        }
        return blockState.get(HORIZONTAL_FACING) == side ? this.getActiveSignal(blockAccess, pos, blockState) : 0;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (state.isValidPosition(worldIn, pos)) {
            this.updateState(worldIn, pos, state);
        } else {
            TileEntity tileentity = this.isTileEntityProvider() ? worldIn.getTileEntity(pos) : null;
            RedstoneDiodeBlock.spawnDrops(state, worldIn, pos, tileentity);
            worldIn.removeBlock(pos, false);
            for (Direction direction : Direction.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
        }
    }

    protected void updateState(World worldIn, BlockPos pos, BlockState state) {
        boolean flag1;
        boolean flag;
        if (!this.isLocked(worldIn, pos, state) && (flag = state.get(POWERED).booleanValue()) != (flag1 = this.shouldBePowered(worldIn, pos, state)) && !worldIn.getPendingBlockTicks().isTickPending(pos, this)) {
            TickPriority tickpriority = TickPriority.HIGH;
            if (this.isFacingTowardsRepeater(worldIn, pos, state)) {
                tickpriority = TickPriority.EXTREMELY_HIGH;
            } else if (flag) {
                tickpriority = TickPriority.VERY_HIGH;
            }
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.getDelay(state), tickpriority);
        }
    }

    public boolean isLocked(IWorldReader worldIn, BlockPos pos, BlockState state) {
        return false;
    }

    protected boolean shouldBePowered(World worldIn, BlockPos pos, BlockState state) {
        return this.calculateInputStrength(worldIn, pos, state) > 0;
    }

    protected int calculateInputStrength(World worldIn, BlockPos pos, BlockState state) {
        Direction direction = state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(direction);
        int i = worldIn.getRedstonePower(blockpos, direction);
        if (i >= 15) {
            return i;
        }
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return Math.max(i, blockstate.isIn(Blocks.REDSTONE_WIRE) ? blockstate.get(RedstoneWireBlock.POWER) : 0);
    }

    protected int getPowerOnSides(IWorldReader worldIn, BlockPos pos, BlockState state) {
        Direction direction = state.get(HORIZONTAL_FACING);
        Direction direction1 = direction.rotateY();
        Direction direction2 = direction.rotateYCCW();
        return Math.max(this.getPowerOnSide(worldIn, pos.offset(direction1), direction1), this.getPowerOnSide(worldIn, pos.offset(direction2), direction2));
    }

    protected int getPowerOnSide(IWorldReader worldIn, BlockPos pos, Direction side) {
        BlockState blockstate = worldIn.getBlockState(pos);
        if (this.isAlternateInput(blockstate)) {
            if (blockstate.isIn(Blocks.REDSTONE_BLOCK)) {
                return 15;
            }
            return blockstate.isIn(Blocks.REDSTONE_WIRE) ? blockstate.get(RedstoneWireBlock.POWER).intValue() : worldIn.getStrongPower(pos, side);
        }
        return 0;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return (BlockState)this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (this.shouldBePowered(worldIn, pos, state)) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.notifyNeighbors(worldIn, pos, state);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.isIn(newState.getBlock())) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
            this.notifyNeighbors(worldIn, pos, state);
        }
    }

    protected void notifyNeighbors(World worldIn, BlockPos pos, BlockState state) {
        Direction direction = state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(direction.getOpposite());
        worldIn.neighborChanged(blockpos, this, pos);
        worldIn.notifyNeighborsOfStateExcept(blockpos, this, direction);
    }

    protected boolean isAlternateInput(BlockState state) {
        return state.canProvidePower();
    }

    protected int getActiveSignal(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return 15;
    }

    public static boolean isDiode(BlockState state) {
        return state.getBlock() instanceof RedstoneDiodeBlock;
    }

    public boolean isFacingTowardsRepeater(IBlockReader worldIn, BlockPos pos, BlockState state) {
        Direction direction = state.get(HORIZONTAL_FACING).getOpposite();
        BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
        return RedstoneDiodeBlock.isDiode(blockstate) && blockstate.get(HORIZONTAL_FACING) != direction;
    }

    protected abstract int getDelay(BlockState var1);
}
