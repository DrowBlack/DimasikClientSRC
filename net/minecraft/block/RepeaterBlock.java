package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class RepeaterBlock
extends RedstoneDiodeBlock {
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final IntegerProperty DELAY = BlockStateProperties.DELAY_1_4;

    protected RepeaterBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(DELAY, 1)).with(LOCKED, false)).with(POWERED, false));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!player.abilities.allowEdit) {
            return ActionResultType.PASS;
        }
        worldIn.setBlockState(pos, (BlockState)state.func_235896_a_(DELAY), 3);
        return ActionResultType.func_233537_a_(worldIn.isRemote);
    }

    @Override
    protected int getDelay(BlockState state) {
        return state.get(DELAY) * 2;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = super.getStateForPlacement(context);
        return (BlockState)blockstate.with(LOCKED, this.isLocked(context.getWorld(), context.getPos(), blockstate));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !worldIn.isRemote() && facing.getAxis() != stateIn.get(HORIZONTAL_FACING).getAxis() ? (BlockState)stateIn.with(LOCKED, this.isLocked(worldIn, currentPos, stateIn)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isLocked(IWorldReader worldIn, BlockPos pos, BlockState state) {
        return this.getPowerOnSides(worldIn, pos, state) > 0;
    }

    @Override
    protected boolean isAlternateInput(BlockState state) {
        return RepeaterBlock.isDiode(state);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(POWERED).booleanValue()) {
            Direction direction = stateIn.get(HORIZONTAL_FACING);
            double d0 = (double)pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
            double d1 = (double)pos.getY() + 0.4 + (rand.nextDouble() - 0.5) * 0.2;
            double d2 = (double)pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
            float f = -5.0f;
            if (rand.nextBoolean()) {
                f = stateIn.get(DELAY) * 2 - 1;
            }
            double d3 = (f /= 16.0f) * (float)direction.getXOffset();
            double d4 = f * (float)direction.getZOffset();
            worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, d0 + d3, d1, d2 + d4, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, DELAY, LOCKED, POWERED);
    }
}
