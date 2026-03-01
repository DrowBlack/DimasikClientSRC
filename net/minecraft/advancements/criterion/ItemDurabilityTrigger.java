package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class ItemDurabilityTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("durability"));
        MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(json.get("delta"));
        return new Instance(entityPredicate, itempredicate, minmaxbounds$intbound, minmaxbounds$intbound1);
    }

    public void trigger(ServerPlayerEntity player, ItemStack itemIn, int newDurability) {
        this.triggerListeners(player, instance -> instance.test(itemIn, newDurability));
    }

    public static class Instance
    extends CriterionInstance {
        private final ItemPredicate item;
        private final MinMaxBounds.IntBound durability;
        private final MinMaxBounds.IntBound delta;

        public Instance(EntityPredicate.AndPredicate player, ItemPredicate item, MinMaxBounds.IntBound durability, MinMaxBounds.IntBound delta) {
            super(ID, player);
            this.item = item;
            this.durability = durability;
            this.delta = delta;
        }

        public static Instance create(EntityPredicate.AndPredicate player, ItemPredicate item, MinMaxBounds.IntBound durability) {
            return new Instance(player, item, durability, MinMaxBounds.IntBound.UNBOUNDED);
        }

        public boolean test(ItemStack item, int durability) {
            if (!this.item.test(item)) {
                return false;
            }
            if (!this.durability.test(item.getMaxDamage() - durability)) {
                return false;
            }
            return this.delta.test(item.getDamage() - durability);
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("item", this.item.serialize());
            jsonobject.add("durability", this.durability.serialize());
            jsonobject.add("delta", this.delta.serialize());
            return jsonobject;
        }
    }
}
