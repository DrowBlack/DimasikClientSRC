package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IInventory
extends IClearable {
    public int getSizeInventory();

    public boolean isEmpty();

    public ItemStack getStackInSlot(int var1);

    public ItemStack decrStackSize(int var1, int var2);

    public ItemStack removeStackFromSlot(int var1);

    public void setInventorySlotContents(int var1, ItemStack var2);

    default public int getInventoryStackLimit() {
        return 64;
    }

    public void markDirty();

    public boolean isUsableByPlayer(PlayerEntity var1);

    default public void openInventory(PlayerEntity player) {
    }

    default public void closeInventory(PlayerEntity player) {
    }

    default public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    default public int count(Item itemIn) {
        int i = 0;
        for (int j = 0; j < this.getSizeInventory(); ++j) {
            ItemStack itemstack = this.getStackInSlot(j);
            if (!itemstack.getItem().equals(itemIn)) continue;
            i += itemstack.getCount();
        }
        return i;
    }

    default public boolean hasAny(Set<Item> set) {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            ItemStack itemstack = this.getStackInSlot(i);
            if (!set.contains(itemstack.getItem()) || itemstack.getCount() <= 0) continue;
            return true;
        }
        return false;
    }
}
