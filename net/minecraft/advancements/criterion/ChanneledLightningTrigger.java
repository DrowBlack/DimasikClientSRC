package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class ChanneledLightningTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        EntityPredicate.AndPredicate[] aentitypredicate$andpredicate = EntityPredicate.AndPredicate.deserialize(json, "victims", conditionsParser);
        return new Instance(entityPredicate, aentitypredicate$andpredicate);
    }

    public void trigger(ServerPlayerEntity player, Collection<? extends Entity> entityTriggered) {
        List list = entityTriggered.stream().map(entity -> EntityPredicate.getLootContext(player, entity)).collect(Collectors.toList());
        this.triggerListeners(player, instance -> instance.test(list));
    }

    public static class Instance
    extends CriterionInstance {
        private final EntityPredicate.AndPredicate[] victims;

        public Instance(EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate[] victims) {
            super(ID, player);
            this.victims = victims;
        }

        public static Instance channeledLightning(EntityPredicate ... victims) {
            return new Instance(EntityPredicate.AndPredicate.ANY_AND, (EntityPredicate.AndPredicate[])Stream.of(victims).map(EntityPredicate.AndPredicate::createAndFromEntityCondition).toArray(EntityPredicate.AndPredicate[]::new));
        }

        public boolean test(Collection<? extends LootContext> victims) {
            for (EntityPredicate.AndPredicate entitypredicate$andpredicate : this.victims) {
                boolean flag = false;
                for (LootContext lootContext : victims) {
                    if (!entitypredicate$andpredicate.testContext(lootContext)) continue;
                    flag = true;
                    break;
                }
                if (flag) continue;
                return false;
            }
            return true;
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("victims", EntityPredicate.AndPredicate.serializeConditionsIn(this.victims, conditions));
            return jsonobject;
        }
    }
}
