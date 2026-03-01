package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.DamagePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EntityHurtPlayerTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        DamagePredicate damagepredicate = DamagePredicate.deserialize(json.get("damage"));
        return new Instance(entityPredicate, damagepredicate);
    }

    public void trigger(ServerPlayerEntity player, DamageSource source, float amountDealt, float amountTaken, boolean wasBlocked) {
        this.triggerListeners(player, instance -> instance.test(player, source, amountDealt, amountTaken, wasBlocked));
    }

    public static class Instance
    extends CriterionInstance {
        private final DamagePredicate damage;

        public Instance(EntityPredicate.AndPredicate player, DamagePredicate damageCondition) {
            super(ID, player);
            this.damage = damageCondition;
        }

        public static Instance forDamage(DamagePredicate.Builder damageConditionBuilder) {
            return new Instance(EntityPredicate.AndPredicate.ANY_AND, damageConditionBuilder.build());
        }

        public boolean test(ServerPlayerEntity player, DamageSource source, float amountDealt, float amountTaken, boolean wasBlocked) {
            return this.damage.test(player, source, amountDealt, amountTaken, wasBlocked);
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("damage", this.damage.serialize());
            return jsonobject;
        }
    }
}
