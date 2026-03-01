package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;

public class MatchTool
implements ILootCondition {
    private final ItemPredicate predicate;

    public MatchTool(ItemPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public LootConditionType func_230419_b_() {
        return LootConditionManager.MATCH_TOOL;
    }

    @Override
    public Set<LootParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootParameters.TOOL);
    }

    @Override
    public boolean test(LootContext p_test_1_) {
        ItemStack itemstack = p_test_1_.get(LootParameters.TOOL);
        return itemstack != null && this.predicate.test(itemstack);
    }

    public static ILootCondition.IBuilder builder(ItemPredicate.Builder p_216012_0_) {
        return () -> new MatchTool(p_216012_0_.build());
    }

    public static class Serializer
    implements ILootSerializer<MatchTool> {
        @Override
        public void serialize(JsonObject p_230424_1_, MatchTool p_230424_2_, JsonSerializationContext p_230424_3_) {
            p_230424_1_.add("predicate", p_230424_2_.predicate.serialize());
        }

        @Override
        public MatchTool deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
            ItemPredicate itempredicate = ItemPredicate.deserialize(p_230423_1_.get("predicate"));
            return new MatchTool(itempredicate);
        }
    }
}
