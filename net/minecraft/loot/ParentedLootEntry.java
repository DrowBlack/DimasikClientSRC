package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.loot.ILootEntry;
import net.minecraft.loot.ILootGenerator;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;

public abstract class ParentedLootEntry
extends LootEntry {
    protected final LootEntry[] entries;
    private final ILootEntry children;

    protected ParentedLootEntry(LootEntry[] entries, ILootCondition[] conditions) {
        super(conditions);
        this.entries = entries;
        this.children = this.combineChildren(entries);
    }

    @Override
    public void func_225579_a_(ValidationTracker p_225579_1_) {
        super.func_225579_a_(p_225579_1_);
        if (this.entries.length == 0) {
            p_225579_1_.addProblem("Empty children list");
        }
        for (int i = 0; i < this.entries.length; ++i) {
            this.entries[i].func_225579_a_(p_225579_1_.func_227534_b_(".entry[" + i + "]"));
        }
    }

    protected abstract ILootEntry combineChildren(ILootEntry[] var1);

    @Override
    public final boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_) {
        return !this.test(p_expand_1_) ? false : this.children.expand(p_expand_1_, p_expand_2_);
    }

    public static <T extends ParentedLootEntry> LootEntry.Serializer<T> getSerializer(final IFactory<T> factory) {
        return new LootEntry.Serializer<T>(){

            @Override
            public void doSerialize(JsonObject object, T context, JsonSerializationContext conditions) {
                object.add("children", conditions.serialize(((ParentedLootEntry)context).entries));
            }

            @Override
            public final T deserialize(JsonObject object, JsonDeserializationContext context, ILootCondition[] conditions) {
                LootEntry[] alootentry = JSONUtils.deserializeClass(object, "children", context, LootEntry[].class);
                return factory.create(alootentry, conditions);
            }
        };
    }

    @FunctionalInterface
    public static interface IFactory<T extends ParentedLootEntry> {
        public T create(LootEntry[] var1, ILootCondition[] var2);
    }
}
