package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FireworkStarFadeRecipe
extends SpecialRecipe {
    private static final Ingredient INGREDIENT_FIREWORK_STAR = Ingredient.fromItems(Items.FIREWORK_STAR);

    public FireworkStarFadeRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        boolean flag = false;
        boolean flag1 = false;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.isEmpty()) continue;
            if (itemstack.getItem() instanceof DyeItem) {
                flag = true;
                continue;
            }
            if (!INGREDIENT_FIREWORK_STAR.test(itemstack)) {
                return false;
            }
            if (flag1) {
                return false;
            }
            flag1 = true;
        }
        return flag1 && flag;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ArrayList<Integer> list = Lists.newArrayList();
        ItemStack itemstack = null;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack1 = inv.getStackInSlot(i);
            Item item = itemstack1.getItem();
            if (item instanceof DyeItem) {
                list.add(((DyeItem)item).getDyeColor().getFireworkColor());
                continue;
            }
            if (!INGREDIENT_FIREWORK_STAR.test(itemstack1)) continue;
            itemstack = itemstack1.copy();
            itemstack.setCount(1);
        }
        if (itemstack != null && !list.isEmpty()) {
            itemstack.getOrCreateChildTag("Explosion").putIntArray("FadeColors", list);
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return IRecipeSerializer.CRAFTING_SPECIAL_FIREWORK_STAR_FADE;
    }
}
