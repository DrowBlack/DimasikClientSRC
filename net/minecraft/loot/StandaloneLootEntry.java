package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootFunctionConsumer;
import net.minecraft.loot.ILootGenerator;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class StandaloneLootEntry
extends LootEntry {
    protected final int weight;
    protected final int quality;
    protected final ILootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;
    private final ILootGenerator generator = new Generator(){

        @Override
        public void func_216188_a(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_) {
            StandaloneLootEntry.this.func_216154_a(ILootFunction.func_215858_a(StandaloneLootEntry.this.combinedFunctions, p_216188_1_, p_216188_2_), p_216188_2_);
        }
    };

    protected StandaloneLootEntry(int weightIn, int qualityIn, ILootCondition[] conditionsIn, ILootFunction[] functionsIn) {
        super(conditionsIn);
        this.weight = weightIn;
        this.quality = qualityIn;
        this.functions = functionsIn;
        this.combinedFunctions = LootFunctionManager.combine(functionsIn);
    }

    @Override
    public void func_225579_a_(ValidationTracker p_225579_1_) {
        super.func_225579_a_(p_225579_1_);
        for (int i = 0; i < this.functions.length; ++i) {
            this.functions[i].func_225580_a_(p_225579_1_.func_227534_b_(".functions[" + i + "]"));
        }
    }

    protected abstract void func_216154_a(Consumer<ItemStack> var1, LootContext var2);

    @Override
    public boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_) {
        if (this.test(p_expand_1_)) {
            p_expand_2_.accept(this.generator);
            return true;
        }
        return false;
    }

    public static Builder<?> builder(ILootEntryBuilder entryBuilderIn) {
        return new BuilderImpl(entryBuilderIn);
    }

    static class BuilderImpl
    extends Builder<BuilderImpl> {
        private final ILootEntryBuilder builder;

        public BuilderImpl(ILootEntryBuilder builder) {
            this.builder = builder;
        }

        @Override
        protected BuilderImpl func_212845_d_() {
            return this;
        }

        @Override
        public LootEntry build() {
            return this.builder.build(this.weight, this.quality, this.func_216079_f(), this.getFunctions());
        }
    }

    @FunctionalInterface
    public static interface ILootEntryBuilder {
        public StandaloneLootEntry build(int var1, int var2, ILootCondition[] var3, ILootFunction[] var4);
    }

    public static abstract class Serializer<T extends StandaloneLootEntry>
    extends LootEntry.Serializer<T> {
        @Override
        public void doSerialize(JsonObject object, T context, JsonSerializationContext conditions) {
            if (((StandaloneLootEntry)context).weight != 1) {
                object.addProperty("weight", ((StandaloneLootEntry)context).weight);
            }
            if (((StandaloneLootEntry)context).quality != 0) {
                object.addProperty("quality", ((StandaloneLootEntry)context).quality);
            }
            if (!ArrayUtils.isEmpty(((StandaloneLootEntry)context).functions)) {
                object.add("functions", conditions.serialize(((StandaloneLootEntry)context).functions));
            }
        }

        @Override
        public final T deserialize(JsonObject object, JsonDeserializationContext context, ILootCondition[] conditions) {
            int i = JSONUtils.getInt(object, "weight", 1);
            int j = JSONUtils.getInt(object, "quality", 0);
            ILootFunction[] ailootfunction = JSONUtils.deserializeClass(object, "functions", new ILootFunction[0], context, ILootFunction[].class);
            return this.deserialize(object, context, i, j, conditions, ailootfunction);
        }

        protected abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, ILootCondition[] var5, ILootFunction[] var6);
    }

    public abstract class Generator
    implements ILootGenerator {
        protected Generator() {
        }

        @Override
        public int getEffectiveWeight(float luck) {
            return Math.max(MathHelper.floor((float)StandaloneLootEntry.this.weight + (float)StandaloneLootEntry.this.quality * luck), 0);
        }
    }

    public static abstract class Builder<T extends Builder<T>>
    extends LootEntry.Builder<T>
    implements ILootFunctionConsumer<T> {
        protected int weight = 1;
        protected int quality = 0;
        private final List<ILootFunction> functions = Lists.newArrayList();

        @Override
        public T acceptFunction(ILootFunction.IBuilder functionBuilder) {
            this.functions.add(functionBuilder.build());
            return (T)((Builder)this.func_212845_d_());
        }

        protected ILootFunction[] getFunctions() {
            return this.functions.toArray(new ILootFunction[0]);
        }

        public T weight(int weightIn) {
            this.weight = weightIn;
            return (T)((Builder)this.func_212845_d_());
        }

        public T quality(int qualityIn) {
            this.quality = qualityIn;
            return (T)((Builder)this.func_212845_d_());
        }
    }
}
