package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BlockNamedItem
extends BlockItem {
    public BlockNamedItem(Block blockIn, Item.Properties properties) {
        super(blockIn, properties);
    }

    @Override
    public String getTranslationKey() {
        return this.getDefaultTranslationKey();
    }
}
