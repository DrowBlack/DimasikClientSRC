package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootConditionConsumer;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootFunction
implements ILootFunction {
    protected final ILootCondition[] conditions;
    private final Predicate<LootContext> combinedConditions;

    protected LootFunction(ILootCondition[] conditionsIn) {
        this.conditions = conditionsIn;
        this.combinedConditions = LootConditionManager.and(conditionsIn);
    }

    @Override
    public final ItemStack apply(ItemStack p_apply_1_, LootContext p_apply_2_) {
        return this.combinedConditions.test(p_apply_2_) ? this.doApply(p_apply_1_, p_apply_2_) : p_apply_1_;
    }

    protected abstract ItemStack doApply(ItemStack var1, LootContext var2);

    @Override
    public void func_225580_a_(ValidationTracker p_225580_1_) {
        ILootFunction.super.func_225580_a_(p_225580_1_);
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].func_225580_a_(p_225580_1_.func_227534_b_(".conditions[" + i + "]"));
        }
    }

    protected static Builder<?> builder(Function<ILootCondition[], ILootFunction> p_215860_0_) {
        return new SimpleBuilder(p_215860_0_);
    }

    static final class SimpleBuilder
    extends Builder<SimpleBuilder> {
        private final Function<ILootCondition[], ILootFunction> function;

        public SimpleBuilder(Function<ILootCondition[], ILootFunction> p_i50229_1_) {
            this.function = p_i50229_1_;
        }

        @Override
        protected SimpleBuilder doCast() {
            return this;
        }

        @Override
        public ILootFunction build() {
            return this.function.apply(this.getConditions());
        }
    }

    public static abstract class Serializer<T extends LootFunction>
    implements ILootSerializer<T> {
        @Override
        public void serialize(JsonObject p_230424_1_, T p_230424_2_, JsonSerializationContext p_230424_3_) {
            if (!ArrayUtils.isEmpty(((LootFunction)p_230424_2_).conditions)) {
                p_230424_1_.add("conditions", p_230424_3_.serialize(((LootFunction)p_230424_2_).conditions));
            }
        }

        @Override
        public final T deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
            ILootCondition[] ailootcondition = JSONUtils.deserializeClass(p_230423_1_, "conditions", new ILootCondition[0], p_230423_2_, ILootCondition[].class);
            return this.deserialize(p_230423_1_, p_230423_2_, ailootcondition);
        }

        public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, ILootCondition[] var3);
    }

    public static abstract class Builder<T extends Builder<T>>
    implements ILootFunction.IBuilder,
    ILootConditionConsumer<T> {
        private final List<ILootCondition> conditions = Lists.newArrayList();

        @Override
        public T acceptCondition(ILootCondition.IBuilder conditionBuilder) {
            this.conditions.add(conditionBuilder.build());
            return this.doCast();
        }

        @Override
        public final T cast() {
            return this.doCast();
        }

        protected abstract T doCast();

        protected ILootCondition[] getConditions() {
            return this.conditions.toArray(new ILootCondition[0]);
        }
    }
}
