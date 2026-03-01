package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.StemGrownBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class StemBlock
extends BushBlock
implements IGrowable {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0), Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 4.0, 9.0), Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 6.0, 9.0), Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0), Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0), Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 12.0, 9.0), Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0), Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)};
    private final StemGrownBlock crop;

    protected StemBlock(StemGrownBlock crop, AbstractBlock.Properties properties) {
        super(properties);
        this.crop = crop;
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(AGE)];
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.isIn(Blocks.FARMLAND);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        float f;
        if (worldIn.getLightSubtracted(pos, 0) >= 9 && random.nextInt((int)(25.0f / (f = CropsBlock.getGrowthChance(this, worldIn, pos))) + 1) == 0) {
            int i = state.get(AGE);
            if (i < 7) {
                state = (BlockState)state.with(AGE, i + 1);
                worldIn.setBlockState(pos, state, 2);
            } else {
                Direction direction = Direction.Plane.HORIZONTAL.random(random);
                BlockPos blockpos = pos.offset(direction);
                BlockState blockstate = worldIn.getBlockState(blockpos.down());
                if (worldIn.getBlockState(blockpos).isAir() && (blockstate.isIn(Blocks.FARMLAND) || blockstate.isIn(Blocks.DIRT) || blockstate.isIn(Blocks.COARSE_DIRT) || blockstate.isIn(Blocks.PODZOL) || blockstate.isIn(Blocks.GRASS_BLOCK))) {
                    worldIn.setBlockState(blockpos, this.crop.getDefaultState());
                    worldIn.setBlockState(pos, (BlockState)this.crop.getAttachedStem().getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, direction));
                }
            }
        }
    }

    @Nullable
    protected Item getSeedItem() {
        if (this.crop == Blocks.PUMPKIN) {
            return Items.PUMPKIN_SEEDS;
        }
        return this.crop == Blocks.MELON ? Items.MELON_SEEDS : null;
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        Item item = this.getSeedItem();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) != 7;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        int i = Math.min(7, state.get(AGE) + MathHelper.nextInt(worldIn.rand, 2, 5));
        BlockState blockstate = (BlockState)state.with(AGE, i);
        worldIn.setBlockState(pos, blockstate, 2);
        if (i == 7) {
            blockstate.randomTick(worldIn, pos, worldIn.rand);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public StemGrownBlock getCrop() {
        return this.crop;
    }
}
