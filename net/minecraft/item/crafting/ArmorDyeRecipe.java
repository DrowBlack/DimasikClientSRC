package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ArmorDyeRecipe
extends SpecialRecipe {
    public ArmorDyeRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack itemstack = ItemStack.EMPTY;
        ArrayList<ItemStack> list = Lists.newArrayList();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack1 = inv.getStackInSlot(i);
            if (itemstack1.isEmpty()) continue;
            if (itemstack1.getItem() instanceof IDyeableArmorItem) {
                if (!itemstack.isEmpty()) {
                    return false;
                }
                itemstack = itemstack1;
                continue;
            }
            if (!(itemstack1.getItem() instanceof DyeItem)) {
                return false;
            }
            list.add(itemstack1);
        }
        return !itemstack.isEmpty() && !list.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ArrayList<DyeItem> list = Lists.newArrayList();
        ItemStack itemstack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack1 = inv.getStackInSlot(i);
            if (itemstack1.isEmpty()) continue;
            Item item = itemstack1.getItem();
            if (item instanceof IDyeableArmorItem) {
                if (!itemstack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                itemstack = itemstack1.copy();
                continue;
            }
            if (!(item instanceof DyeItem)) {
                return ItemStack.EMPTY;
            }
            list.add((DyeItem)item);
        }
        return !itemstack.isEmpty() && !list.isEmpty() ? IDyeableArmorItem.dyeItem(itemstack, list) : ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return IRecipeSerializer.CRAFTING_SPECIAL_ARMORDYE;
    }
}
