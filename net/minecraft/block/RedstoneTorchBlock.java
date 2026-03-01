package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RedstoneTorchBlock
extends TorchBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final Map<IBlockReader, List<Toggle>> BURNED_TORCHES = new WeakHashMap<IBlockReader, List<Toggle>>();

    protected RedstoneTorchBlock(AbstractBlock.Properties properties) {
        super(properties, RedstoneParticleData.REDSTONE_DUST);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LIT, true));
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        for (Direction direction : Direction.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving) {
            for (Direction direction : Direction.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
        }
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(LIT) != false && Direction.UP != side ? 15 : 0;
    }

    protected boolean shouldBeOff(World worldIn, BlockPos pos, BlockState state) {
        return worldIn.isSidePowered(pos.down(), Direction.DOWN);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        boolean flag = this.shouldBeOff(worldIn, pos, state);
        List<Toggle> list = BURNED_TORCHES.get(worldIn);
        while (list != null && !list.isEmpty() && worldIn.getGameTime() - list.get((int)0).time > 60L) {
            list.remove(0);
        }
        if (state.get(LIT).booleanValue()) {
            if (flag) {
                worldIn.setBlockState(pos, (BlockState)state.with(LIT, false), 3);
                if (RedstoneTorchBlock.isBurnedOut(worldIn, pos, true)) {
                    worldIn.playEvent(1502, pos, 0);
                    worldIn.getPendingBlockTicks().scheduleTick(pos, worldIn.getBlockState(pos).getBlock(), 160);
                }
            }
        } else if (!flag && !RedstoneTorchBlock.isBurnedOut(worldIn, pos, false)) {
            worldIn.setBlockState(pos, (BlockState)state.with(LIT, true), 3);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (state.get(LIT).booleanValue() == this.shouldBeOff(worldIn, pos, state) && !worldIn.getPendingBlockTicks().isTickPending(pos, this)) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
        }
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return side == Direction.DOWN ? blockState.getWeakPower(blockAccess, pos, side) : 0;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(LIT).booleanValue()) {
            double d0 = (double)pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
            double d1 = (double)pos.getY() + 0.7 + (rand.nextDouble() - 0.5) * 0.2;
            double d2 = (double)pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
            worldIn.addParticle(this.particleData, d0, d1, d2, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    private static boolean isBurnedOut(World world, BlockPos worldIn, boolean pos) {
        List list = BURNED_TORCHES.computeIfAbsent(world, reader -> Lists.newArrayList());
        if (pos) {
            list.add(new Toggle(worldIn.toImmutable(), world.getGameTime()));
        }
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            Toggle redstonetorchblock$toggle = (Toggle)list.get(j);
            if (!redstonetorchblock$toggle.pos.equals(worldIn) || ++i < 8) continue;
            return true;
        }
        return false;
    }

    public static class Toggle {
        private final BlockPos pos;
        private final long time;

        public Toggle(BlockPos pos, long time) {
            this.pos = pos;
            this.time = time;
        }
    }
}
