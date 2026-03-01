package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConcretePowderBlock
extends FallingBlock {
    private final BlockState solidifiedState;

    public ConcretePowderBlock(Block solidified, AbstractBlock.Properties properties) {
        super(properties);
        this.solidifiedState = solidified.getDefaultState();
    }

    @Override
    public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
        if (ConcretePowderBlock.shouldSolidify(worldIn, pos, hitState)) {
            worldIn.setBlockState(pos, this.solidifiedState, 3);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate;
        BlockPos blockpos;
        World iblockreader = context.getWorld();
        return ConcretePowderBlock.shouldSolidify(iblockreader, blockpos = context.getPos(), blockstate = iblockreader.getBlockState(blockpos)) ? this.solidifiedState : super.getStateForPlacement(context);
    }

    private static boolean shouldSolidify(IBlockReader reader, BlockPos pos, BlockState state) {
        return ConcretePowderBlock.causesSolidify(state) || ConcretePowderBlock.isTouchingLiquid(reader, pos);
    }

    private static boolean isTouchingLiquid(IBlockReader reader, BlockPos pos) {
        boolean flag = false;
        BlockPos.Mutable blockpos$mutable = pos.toMutable();
        for (Direction direction : Direction.values()) {
            BlockState blockstate = reader.getBlockState(blockpos$mutable);
            if (direction == Direction.DOWN && !ConcretePowderBlock.causesSolidify(blockstate)) continue;
            blockpos$mutable.setAndMove(pos, direction);
            blockstate = reader.getBlockState(blockpos$mutable);
            if (!ConcretePowderBlock.causesSolidify(blockstate) || blockstate.isSolidSide(reader, pos, direction.getOpposite())) continue;
            flag = true;
            break;
        }
        return flag;
    }

    private static boolean causesSolidify(BlockState state) {
        return state.getFluidState().isTagged(FluidTags.WATER);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return ConcretePowderBlock.isTouchingLiquid(worldIn, currentPos) ? this.solidifiedState : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {
        return state.getMaterialColor((IBlockReader)reader, (BlockPos)pos).colorValue;
    }
}
