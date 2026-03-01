package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock
extends AbstractPressurePlateBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
    private final int maxWeight;

    protected WeightedPressurePlateBlock(int maxWeight, AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWER, 0));
        this.maxWeight = maxWeight;
    }

    @Override
    protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
        int i = Math.min(worldIn.getEntitiesWithinAABB(Entity.class, PRESSURE_AABB.offset(pos)).size(), this.maxWeight);
        if (i > 0) {
            float f = (float)Math.min(this.maxWeight, i) / (float)this.maxWeight;
            return MathHelper.ceil(f * 15.0f);
        }
        return 0;
    }

    @Override
    protected void playClickOnSound(IWorld worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.90000004f);
    }

    @Override
    protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.75f);
    }

    @Override
    protected int getRedstoneStrength(BlockState state) {
        return state.get(POWER);
    }

    @Override
    protected BlockState setRedstoneStrength(BlockState state, int strength) {
        return (BlockState)state.with(POWER, strength);
    }

    @Override
    protected int getPoweredDuration() {
        return 10;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }
}
