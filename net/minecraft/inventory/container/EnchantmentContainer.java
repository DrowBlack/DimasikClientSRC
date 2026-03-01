package net.minecraft.inventory.container;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class EnchantmentContainer
extends Container {
    private final IInventory tableInventory = new Inventory(2){

        @Override
        public void markDirty() {
            super.markDirty();
            EnchantmentContainer.this.onCraftMatrixChanged(this);
        }
    };
    private final IWorldPosCallable worldPosCallable;
    private final Random rand = new Random();
    private final IntReferenceHolder xpSeed = IntReferenceHolder.single();
    public final int[] enchantLevels = new int[3];
    public final int[] enchantClue = new int[]{-1, -1, -1};
    public final int[] worldClue = new int[]{-1, -1, -1};

    public EnchantmentContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public EnchantmentContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(ContainerType.ENCHANTMENT, id);
        this.worldPosCallable = worldPosCallable;
        this.addSlot(new Slot(this.tableInventory, 0, 15, 47){

            @Override
            public boolean isItemValid(ItemStack stack) {
                return true;
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.tableInventory, 1, 35, 47){

            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.LAPIS_LAZULI;
            }
        });
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
        this.trackInt(IntReferenceHolder.create(this.enchantLevels, 0));
        this.trackInt(IntReferenceHolder.create(this.enchantLevels, 1));
        this.trackInt(IntReferenceHolder.create(this.enchantLevels, 2));
        this.trackInt(this.xpSeed).set(playerInventory.player.getXPSeed());
        this.trackInt(IntReferenceHolder.create(this.enchantClue, 0));
        this.trackInt(IntReferenceHolder.create(this.enchantClue, 1));
        this.trackInt(IntReferenceHolder.create(this.enchantClue, 2));
        this.trackInt(IntReferenceHolder.create(this.worldClue, 0));
        this.trackInt(IntReferenceHolder.create(this.worldClue, 1));
        this.trackInt(IntReferenceHolder.create(this.worldClue, 2));
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (inventoryIn == this.tableInventory) {
            ItemStack itemstack = inventoryIn.getStackInSlot(0);
            if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                this.worldPosCallable.consume((p_217002_2_, p_217002_3_) -> {
                    int j = 0;
                    for (int k = -1; k <= 1; ++k) {
                        for (int l = -1; l <= 1; ++l) {
                            if (k == 0 && l == 0 || !p_217002_2_.isAirBlock(p_217002_3_.add(l, 0, k)) || !p_217002_2_.isAirBlock(p_217002_3_.add(l, 1, k))) continue;
                            if (p_217002_2_.getBlockState(p_217002_3_.add(l * 2, 0, k * 2)).isIn(Blocks.BOOKSHELF)) {
                                ++j;
                            }
                            if (p_217002_2_.getBlockState(p_217002_3_.add(l * 2, 1, k * 2)).isIn(Blocks.BOOKSHELF)) {
                                ++j;
                            }
                            if (l == 0 || k == 0) continue;
                            if (p_217002_2_.getBlockState(p_217002_3_.add(l * 2, 0, k)).isIn(Blocks.BOOKSHELF)) {
                                ++j;
                            }
                            if (p_217002_2_.getBlockState(p_217002_3_.add(l * 2, 1, k)).isIn(Blocks.BOOKSHELF)) {
                                ++j;
                            }
                            if (p_217002_2_.getBlockState(p_217002_3_.add(l, 0, k * 2)).isIn(Blocks.BOOKSHELF)) {
                                ++j;
                            }
                            if (!p_217002_2_.getBlockState(p_217002_3_.add(l, 1, k * 2)).isIn(Blocks.BOOKSHELF)) continue;
                            ++j;
                        }
                    }
                    this.rand.setSeed(this.xpSeed.get());
                    for (int i1 = 0; i1 < 3; ++i1) {
                        this.enchantLevels[i1] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i1, j, itemstack);
                        this.enchantClue[i1] = -1;
                        this.worldClue[i1] = -1;
                        if (this.enchantLevels[i1] >= i1 + 1) continue;
                        this.enchantLevels[i1] = 0;
                    }
                    for (int j1 = 0; j1 < 3; ++j1) {
                        List<EnchantmentData> list;
                        if (this.enchantLevels[j1] <= 0 || (list = this.getEnchantmentList(itemstack, j1, this.enchantLevels[j1])) == null || list.isEmpty()) continue;
                        EnchantmentData enchantmentdata = list.get(this.rand.nextInt(list.size()));
                        this.enchantClue[j1] = Registry.ENCHANTMENT.getId(enchantmentdata.enchantment);
                        this.worldClue[j1] = enchantmentdata.enchantmentLevel;
                    }
                    this.detectAndSendChanges();
                });
            } else {
                for (int i = 0; i < 3; ++i) {
                    this.enchantLevels[i] = 0;
                    this.enchantClue[i] = -1;
                    this.worldClue[i] = -1;
                }
            }
        }
    }

    @Override
    public boolean enchantItem(PlayerEntity playerIn, int id) {
        ItemStack itemstack = this.tableInventory.getStackInSlot(0);
        ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
        int i = id + 1;
        if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !playerIn.abilities.isCreativeMode) {
            return false;
        }
        if (this.enchantLevels[id] <= 0 || itemstack.isEmpty() || (playerIn.experienceLevel < i || playerIn.experienceLevel < this.enchantLevels[id]) && !playerIn.abilities.isCreativeMode) {
            return false;
        }
        this.worldPosCallable.consume((p_217003_6_, p_217003_7_) -> {
            ItemStack itemstack2 = itemstack;
            List<EnchantmentData> list = this.getEnchantmentList(itemstack, id, this.enchantLevels[id]);
            if (!list.isEmpty()) {
                boolean flag;
                playerIn.onEnchant(itemstack, i);
                boolean bl = flag = itemstack.getItem() == Items.BOOK;
                if (flag) {
                    itemstack2 = new ItemStack(Items.ENCHANTED_BOOK);
                    CompoundNBT compoundnbt = itemstack.getTag();
                    if (compoundnbt != null) {
                        itemstack2.setTag(compoundnbt.copy());
                    }
                    this.tableInventory.setInventorySlotContents(0, itemstack2);
                }
                for (int j = 0; j < list.size(); ++j) {
                    EnchantmentData enchantmentdata = list.get(j);
                    if (flag) {
                        EnchantedBookItem.addEnchantment(itemstack2, enchantmentdata);
                        continue;
                    }
                    itemstack2.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
                }
                if (!playerIn.abilities.isCreativeMode) {
                    itemstack1.shrink(i);
                    if (itemstack1.isEmpty()) {
                        this.tableInventory.setInventorySlotContents(1, ItemStack.EMPTY);
                    }
                }
                playerIn.addStat(Stats.ENCHANT_ITEM);
                if (playerIn instanceof ServerPlayerEntity) {
                    CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity)playerIn, itemstack2, i);
                }
                this.tableInventory.markDirty();
                this.xpSeed.set(playerIn.getXPSeed());
                this.onCraftMatrixChanged(this.tableInventory);
                p_217003_6_.playSound(null, (BlockPos)p_217003_7_, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0f, p_217003_6_.rand.nextFloat() * 0.1f + 0.9f);
            }
        });
        return true;
    }

    private List<EnchantmentData> getEnchantmentList(ItemStack stack, int enchantSlot, int level) {
        this.rand.setSeed(this.xpSeed.get() + enchantSlot);
        List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.rand, stack, level, false);
        if (stack.getItem() == Items.BOOK && list.size() > 1) {
            list.remove(this.rand.nextInt(list.size()));
        }
        return list;
    }

    public int getLapisAmount() {
        ItemStack itemstack = this.tableInventory.getStackInSlot(1);
        return itemstack.isEmpty() ? 0 : itemstack.getCount();
    }

    public int func_217005_f() {
        return this.xpSeed.get();
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.worldPosCallable.consume((p_217004_2_, p_217004_3_) -> this.clearContainer(playerIn, playerIn.world, this.tableInventory));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return EnchantmentContainer.isWithinUsableDistance(this.worldPosCallable, playerIn, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 1) {
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.getItem() == Items.LAPIS_LAZULI) {
                if (!this.mergeItemStack(itemstack1, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(itemstack1)) {
                    return ItemStack.EMPTY;
                }
                ItemStack itemstack2 = itemstack1.copy();
                itemstack2.setCount(1);
                itemstack1.shrink(1);
                ((Slot)this.inventorySlots.get(0)).putStack(itemstack2);
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
}
