package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class FilledBucketTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("filled_bucket");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        return new Instance(entityPredicate, itempredicate);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.triggerListeners(player, instance -> instance.test(stack));
    }

    public static class Instance
    extends CriterionInstance {
        private final ItemPredicate item;

        public Instance(EntityPredicate.AndPredicate player, ItemPredicate itemCondition) {
            super(ID, player);
            this.item = itemCondition;
        }

        public static Instance forItem(ItemPredicate itemCondition) {
            return new Instance(EntityPredicate.AndPredicate.ANY_AND, itemCondition);
        }

        public boolean test(ItemStack stack) {
            return this.item.test(stack);
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("item", this.item.serialize());
            return jsonobject;
        }
    }
}
