package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(json.get("distance"));
        return new Instance(entityPredicate, minmaxbounds$floatbound);
    }

    public void trigger(ServerPlayerEntity player, BlockPos pos) {
        double d0 = player.getPosX() - (double)pos.getX();
        double d1 = player.getPosZ() - (double)pos.getZ();
        double d2 = d0 * d0 + d1 * d1;
        this.triggerListeners(player, instance -> instance.test(d2));
    }

    public static class Instance
    extends CriterionInstance {
        private final MinMaxBounds.FloatBound distance;

        public Instance(EntityPredicate.AndPredicate player, MinMaxBounds.FloatBound distance) {
            super(ID, player);
            this.distance = distance;
        }

        public boolean test(double distanceSq) {
            return this.distance.testSquared(distanceSq);
        }
    }
}
