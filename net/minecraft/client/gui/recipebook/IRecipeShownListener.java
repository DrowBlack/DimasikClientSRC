package net.minecraft.client.gui.recipebook;

import net.minecraft.client.gui.recipebook.RecipeBookGui;

public interface IRecipeShownListener {
    public void recipesUpdated();

    public RecipeBookGui getRecipeGui();
}
