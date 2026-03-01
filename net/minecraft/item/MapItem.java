package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AbstractMapItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MapItem
extends AbstractMapItem {
    public MapItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = FilledMapItem.setupNewMap(worldIn, MathHelper.floor(playerIn.getPosX()), MathHelper.floor(playerIn.getPosZ()), (byte)0, true, false);
        ItemStack itemstack1 = playerIn.getHeldItem(handIn);
        if (!playerIn.abilities.isCreativeMode) {
            itemstack1.shrink(1);
        }
        playerIn.addStat(Stats.ITEM_USED.get(this));
        playerIn.playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0f, 1.0f);
        if (itemstack1.isEmpty()) {
            return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
        }
        if (!playerIn.inventory.addItemStackToInventory(itemstack.copy())) {
            playerIn.dropItem(itemstack, false);
        }
        return ActionResult.func_233538_a_(itemstack1, worldIn.isRemote());
    }
}
