package net.minecraft.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe>
implements IRecipeSerializer<T> {
    private final int cookingTime;
    private final IFactory<T> factory;

    public CookingRecipeSerializer(IFactory<T> factory, int cookingTime) {
        this.cookingTime = cookingTime;
        this.factory = factory;
    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
        String s = JSONUtils.getString(json, "group", "");
        JsonElement jsonelement = JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient");
        Ingredient ingredient = Ingredient.deserialize(jsonelement);
        String s1 = JSONUtils.getString(json, "result");
        ResourceLocation resourcelocation = new ResourceLocation(s1);
        ItemStack itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> new IllegalStateException("Item: " + s1 + " does not exist")));
        float f = JSONUtils.getFloat(json, "experience", 0.0f);
        int i = JSONUtils.getInt(json, "cookingtime", this.cookingTime);
        return this.factory.create(recipeId, s, ingredient, itemstack, f, i);
    }

    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        String s = buffer.readString(Short.MAX_VALUE);
        Ingredient ingredient = Ingredient.read(buffer);
        ItemStack itemstack = buffer.readItemStack();
        float f = buffer.readFloat();
        int i = buffer.readVarInt();
        return this.factory.create(recipeId, s, ingredient, itemstack, f, i);
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
        buffer.writeString(((AbstractCookingRecipe)recipe).group);
        ((AbstractCookingRecipe)recipe).ingredient.write(buffer);
        buffer.writeItemStack(((AbstractCookingRecipe)recipe).result);
        buffer.writeFloat(((AbstractCookingRecipe)recipe).experience);
        buffer.writeVarInt(((AbstractCookingRecipe)recipe).cookTime);
    }

    static interface IFactory<T extends AbstractCookingRecipe> {
        public T create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4, float var5, int var6);
    }
}
