package net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SimpleFoiledItem
extends Item {
    public SimpleFoiledItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
