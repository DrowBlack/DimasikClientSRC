package net.minecraft.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.server.ServerWorld;

public class TimeCheck
implements ILootCondition {
    @Nullable
    private final Long field_227570_a_;
    private final RandomValueRange field_227571_b_;

    private TimeCheck(@Nullable Long p_i225898_1_, RandomValueRange p_i225898_2_) {
        this.field_227570_a_ = p_i225898_1_;
        this.field_227571_b_ = p_i225898_2_;
    }

    @Override
    public LootConditionType func_230419_b_() {
        return LootConditionManager.TIME_CHECK;
    }

    @Override
    public boolean test(LootContext p_test_1_) {
        ServerWorld serverworld = p_test_1_.getWorld();
        long i = serverworld.getDayTime();
        if (this.field_227570_a_ != null) {
            i %= this.field_227570_a_.longValue();
        }
        return this.field_227571_b_.isInRange((int)i);
    }

    public static class Serializer
    implements ILootSerializer<TimeCheck> {
        @Override
        public void serialize(JsonObject p_230424_1_, TimeCheck p_230424_2_, JsonSerializationContext p_230424_3_) {
            p_230424_1_.addProperty("period", p_230424_2_.field_227570_a_);
            p_230424_1_.add("value", p_230424_3_.serialize(p_230424_2_.field_227571_b_));
        }

        @Override
        public TimeCheck deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
            Long olong = p_230423_1_.has("period") ? Long.valueOf(JSONUtils.getLong(p_230423_1_, "period")) : null;
            RandomValueRange randomvaluerange = JSONUtils.deserializeClass(p_230423_1_, "value", p_230423_2_, RandomValueRange.class);
            return new TimeCheck(olong, randomvaluerange);
        }
    }
}
