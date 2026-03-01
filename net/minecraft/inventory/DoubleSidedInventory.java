package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class DoubleSidedInventory
implements IInventory {
    private final IInventory upperChest;
    private final IInventory lowerChest;

    public DoubleSidedInventory(IInventory upperChest, IInventory lowerChest) {
        if (upperChest == null) {
            upperChest = lowerChest;
        }
        if (lowerChest == null) {
            lowerChest = upperChest;
        }
        this.upperChest = upperChest;
        this.lowerChest = lowerChest;
    }

    @Override
    public int getSizeInventory() {
        return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return this.upperChest.isEmpty() && this.lowerChest.isEmpty();
    }

    public boolean isPartOfLargeChest(IInventory inventoryIn) {
        return this.upperChest == inventoryIn || this.lowerChest == inventoryIn;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlot(index - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return index >= this.upperChest.getSizeInventory() ? this.lowerChest.decrStackSize(index - this.upperChest.getSizeInventory(), count) : this.upperChest.decrStackSize(index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return index >= this.upperChest.getSizeInventory() ? this.lowerChest.removeStackFromSlot(index - this.upperChest.getSizeInventory()) : this.upperChest.removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index >= this.upperChest.getSizeInventory()) {
            this.lowerChest.setInventorySlotContents(index - this.upperChest.getSizeInventory(), stack);
        } else {
            this.upperChest.setInventorySlotContents(index, stack);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return this.upperChest.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        this.upperChest.markDirty();
        this.lowerChest.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return this.upperChest.isUsableByPlayer(player) && this.lowerChest.isUsableByPlayer(player);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        this.upperChest.openInventory(player);
        this.lowerChest.openInventory(player);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        this.upperChest.closeInventory(player);
        this.lowerChest.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index >= this.upperChest.getSizeInventory() ? this.lowerChest.isItemValidForSlot(index - this.upperChest.getSizeInventory(), stack) : this.upperChest.isItemValidForSlot(index, stack);
    }

    @Override
    public void clear() {
        this.upperChest.clear();
        this.lowerChest.clear();
    }
}
