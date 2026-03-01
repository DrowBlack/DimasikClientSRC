package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BrewedPotionTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("brewed_potion");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        Potion potion = null;
        if (json.has("potion")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "potion"));
            potion = Registry.POTION.getOptional(resourcelocation).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + String.valueOf(resourcelocation) + "'"));
        }
        return new Instance(entityPredicate, potion);
    }

    public void trigger(ServerPlayerEntity player, Potion potionIn) {
        this.triggerListeners(player, instance -> instance.test(potionIn));
    }

    public static class Instance
    extends CriterionInstance {
        private final Potion potion;

        public Instance(EntityPredicate.AndPredicate player, @Nullable Potion potion) {
            super(ID, player);
            this.potion = potion;
        }

        public static Instance brewedPotion() {
            return new Instance(EntityPredicate.AndPredicate.ANY_AND, (Potion)null);
        }

        public boolean test(Potion potion) {
            return this.potion == null || this.potion == potion;
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            if (this.potion != null) {
                jsonobject.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
            }
            return jsonobject;
        }
    }
}
