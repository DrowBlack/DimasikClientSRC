package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class SpecialRecipeSerializer<T extends IRecipe<?>>
implements IRecipeSerializer<T> {
    private final Function<ResourceLocation, T> field_222176_t;

    public SpecialRecipeSerializer(Function<ResourceLocation, T> p_i50024_1_) {
        this.field_222176_t = p_i50024_1_;
    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
        return (T)((IRecipe)this.field_222176_t.apply(recipeId));
    }

    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        return (T)((IRecipe)this.field_222176_t.apply(recipeId));
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
    }
}
