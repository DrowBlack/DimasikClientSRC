package net.optifine.config;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.IObjectLocator;
import net.optifine.util.ItemUtils;

public class ItemLocator
implements IObjectLocator<Item> {
    @Override
    public Item getObject(ResourceLocation loc) {
        return ItemUtils.getItem(loc);
    }
}
