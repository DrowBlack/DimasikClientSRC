package net.minecraft.loot;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootType;

public class LootPoolEntryType
extends LootType<LootEntry> {
    public LootPoolEntryType(ILootSerializer<? extends LootEntry> p_i232168_1_) {
        super(p_i232168_1_);
    }
}
