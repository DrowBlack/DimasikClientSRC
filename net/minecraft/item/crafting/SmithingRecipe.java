package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SmithingRecipe
implements IRecipe<IInventory> {
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;
    private final ResourceLocation recipeId;

    public SmithingRecipe(ResourceLocation recipeId, Ingredient base, Ingredient addition, ItemStack result) {
        this.recipeId = recipeId;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return this.base.test(inv.getStackInSlot(0)) && this.addition.test(inv.getStackInSlot(1));
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        ItemStack itemstack = this.result.copy();
        CompoundNBT compoundnbt = inv.getStackInSlot(0).getTag();
        if (compoundnbt != null) {
            itemstack.setTag(compoundnbt.copy());
        }
        return itemstack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.result;
    }

    public boolean isValidAdditionItem(ItemStack addition) {
        return this.addition.test(addition);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    @Override
    public ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return IRecipeSerializer.SMITHING;
    }

    @Override
    public IRecipeType<?> getType() {
        return IRecipeType.SMITHING;
    }

    public static class Serializer
    implements IRecipeSerializer<SmithingRecipe> {
        @Override
        public SmithingRecipe read(ResourceLocation recipeId, JsonObject json) {
            Ingredient ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "base"));
            Ingredient ingredient1 = Ingredient.deserialize(JSONUtils.getJsonObject(json, "addition"));
            ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            return new SmithingRecipe(recipeId, ingredient, ingredient1, itemstack);
        }

        @Override
        public SmithingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            Ingredient ingredient = Ingredient.read(buffer);
            Ingredient ingredient1 = Ingredient.read(buffer);
            ItemStack itemstack = buffer.readItemStack();
            return new SmithingRecipe(recipeId, ingredient, ingredient1, itemstack);
        }

        @Override
        public void write(PacketBuffer buffer, SmithingRecipe recipe) {
            recipe.base.write(buffer);
            recipe.addition.write(buffer);
            buffer.writeItemStack(recipe.result);
        }
    }
}
