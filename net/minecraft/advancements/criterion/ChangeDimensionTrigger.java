package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ChangeDimensionTrigger
extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = new ResourceLocation("changed_dimension");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        RegistryKey<World> registrykey = json.has("from") ? RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(JSONUtils.getString(json, "from"))) : null;
        RegistryKey<World> registrykey1 = json.has("to") ? RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(JSONUtils.getString(json, "to"))) : null;
        return new Instance(entityPredicate, registrykey, registrykey1);
    }

    public void testForAll(ServerPlayerEntity player, RegistryKey<World> fromWorld, RegistryKey<World> toWorld) {
        this.triggerListeners(player, instance -> instance.test(fromWorld, toWorld));
    }

    public static class Instance
    extends CriterionInstance {
        @Nullable
        private final RegistryKey<World> from;
        @Nullable
        private final RegistryKey<World> to;

        public Instance(EntityPredicate.AndPredicate entityPredicate, @Nullable RegistryKey<World> fromWorld, @Nullable RegistryKey<World> toWorld) {
            super(ID, entityPredicate);
            this.from = fromWorld;
            this.to = toWorld;
        }

        public static Instance toWorld(RegistryKey<World> toWorld) {
            return new Instance(EntityPredicate.AndPredicate.ANY_AND, null, toWorld);
        }

        public boolean test(RegistryKey<World> fromWorld, RegistryKey<World> toWorld) {
            if (this.from != null && this.from != fromWorld) {
                return false;
            }
            return this.to == null || this.to == toWorld;
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            if (this.from != null) {
                jsonobject.addProperty("from", this.from.getLocation().toString());
            }
            if (this.to != null) {
                jsonobject.addProperty("to", this.to.getLocation().toString());
            }
            return jsonobject;
        }
    }
}
