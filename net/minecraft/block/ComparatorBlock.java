package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.ComparatorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ComparatorBlock
extends RedstoneDiodeBlock
implements ITileEntityProvider {
    public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.COMPARATOR_MODE;

    public ComparatorBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(POWERED, false)).with(MODE, ComparatorMode.COMPARE));
    }

    @Override
    protected int getDelay(BlockState state) {
        return 2;
    }

    @Override
    protected int getActiveSignal(IBlockReader worldIn, BlockPos pos, BlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof ComparatorTileEntity ? ((ComparatorTileEntity)tileentity).getOutputSignal() : 0;
    }

    private int calculateOutput(World worldIn, BlockPos pos, BlockState state) {
        return state.get(MODE) == ComparatorMode.SUBTRACT ? Math.max(this.calculateInputStrength(worldIn, pos, state) - this.getPowerOnSides(worldIn, pos, state), 0) : this.calculateInputStrength(worldIn, pos, state);
    }

    @Override
    protected boolean shouldBePowered(World worldIn, BlockPos pos, BlockState state) {
        int i = this.calculateInputStrength(worldIn, pos, state);
        if (i == 0) {
            return false;
        }
        int j = this.getPowerOnSides(worldIn, pos, state);
        if (i > j) {
            return true;
        }
        return i == j && state.get(MODE) == ComparatorMode.COMPARE;
    }

    @Override
    protected int calculateInputStrength(World worldIn, BlockPos pos, BlockState state) {
        int i = super.calculateInputStrength(worldIn, pos, state);
        Direction direction = state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(direction);
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.hasComparatorInputOverride()) {
            i = blockstate.getComparatorInputOverride(worldIn, blockpos);
        } else if (i < 15 && blockstate.isNormalCube(worldIn, blockpos)) {
            blockpos = blockpos.offset(direction);
            blockstate = worldIn.getBlockState(blockpos);
            ItemFrameEntity itemframeentity = this.findItemFrame(worldIn, direction, blockpos);
            int j = Math.max(itemframeentity == null ? Integer.MIN_VALUE : itemframeentity.getAnalogOutput(), blockstate.hasComparatorInputOverride() ? blockstate.getComparatorInputOverride(worldIn, blockpos) : Integer.MIN_VALUE);
            if (j != Integer.MIN_VALUE) {
                i = j;
            }
        }
        return i;
    }

    @Nullable
    private ItemFrameEntity findItemFrame(World worldIn, Direction facing, BlockPos pos) {
        List<ItemFrameEntity> list = worldIn.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), itemFrame -> itemFrame != null && itemFrame.getHorizontalFacing() == facing);
        return list.size() == 1 ? list.get(0) : null;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!player.abilities.allowEdit) {
            return ActionResultType.PASS;
        }
        float f = (state = (BlockState)state.func_235896_a_(MODE)).get(MODE) == ComparatorMode.SUBTRACT ? 0.55f : 0.5f;
        worldIn.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, f);
        worldIn.setBlockState(pos, state, 2);
        this.onStateChange(worldIn, pos, state);
        return ActionResultType.func_233537_a_(worldIn.isRemote);
    }

    @Override
    protected void updateState(World worldIn, BlockPos pos, BlockState state) {
        if (!worldIn.getPendingBlockTicks().isTickPending(pos, this)) {
            int j;
            int i = this.calculateOutput(worldIn, pos, state);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            int n = j = tileentity instanceof ComparatorTileEntity ? ((ComparatorTileEntity)tileentity).getOutputSignal() : 0;
            if (i != j || state.get(POWERED).booleanValue() != this.shouldBePowered(worldIn, pos, state)) {
                TickPriority tickpriority = this.isFacingTowardsRepeater(worldIn, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
                worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2, tickpriority);
            }
        }
    }

    private void onStateChange(World worldIn, BlockPos pos, BlockState state) {
        int i = this.calculateOutput(worldIn, pos, state);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        int j = 0;
        if (tileentity instanceof ComparatorTileEntity) {
            ComparatorTileEntity comparatortileentity = (ComparatorTileEntity)tileentity;
            j = comparatortileentity.getOutputSignal();
            comparatortileentity.setOutputSignal(i);
        }
        if (j != i || state.get(MODE) == ComparatorMode.COMPARE) {
            boolean flag1 = this.shouldBePowered(worldIn, pos, state);
            boolean flag = state.get(POWERED);
            if (flag && !flag1) {
                worldIn.setBlockState(pos, (BlockState)state.with(POWERED, false), 2);
            } else if (!flag && flag1) {
                worldIn.setBlockState(pos, (BlockState)state.with(POWERED, true), 2);
            }
            this.notifyNeighbors(worldIn, pos, state);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        this.onStateChange(worldIn, pos, state);
    }

    @Override
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new ComparatorTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, MODE, POWERED);
    }
}
