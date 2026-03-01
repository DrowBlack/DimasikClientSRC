package net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BookItem
extends Item {
    public BookItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }
}
