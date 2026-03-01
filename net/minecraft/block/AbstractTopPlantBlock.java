package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractTopPlantBlock
extends AbstractPlantBlock
implements IGrowable {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_25;
    private final double growthChance;

    protected AbstractTopPlantBlock(AbstractBlock.Properties properties, Direction direction, VoxelShape shape, boolean waterloggable, double growthChance) {
        super(properties, direction, shape, waterloggable);
        this.growthChance = growthChance;
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
    }

    @Override
    public BlockState grow(IWorld world) {
        return (BlockState)this.getDefaultState().with(AGE, world.getRandom().nextInt(25));
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return state.get(AGE) < 25;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        BlockPos blockpos;
        if (state.get(AGE) < 25 && random.nextDouble() < this.growthChance && this.canGrowIn(worldIn.getBlockState(blockpos = pos.offset(this.growthDirection)))) {
            worldIn.setBlockState(blockpos, (BlockState)state.func_235896_a_(AGE));
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == this.growthDirection.getOpposite() && !stateIn.isValidPosition(worldIn, currentPos)) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }
        if (facing != this.growthDirection || !facingState.isIn(this) && !facingState.isIn(this.getBodyPlantBlock())) {
            if (this.breaksInWater) {
                worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
            }
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        return this.getBodyPlantBlock().getDefaultState();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return this.canGrowIn(worldIn.getBlockState(pos.offset(this.growthDirection)));
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        BlockPos blockpos = pos.offset(this.growthDirection);
        int i = Math.min(state.get(AGE) + 1, 25);
        int j = this.getGrowthAmount(rand);
        for (int k = 0; k < j && this.canGrowIn(worldIn.getBlockState(blockpos)); ++k) {
            worldIn.setBlockState(blockpos, (BlockState)state.with(AGE, i));
            blockpos = blockpos.offset(this.growthDirection);
            i = Math.min(i + 1, 25);
        }
    }

    protected abstract int getGrowthAmount(Random var1);

    protected abstract boolean canGrowIn(BlockState var1);

    @Override
    protected AbstractTopPlantBlock getTopPlantBlock() {
        return this;
    }
}
