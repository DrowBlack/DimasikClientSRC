package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;

public class OperatorOnlyItem
extends BlockItem {
    public OperatorOnlyItem(Block blockIn, Item.Properties builder) {
        super(blockIn, builder);
    }

    @Override
    @Nullable
    protected BlockState getStateForPlacement(BlockItemUseContext context) {
        PlayerEntity playerentity = context.getPlayer();
        return playerentity != null && !playerentity.canUseCommandBlock() ? null : super.getStateForPlacement(context);
    }
}
