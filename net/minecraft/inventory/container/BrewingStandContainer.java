package net.minecraft.inventory.container;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class BrewingStandContainer
extends Container {
    private final IInventory tileBrewingStand;
    private final IIntArray field_216983_d;
    private final Slot slot;

    public BrewingStandContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(5), new IntArray(2));
    }

    public BrewingStandContainer(int id, PlayerInventory playerInventory, IInventory inventory, IIntArray p_i50096_4_) {
        super(ContainerType.BREWING_STAND, id);
        BrewingStandContainer.assertInventorySize(inventory, 5);
        BrewingStandContainer.assertIntArraySize(p_i50096_4_, 2);
        this.tileBrewingStand = inventory;
        this.field_216983_d = p_i50096_4_;
        this.addSlot(new PotionSlot(inventory, 0, 56, 51));
        this.addSlot(new PotionSlot(inventory, 1, 79, 58));
        this.addSlot(new PotionSlot(inventory, 2, 102, 51));
        this.slot = this.addSlot(new IngredientSlot(inventory, 3, 79, 17));
        this.addSlot(new FuelSlot(inventory, 4, 17, 17));
        this.trackIntArray(p_i50096_4_);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.tileBrewingStand.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if ((index < 0 || index > 2) && index != 3 && index != 4) {
                if (FuelSlot.isValidBrewingFuel(itemstack) ? this.mergeItemStack(itemstack1, 4, 5, false) || this.slot.isItemValid(itemstack1) && !this.mergeItemStack(itemstack1, 3, 4, false) : (this.slot.isItemValid(itemstack1) ? !this.mergeItemStack(itemstack1, 3, 4, false) : (PotionSlot.canHoldPotion(itemstack) && itemstack.getCount() == 1 ? !this.mergeItemStack(itemstack1, 0, 3, false) : (index >= 5 && index < 32 ? !this.mergeItemStack(itemstack1, 32, 41, false) : (index >= 32 && index < 41 ? !this.mergeItemStack(itemstack1, 5, 32, false) : !this.mergeItemStack(itemstack1, 5, 41, false)))))) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.mergeItemStack(itemstack1, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    public int func_216982_e() {
        return this.field_216983_d.get(1);
    }

    public int func_216981_f() {
        return this.field_216983_d.get(0);
    }

    static class PotionSlot
    extends Slot {
        public PotionSlot(IInventory p_i47598_1_, int p_i47598_2_, int p_i47598_3_, int p_i47598_4_) {
            super(p_i47598_1_, p_i47598_2_, p_i47598_3_, p_i47598_4_);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return PotionSlot.canHoldPotion(stack);
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }

        @Override
        public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
            Potion potion = PotionUtils.getPotionFromItem(stack);
            if (thePlayer instanceof ServerPlayerEntity) {
                CriteriaTriggers.BREWED_POTION.trigger((ServerPlayerEntity)thePlayer, potion);
            }
            super.onTake(thePlayer, stack);
            return stack;
        }

        public static boolean canHoldPotion(ItemStack stack) {
            Item item = stack.getItem();
            return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE;
        }
    }

    static class IngredientSlot
    extends Slot {
        public IngredientSlot(IInventory iInventoryIn, int index, int xPosition, int yPosition) {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return PotionBrewing.isReagent(stack);
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }
    }

    static class FuelSlot
    extends Slot {
        public FuelSlot(IInventory iInventoryIn, int index, int xPosition, int yPosition) {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return FuelSlot.isValidBrewingFuel(stack);
        }

        public static boolean isValidBrewingFuel(ItemStack itemStackIn) {
            return itemStackIn.getItem() == Items.BLAZE_POWDER;
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }
    }
}
