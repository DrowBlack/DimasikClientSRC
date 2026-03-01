package net.minecraft.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ServerRecipePlacer;

public class ServerRecipePlacerFurnace<C extends IInventory>
extends ServerRecipePlacer<C> {
    private boolean matches;

    public ServerRecipePlacerFurnace(RecipeBookContainer<C> recipeBookContainer) {
        super(recipeBookContainer);
    }

    @Override
    protected void tryPlaceRecipe(IRecipe<C> recipe, boolean placeAll) {
        ItemStack itemstack;
        this.matches = this.recipeBookContainer.matches(recipe);
        int i = this.recipeItemHelper.getBiggestCraftableStack(recipe, null);
        if (this.matches && ((itemstack = this.recipeBookContainer.getSlot(0).getStack()).isEmpty() || i <= itemstack.getCount())) {
            return;
        }
        IntArrayList intlist = new IntArrayList();
        int j = this.getMaxAmount(placeAll, i, this.matches);
        if (this.recipeItemHelper.canCraft(recipe, intlist, j)) {
            if (!this.matches) {
                this.giveToPlayer(this.recipeBookContainer.getOutputSlot());
                this.giveToPlayer(0);
            }
            this.func_201516_a(j, intlist);
        }
    }

    @Override
    protected void clear() {
        this.giveToPlayer(this.recipeBookContainer.getOutputSlot());
        super.clear();
    }

    protected void func_201516_a(int p_201516_1_, IntList p_201516_2_) {
        IntListIterator iterator = p_201516_2_.iterator();
        Slot slot = this.recipeBookContainer.getSlot(0);
        ItemStack itemstack = RecipeItemHelper.unpack((Integer)iterator.next());
        if (!itemstack.isEmpty()) {
            int i = Math.min(itemstack.getMaxStackSize(), p_201516_1_);
            if (this.matches) {
                i -= slot.getStack().getCount();
            }
            for (int j = 0; j < i; ++j) {
                this.consumeIngredient(slot, itemstack);
            }
        }
    }
}
