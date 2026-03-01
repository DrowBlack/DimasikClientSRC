package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class PlayerGeneratesContainerLootTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("player_generates_container_loot");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "loot_table"));
        return new Instance(entityPredicate, resourcelocation);
    }

    public void test(ServerPlayerEntity player, ResourceLocation generatedLoot) {
        this.triggerListeners(player, instance -> instance.test(generatedLoot));
    }

    public static class Instance
    extends CriterionInstance {
        private final ResourceLocation generatedLoot;

        public Instance(EntityPredicate.AndPredicate player, ResourceLocation generatedLoot) {
            super(ID, player);
            this.generatedLoot = generatedLoot;
        }

        public static Instance create(ResourceLocation generatedLoot) {
            return new Instance(EntityPredicate.AndPredicate.ANY_AND, generatedLoot);
        }

        public boolean test(ResourceLocation generatedLoot) {
            return this.generatedLoot.equals(generatedLoot);
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.addProperty("loot_table", this.generatedLoot.toString());
            return jsonobject;
        }
    }
}
