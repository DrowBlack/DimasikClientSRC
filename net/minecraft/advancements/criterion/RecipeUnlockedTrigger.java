package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeUnlockedTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "recipe"));
        return new Instance(entityPredicate, resourcelocation);
    }

    public void trigger(ServerPlayerEntity player, IRecipe<?> recipe) {
        this.triggerListeners(player, instance -> instance.test(recipe));
    }

    public static Instance create(ResourceLocation recipeID) {
        return new Instance(EntityPredicate.AndPredicate.ANY_AND, recipeID);
    }

    public static class Instance
    extends CriterionInstance {
        private final ResourceLocation recipe;

        public Instance(EntityPredicate.AndPredicate player, ResourceLocation recipeID) {
            super(ID, player);
            this.recipe = recipeID;
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.addProperty("recipe", this.recipe.toString());
            return jsonobject;
        }

        public boolean test(IRecipe<?> recipe) {
            return this.recipe.equals(recipe.getId());
        }
    }
}
