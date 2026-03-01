package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FireworkRocketRecipe
extends SpecialRecipe {
    private static final Ingredient INGREDIENT_PAPER = Ingredient.fromItems(Items.PAPER);
    private static final Ingredient INGREDIENT_GUNPOWDER = Ingredient.fromItems(Items.GUNPOWDER);
    private static final Ingredient INGREDIENT_FIREWORK_STAR = Ingredient.fromItems(Items.FIREWORK_STAR);

    public FireworkRocketRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        boolean flag = false;
        int i = 0;
        for (int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (itemstack.isEmpty()) continue;
            if (INGREDIENT_PAPER.test(itemstack)) {
                if (flag) {
                    return false;
                }
                flag = true;
                continue;
            }
            if (!(INGREDIENT_GUNPOWDER.test(itemstack) ? ++i > 3 : !INGREDIENT_FIREWORK_STAR.test(itemstack))) continue;
            return false;
        }
        return flag && i >= 1;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 3);
        CompoundNBT compoundnbt = itemstack.getOrCreateChildTag("Fireworks");
        ListNBT listnbt = new ListNBT();
        int i = 0;
        for (int j = 0; j < inv.getSizeInventory(); ++j) {
            CompoundNBT compoundnbt1;
            ItemStack itemstack1 = inv.getStackInSlot(j);
            if (itemstack1.isEmpty()) continue;
            if (INGREDIENT_GUNPOWDER.test(itemstack1)) {
                ++i;
                continue;
            }
            if (!INGREDIENT_FIREWORK_STAR.test(itemstack1) || (compoundnbt1 = itemstack1.getChildTag("Explosion")) == null) continue;
            listnbt.add(compoundnbt1);
        }
        compoundnbt.putByte("Flight", (byte)i);
        if (!listnbt.isEmpty()) {
            compoundnbt.put("Explosions", listnbt);
        }
        return itemstack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return IRecipeSerializer.CRAFTING_SPECIAL_FIREWORK_ROCKET;
    }
}
