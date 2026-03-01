package net.minecraft.loot;

import net.minecraft.loot.functions.ILootFunction;

public interface ILootFunctionConsumer<T> {
    public T acceptFunction(ILootFunction.IBuilder var1);

    public T cast();
}
