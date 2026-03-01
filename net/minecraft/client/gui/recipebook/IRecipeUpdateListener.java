package net.minecraft.client.gui.recipebook;

import java.util.List;
import net.minecraft.item.crafting.IRecipe;

public interface IRecipeUpdateListener {
    public void recipesShown(List<IRecipe<?>> var1);
}
