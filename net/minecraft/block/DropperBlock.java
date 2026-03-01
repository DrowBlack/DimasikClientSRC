package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class DropperBlock
extends DispenserBlock {
    private static final IDispenseItemBehavior DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior();

    public DropperBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    protected IDispenseItemBehavior getBehavior(ItemStack stack) {
        return DISPENSE_BEHAVIOR;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new DropperTileEntity();
    }

    @Override
    protected void dispense(ServerWorld worldIn, BlockPos pos) {
        ProxyBlockSource proxyblocksource = new ProxyBlockSource(worldIn, pos);
        DispenserTileEntity dispensertileentity = (DispenserTileEntity)proxyblocksource.getBlockTileEntity();
        int i = dispensertileentity.getDispenseSlot();
        if (i < 0) {
            worldIn.playEvent(1001, pos, 0);
        } else {
            ItemStack itemstack = dispensertileentity.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                ItemStack itemstack1;
                Direction direction = worldIn.getBlockState(pos).get(FACING);
                IInventory iinventory = HopperTileEntity.getInventoryAtPosition(worldIn, pos.offset(direction));
                if (iinventory == null) {
                    itemstack1 = DISPENSE_BEHAVIOR.dispense(proxyblocksource, itemstack);
                } else {
                    itemstack1 = HopperTileEntity.putStackInInventoryAllSlots(dispensertileentity, iinventory, itemstack.copy().split(1), direction.getOpposite());
                    if (itemstack1.isEmpty()) {
                        itemstack1 = itemstack.copy();
                        itemstack1.shrink(1);
                    } else {
                        itemstack1 = itemstack.copy();
                    }
                }
                dispensertileentity.setInventorySlotContents(i, itemstack1);
            }
        }
    }
}
