package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpongeBlock
extends Block {
    protected SpongeBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.isIn(state.getBlock())) {
            this.tryAbsorb(worldIn, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        this.tryAbsorb(worldIn, pos);
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    protected void tryAbsorb(World worldIn, BlockPos pos) {
        if (this.absorb(worldIn, pos)) {
            worldIn.setBlockState(pos, Blocks.WET_SPONGE.getDefaultState(), 2);
            worldIn.playEvent(2001, pos, Block.getStateId(Blocks.WATER.getDefaultState()));
        }
    }

    private boolean absorb(World worldIn, BlockPos pos) {
        LinkedList<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<BlockPos, Integer>(pos, 0));
        int i = 0;
        while (!queue.isEmpty()) {
            Tuple tuple = (Tuple)queue.poll();
            BlockPos blockpos = (BlockPos)tuple.getA();
            int j = (Integer)tuple.getB();
            for (Direction direction : Direction.values()) {
                BlockPos blockpos1 = blockpos.offset(direction);
                BlockState blockstate = worldIn.getBlockState(blockpos1);
                FluidState fluidstate = worldIn.getFluidState(blockpos1);
                Material material = blockstate.getMaterial();
                if (!fluidstate.isTagged(FluidTags.WATER)) continue;
                if (blockstate.getBlock() instanceof IBucketPickupHandler && ((IBucketPickupHandler)((Object)blockstate.getBlock())).pickupFluid(worldIn, blockpos1, blockstate) != Fluids.EMPTY) {
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Tuple<BlockPos, Integer>(blockpos1, j + 1));
                    continue;
                }
                if (blockstate.getBlock() instanceof FlowingFluidBlock) {
                    worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Tuple<BlockPos, Integer>(blockpos1, j + 1));
                    continue;
                }
                if (material != Material.OCEAN_PLANT && material != Material.SEA_GRASS) continue;
                TileEntity tileentity = blockstate.getBlock().isTileEntityProvider() ? worldIn.getTileEntity(blockpos1) : null;
                SpongeBlock.spawnDrops(blockstate, worldIn, blockpos1, tileentity);
                worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
                ++i;
                if (j >= 6) continue;
                queue.add(new Tuple<BlockPos, Integer>(blockpos1, j + 1));
            }
            if (i <= 64) continue;
            break;
        }
        return i > 0;
    }
}
