package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class LecternContainer
extends Container {
    private final IInventory lecternInventory;
    private final IIntArray field_217019_d;

    public LecternContainer(int p_i50075_1_) {
        this(p_i50075_1_, new Inventory(1), new IntArray(1));
    }

    public LecternContainer(int id, IInventory p_i50076_2_, IIntArray p_i50076_3_) {
        super(ContainerType.LECTERN, id);
        LecternContainer.assertInventorySize(p_i50076_2_, 1);
        LecternContainer.assertIntArraySize(p_i50076_3_, 1);
        this.lecternInventory = p_i50076_2_;
        this.field_217019_d = p_i50076_3_;
        this.addSlot(new Slot(p_i50076_2_, 0, 0, 0){

            @Override
            public void onSlotChanged() {
                super.onSlotChanged();
                LecternContainer.this.onCraftMatrixChanged(this.inventory);
            }
        });
        this.trackIntArray(p_i50076_3_);
    }

    @Override
    public boolean enchantItem(PlayerEntity playerIn, int id) {
        if (id >= 100) {
            int k = id - 100;
            this.updateProgressBar(0, k);
            return true;
        }
        switch (id) {
            case 1: {
                int j = this.field_217019_d.get(0);
                this.updateProgressBar(0, j - 1);
                return true;
            }
            case 2: {
                int i = this.field_217019_d.get(0);
                this.updateProgressBar(0, i + 1);
                return true;
            }
            case 3: {
                if (!playerIn.isAllowEdit()) {
                    return false;
                }
                ItemStack itemstack = this.lecternInventory.removeStackFromSlot(0);
                this.lecternInventory.markDirty();
                if (!playerIn.inventory.addItemStackToInventory(itemstack)) {
                    playerIn.dropItem(itemstack, false);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        this.detectAndSendChanges();
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.lecternInventory.isUsableByPlayer(playerIn);
    }

    public ItemStack getBook() {
        return this.lecternInventory.getStackInSlot(0);
    }

    public int getPage() {
        return this.field_217019_d.get(0);
    }
}
