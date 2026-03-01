package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.IntClamper;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.JSONUtils;

public class LimitCount
extends LootFunction {
    private final IntClamper field_215914_a;

    private LimitCount(ILootCondition[] p_i51232_1_, IntClamper p_i51232_2_) {
        super(p_i51232_1_);
        this.field_215914_a = p_i51232_2_;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return LootFunctionManager.LIMIT_COUNT;
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        int i = this.field_215914_a.applyAsInt(stack.getCount());
        stack.setCount(i);
        return stack;
    }

    public static LootFunction.Builder<?> func_215911_a(IntClamper p_215911_0_) {
        return LimitCount.builder(p_215912_1_ -> new LimitCount((ILootCondition[])p_215912_1_, p_215911_0_));
    }

    public static class Serializer
    extends LootFunction.Serializer<LimitCount> {
        @Override
        public void serialize(JsonObject p_230424_1_, LimitCount p_230424_2_, JsonSerializationContext p_230424_3_) {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            p_230424_1_.add("limit", p_230424_3_.serialize(p_230424_2_.field_215914_a));
        }

        @Override
        public LimitCount deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            IntClamper intclamper = JSONUtils.deserializeClass(object, "limit", deserializationContext, IntClamper.class);
            return new LimitCount(conditionsIn, intclamper);
        }
    }
}
