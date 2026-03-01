package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantBlock;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractBodyPlantBlock
extends AbstractPlantBlock
implements IGrowable {
    protected AbstractBodyPlantBlock(AbstractBlock.Properties properties, Direction growthDirection, VoxelShape shape, boolean waterloggable) {
        super(properties, growthDirection, shape, waterloggable);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        Block block;
        if (facing == this.growthDirection.getOpposite() && !stateIn.isValidPosition(worldIn, currentPos)) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }
        AbstractTopPlantBlock abstracttopplantblock = this.getTopPlantBlock();
        if (facing == this.growthDirection && (block = facingState.getBlock()) != this && block != abstracttopplantblock) {
            return abstracttopplantblock.grow(worldIn);
        }
        if (this.breaksInWater) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(this.getTopPlantBlock());
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        Optional<BlockPos> optional = this.nextGrowPosition(worldIn, pos, state);
        return optional.isPresent() && this.getTopPlantBlock().canGrowIn(worldIn.getBlockState(optional.get().offset(this.growthDirection)));
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        Optional<BlockPos> optional = this.nextGrowPosition(worldIn, pos, state);
        if (optional.isPresent()) {
            BlockState blockstate = worldIn.getBlockState(optional.get());
            ((AbstractTopPlantBlock)blockstate.getBlock()).grow(worldIn, rand, optional.get(), blockstate);
        }
    }

    private Optional<BlockPos> nextGrowPosition(IBlockReader reader, BlockPos pos, BlockState state) {
        Block block;
        BlockPos blockpos = pos;
        while ((block = reader.getBlockState(blockpos = blockpos.offset(this.growthDirection)).getBlock()) == state.getBlock()) {
        }
        return block == this.getTopPlantBlock() ? Optional.of(blockpos) : Optional.empty();
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        boolean flag = super.isReplaceable(state, useContext);
        return flag && useContext.getItem().getItem() == this.getTopPlantBlock().asItem() ? false : flag;
    }

    @Override
    protected Block getBodyPlantBlock() {
        return this;
    }
}
