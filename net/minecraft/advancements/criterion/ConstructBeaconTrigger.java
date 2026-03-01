package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.ResourceLocation;

public class ConstructBeaconTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("construct_beacon");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("level"));
        return new Instance(entityPredicate, minmaxbounds$intbound);
    }

    public void trigger(ServerPlayerEntity player, BeaconTileEntity beacon) {
        this.triggerListeners(player, instance -> instance.test(beacon));
    }

    public static class Instance
    extends CriterionInstance {
        private final MinMaxBounds.IntBound level;

        public Instance(EntityPredicate.AndPredicate player, MinMaxBounds.IntBound level) {
            super(ID, player);
            this.level = level;
        }

        public static Instance forLevel(MinMaxBounds.IntBound level) {
            return new Instance(EntityPredicate.AndPredicate.ANY_AND, level);
        }

        public boolean test(BeaconTileEntity beacon) {
            return this.level.test(beacon.getLevels());
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("level", this.level.serialize());
            return jsonobject;
        }
    }
}
