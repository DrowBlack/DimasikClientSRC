package net.minecraft.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.ResourceLocation;

public abstract class SpecialRecipe
implements ICraftingRecipe {
    private final ResourceLocation id;

    public SpecialRecipe(ResourceLocation idIn) {
        this.id = idIn;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
}
