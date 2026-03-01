package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class JukeboxBlock
extends ContainerBlock {
    public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

    protected JukeboxBlock(AbstractBlock.Properties builder) {
        super(builder);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HAS_RECORD, false));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        CompoundNBT compoundnbt1;
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        CompoundNBT compoundnbt = stack.getOrCreateTag();
        if (compoundnbt.contains("BlockEntityTag") && (compoundnbt1 = compoundnbt.getCompound("BlockEntityTag")).contains("RecordItem")) {
            worldIn.setBlockState(pos, (BlockState)state.with(HAS_RECORD, true), 2);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (state.get(HAS_RECORD).booleanValue()) {
            this.dropRecord(worldIn, pos);
            state = (BlockState)state.with(HAS_RECORD, false);
            worldIn.setBlockState(pos, state, 2);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        return ActionResultType.PASS;
    }

    public void insertRecord(IWorld worldIn, BlockPos pos, BlockState state, ItemStack recordStack) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof JukeboxTileEntity) {
            ((JukeboxTileEntity)tileentity).setRecord(recordStack.copy());
            worldIn.setBlockState(pos, (BlockState)state.with(HAS_RECORD, true), 2);
        }
    }

    private void dropRecord(World worldIn, BlockPos pos) {
        JukeboxTileEntity jukeboxtileentity;
        ItemStack itemstack;
        TileEntity tileentity;
        if (!worldIn.isRemote && (tileentity = worldIn.getTileEntity(pos)) instanceof JukeboxTileEntity && !(itemstack = (jukeboxtileentity = (JukeboxTileEntity)tileentity).getRecord()).isEmpty()) {
            worldIn.playEvent(1010, pos, 0);
            jukeboxtileentity.clear();
            float f = 0.7f;
            double d0 = (double)(worldIn.rand.nextFloat() * 0.7f) + (double)0.15f;
            double d1 = (double)(worldIn.rand.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
            double d2 = (double)(worldIn.rand.nextFloat() * 0.7f) + (double)0.15f;
            ItemStack itemstack1 = itemstack.copy();
            ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, itemstack1);
            itementity.setDefaultPickupDelay();
            worldIn.addEntity(itementity);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            this.dropRecord(worldIn, pos);
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new JukeboxTileEntity();
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        Item item;
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof JukeboxTileEntity && (item = ((JukeboxTileEntity)tileentity).getRecord().getItem()) instanceof MusicDiscItem) {
            return ((MusicDiscItem)item).getComparatorValue();
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HAS_RECORD);
    }
}
