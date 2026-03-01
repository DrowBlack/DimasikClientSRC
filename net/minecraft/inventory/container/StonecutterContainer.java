package net.minecraft.inventory.container;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StonecutterContainer
extends Container {
    private final IWorldPosCallable worldPosCallable;
    private final IntReferenceHolder selectedRecipe = IntReferenceHolder.single();
    private final World world;
    private List<StonecuttingRecipe> recipes = Lists.newArrayList();
    private ItemStack itemStackInput = ItemStack.EMPTY;
    private long lastOnTake;
    final Slot inputInventorySlot;
    final Slot outputInventorySlot;
    private Runnable inventoryUpdateListener = () -> {};
    public final IInventory inputInventory = new Inventory(1){

        @Override
        public void markDirty() {
            super.markDirty();
            StonecutterContainer.this.onCraftMatrixChanged(this);
            StonecutterContainer.this.inventoryUpdateListener.run();
        }
    };
    private final CraftResultInventory inventory = new CraftResultInventory();

    public StonecutterContainer(int windowIdIn, PlayerInventory playerInventoryIn) {
        this(windowIdIn, playerInventoryIn, IWorldPosCallable.DUMMY);
    }

    public StonecutterContainer(int windowIdIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosCallableIn) {
        super(ContainerType.STONECUTTER, windowIdIn);
        this.worldPosCallable = worldPosCallableIn;
        this.world = playerInventoryIn.player.world;
        this.inputInventorySlot = this.addSlot(new Slot(this.inputInventory, 0, 20, 33));
        this.outputInventorySlot = this.addSlot(new Slot(this.inventory, 1, 143, 33){

            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
                stack.onCrafting(thePlayer.world, thePlayer, stack.getCount());
                StonecutterContainer.this.inventory.onCrafting(thePlayer);
                ItemStack itemstack = StonecutterContainer.this.inputInventorySlot.decrStackSize(1);
                if (!itemstack.isEmpty()) {
                    StonecutterContainer.this.updateRecipeResultSlot();
                }
                worldPosCallableIn.consume((p_216954_1_, p_216954_2_) -> {
                    long l = p_216954_1_.getGameTime();
                    if (StonecutterContainer.this.lastOnTake != l) {
                        p_216954_1_.playSound(null, (BlockPos)p_216954_2_, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        StonecutterContainer.this.lastOnTake = l;
                    }
                });
                return super.onTake(thePlayer, stack);
            }
        });
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142));
        }
        this.trackInt(this.selectedRecipe);
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public List<StonecuttingRecipe> getRecipeList() {
        return this.recipes;
    }

    public int getRecipeListSize() {
        return this.recipes.size();
    }

    public boolean hasItemsinInputSlot() {
        return this.inputInventorySlot.getHasStack() && !this.recipes.isEmpty();
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return StonecutterContainer.isWithinUsableDistance(this.worldPosCallable, playerIn, Blocks.STONECUTTER);
    }

    @Override
    public boolean enchantItem(PlayerEntity playerIn, int id) {
        if (this.func_241818_d_(id)) {
            this.selectedRecipe.set(id);
            this.updateRecipeResultSlot();
        }
        return true;
    }

    private boolean func_241818_d_(int p_241818_1_) {
        return p_241818_1_ >= 0 && p_241818_1_ < this.recipes.size();
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        ItemStack itemstack = this.inputInventorySlot.getStack();
        if (itemstack.getItem() != this.itemStackInput.getItem()) {
            this.itemStackInput = itemstack.copy();
            this.updateAvailableRecipes(inventoryIn, itemstack);
        }
    }

    private void updateAvailableRecipes(IInventory inventoryIn, ItemStack stack) {
        this.recipes.clear();
        this.selectedRecipe.set(-1);
        this.outputInventorySlot.putStack(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.recipes = this.world.getRecipeManager().getRecipes(IRecipeType.STONECUTTING, inventoryIn, this.world);
        }
    }

    private void updateRecipeResultSlot() {
        if (!this.recipes.isEmpty() && this.func_241818_d_(this.selectedRecipe.get())) {
            StonecuttingRecipe stonecuttingrecipe = this.recipes.get(this.selectedRecipe.get());
            this.inventory.setRecipeUsed(stonecuttingrecipe);
            this.outputInventorySlot.putStack(stonecuttingrecipe.getCraftingResult(this.inputInventory));
        } else {
            this.outputInventorySlot.putStack(ItemStack.EMPTY);
        }
        this.detectAndSendChanges();
    }

    @Override
    public ContainerType<?> getType() {
        return ContainerType.STONECUTTER;
    }

    public void setInventoryUpdateListener(Runnable listenerIn) {
        this.inventoryUpdateListener = listenerIn;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.inventory && super.canMergeSlot(stack, slotIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (index == 1) {
                item.onCreated(itemstack1, playerIn.world, playerIn);
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index == 0 ? !this.mergeItemStack(itemstack1, 2, 38, false) : (this.world.getRecipeManager().getRecipe(IRecipeType.STONECUTTING, new Inventory(itemstack1), this.world).isPresent() ? !this.mergeItemStack(itemstack1, 0, 1, false) : (index >= 2 && index < 29 ? !this.mergeItemStack(itemstack1, 29, 38, false) : index >= 29 && index < 38 && !this.mergeItemStack(itemstack1, 2, 29, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
            slot.onSlotChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
            this.detectAndSendChanges();
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.inventory.removeStackFromSlot(1);
        this.worldPosCallable.consume((p_217079_2_, p_217079_3_) -> this.clearContainer(playerIn, playerIn.world, this.inputInventory));
    }
}
