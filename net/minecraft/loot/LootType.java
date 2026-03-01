package net.minecraft.loot;

import net.minecraft.loot.ILootSerializer;

public class LootType<T> {
    private final ILootSerializer<? extends T> serializer;

    public LootType(ILootSerializer<? extends T> serializer) {
        this.serializer = serializer;
    }

    public ILootSerializer<? extends T> getSerializer() {
        return this.serializer;
    }
}
