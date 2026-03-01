package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;

public class CustomRecipeBuilder {
    private final SpecialRecipeSerializer<?> serializer;

    public CustomRecipeBuilder(SpecialRecipeSerializer<?> p_i50786_1_) {
        this.serializer = p_i50786_1_;
    }

    public static CustomRecipeBuilder customRecipe(SpecialRecipeSerializer<?> p_218656_0_) {
        return new CustomRecipeBuilder(p_218656_0_);
    }

    public void build(Consumer<IFinishedRecipe> consumerIn, final String id) {
        consumerIn.accept(new IFinishedRecipe(){

            @Override
            public void serialize(JsonObject json) {
            }

            @Override
            public IRecipeSerializer<?> getSerializer() {
                return CustomRecipeBuilder.this.serializer;
            }

            @Override
            public ResourceLocation getID() {
                return new ResourceLocation(id);
            }

            @Override
            @Nullable
            public JsonObject getAdvancementJson() {
                return null;
            }

            @Override
            public ResourceLocation getAdvancementID() {
                return new ResourceLocation("");
            }
        });
    }
}
