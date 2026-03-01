package net.minecraft.loot;

import net.minecraft.loot.ILootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootEntryManager;
import net.minecraft.loot.LootPoolEntryType;
import net.minecraft.loot.ParentedLootEntry;
import net.minecraft.loot.conditions.ILootCondition;

public class SequenceLootEntry
extends ParentedLootEntry {
    SequenceLootEntry(LootEntry[] children, ILootCondition[] conditions) {
        super(children, conditions);
    }

    @Override
    public LootPoolEntryType func_230420_a_() {
        return LootEntryManager.SEQUENCE;
    }

    @Override
    protected ILootEntry combineChildren(ILootEntry[] entries) {
        switch (entries.length) {
            case 0: {
                return field_216140_b;
            }
            case 1: {
                return entries[0];
            }
            case 2: {
                return entries[0].sequence(entries[1]);
            }
        }
        return (context, generatorConsumer) -> {
            for (ILootEntry ilootentry : entries) {
                if (ilootentry.expand(context, generatorConsumer)) continue;
                return false;
            }
            return true;
        };
    }
}
