package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class MapExtendingRecipe
extends ShapedRecipe {
    public MapExtendingRecipe(ResourceLocation id) {
        super(id, "", 3, 3, NonNullList.from(Ingredient.EMPTY, Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.FILLED_MAP), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER)), new ItemStack(Items.MAP));
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if (!super.matches(inv, worldIn)) {
            return false;
        }
        ItemStack itemstack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = inv.getStackInSlot(i);
            if (itemstack1.getItem() != Items.FILLED_MAP) continue;
            itemstack = itemstack1;
        }
        if (itemstack.isEmpty()) {
            return false;
        }
        MapData mapdata = FilledMapItem.getMapData(itemstack, worldIn);
        if (mapdata == null) {
            return false;
        }
        if (this.isExplorationMap(mapdata)) {
            return false;
        }
        return mapdata.scale < 4;
    }

    private boolean isExplorationMap(MapData data) {
        if (data.mapDecorations != null) {
            for (MapDecoration mapdecoration : data.mapDecorations.values()) {
                if (mapdecoration.getType() != MapDecoration.Type.MANSION && mapdecoration.getType() != MapDecoration.Type.MONUMENT) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack itemstack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = inv.getStackInSlot(i);
            if (itemstack1.getItem() != Items.FILLED_MAP) continue;
            itemstack = itemstack1;
        }
        itemstack = itemstack.copy();
        itemstack.setCount(1);
        itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
        return itemstack;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return IRecipeSerializer.CRAFTING_SPECIAL_MAPEXTENDING;
    }
}
