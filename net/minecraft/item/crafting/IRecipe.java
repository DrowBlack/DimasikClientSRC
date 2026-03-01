package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IRecipe<C extends IInventory> {
    public boolean matches(C var1, World var2);

    public ItemStack getCraftingResult(C var1);

    public boolean canFit(int var1, int var2);

    public ItemStack getRecipeOutput();

    default public NonNullList<ItemStack> getRemainingItems(C inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i) {
            Item item = inv.getStackInSlot(i).getItem();
            if (!item.hasContainerItem()) continue;
            nonnulllist.set(i, new ItemStack(item.getContainerItem()));
        }
        return nonnulllist;
    }

    default public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    default public boolean isDynamic() {
        return false;
    }

    default public String getGroup() {
        return "";
    }

    default public ItemStack getIcon() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    public ResourceLocation getId();

    public IRecipeSerializer<?> getSerializer();

    public IRecipeType<?> getType();
}
