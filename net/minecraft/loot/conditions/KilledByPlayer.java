package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;

public class KilledByPlayer
implements ILootCondition {
    private static final KilledByPlayer INSTANCE = new KilledByPlayer();

    private KilledByPlayer() {
    }

    @Override
    public LootConditionType func_230419_b_() {
        return LootConditionManager.KILLED_BY_PLAYER;
    }

    @Override
    public Set<LootParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootParameters.LAST_DAMAGE_PLAYER);
    }

    @Override
    public boolean test(LootContext p_test_1_) {
        return p_test_1_.has(LootParameters.LAST_DAMAGE_PLAYER);
    }

    public static ILootCondition.IBuilder builder() {
        return () -> INSTANCE;
    }

    public static class Serializer
    implements ILootSerializer<KilledByPlayer> {
        @Override
        public void serialize(JsonObject p_230424_1_, KilledByPlayer p_230424_2_, JsonSerializationContext p_230424_3_) {
        }

        @Override
        public KilledByPlayer deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
            return INSTANCE;
        }
    }
}
