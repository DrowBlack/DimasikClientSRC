package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ThrowablePotionItem
extends PotionItem {
    public ThrowablePotionItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            PotionEntity potionentity = new PotionEntity(worldIn, playerIn);
            potionentity.setItem(itemstack);
            potionentity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0f, 0.5f, 1.0f);
            worldIn.addEntity(potionentity);
        }
        playerIn.addStat(Stats.ITEM_USED.get(this));
        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }
}
