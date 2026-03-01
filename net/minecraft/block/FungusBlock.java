package net.minecraft.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.HugeFungusConfig;
import net.minecraft.world.server.ServerWorld;

public class FungusBlock
extends BushBlock
implements IGrowable {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
    private final Supplier<ConfiguredFeature<HugeFungusConfig, ?>> fungusFeature;

    protected FungusBlock(AbstractBlock.Properties properties, Supplier<ConfiguredFeature<HugeFungusConfig, ?>> fungusFeature) {
        super(properties);
        this.fungusFeature = fungusFeature;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.isIn(BlockTags.NYLIUM) || state.isIn(Blocks.MYCELIUM) || state.isIn(Blocks.SOUL_SOIL) || super.isValidGround(state, worldIn, pos);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        Block block = ((HugeFungusConfig)this.fungusFeature.get().config).field_236303_f_.getBlock();
        Block block1 = worldIn.getBlockState(pos.down()).getBlock();
        return block1 == block;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return (double)rand.nextFloat() < 0.4;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        this.fungusFeature.get().func_242765_a(worldIn, worldIn.getChunkProvider().getChunkGenerator(), rand, pos);
    }
}
