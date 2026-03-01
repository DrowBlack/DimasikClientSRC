package net.minecraft.loot.functions;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.IParameterized;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunctionType;

public interface ILootFunction
extends IParameterized,
BiFunction<ItemStack, LootContext, ItemStack> {
    public LootFunctionType getFunctionType();

    public static Consumer<ItemStack> func_215858_a(BiFunction<ItemStack, LootContext, ItemStack> p_215858_0_, Consumer<ItemStack> stackConsumer, LootContext context) {
        return stack -> stackConsumer.accept((ItemStack)p_215858_0_.apply((ItemStack)stack, context));
    }

    public static interface IBuilder {
        public ILootFunction build();
    }
}
