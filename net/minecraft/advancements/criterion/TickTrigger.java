package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public class TickTrigger
extends AbstractCriterionTrigger<Instance> {
    public static final ResourceLocation ID = new ResourceLocation("tick");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return new Instance(entityPredicate);
    }

    public void trigger(ServerPlayerEntity player) {
        this.triggerListeners(player, instance -> true);
    }

    public static class Instance
    extends CriterionInstance {
        public Instance(EntityPredicate.AndPredicate player) {
            super(ID, player);
        }
    }
}
