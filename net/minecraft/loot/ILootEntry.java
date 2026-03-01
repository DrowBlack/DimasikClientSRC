package net.minecraft.loot;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.loot.ILootGenerator;
import net.minecraft.loot.LootContext;

@FunctionalInterface
interface ILootEntry {
    public static final ILootEntry field_216139_a = (p_216134_0_, p_216134_1_) -> false;
    public static final ILootEntry field_216140_b = (p_216136_0_, p_216136_1_) -> true;

    public boolean expand(LootContext var1, Consumer<ILootGenerator> var2);

    default public ILootEntry sequence(ILootEntry entry) {
        Objects.requireNonNull(entry);
        return (p_216137_2_, p_216137_3_) -> this.expand(p_216137_2_, p_216137_3_) && entry.expand(p_216137_2_, p_216137_3_);
    }

    default public ILootEntry alternate(ILootEntry entry) {
        Objects.requireNonNull(entry);
        return (p_216138_2_, p_216138_3_) -> this.expand(p_216138_2_, p_216138_3_) || entry.expand(p_216138_2_, p_216138_3_);
    }
}
