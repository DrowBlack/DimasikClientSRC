package net.minecraft.loot;

import net.minecraft.loot.conditions.ILootCondition;

public interface ILootConditionConsumer<T> {
    public T acceptCondition(ILootCondition.IBuilder var1);

    public T cast();
}
