package net.minecraft.item;

import dimasik.Load;
import dimasik.modules.player.ItemsCooldown;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class EnderPearlItem
extends Item {
    public EnderPearlItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        boolean isPvp;
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (random.nextFloat() * 0.4f + 0.8f));
        ItemsCooldown cooldown = Load.getInstance().getHooks().getModuleManagers().getItemsCooldown();
        ItemsCooldown.ItemEnum itemEnum = ItemsCooldown.ItemEnum.getItemEnum(this);
        boolean bl = isPvp = cooldown.isPvpMode() || (Boolean)cooldown.getOnlyPvp().getValue() == false;
        if (cooldown.isToggled() && itemEnum != null && cooldown.isCurrentItem(itemEnum) && isPvp) {
            cooldown.lastUseItemTime.put(itemEnum.getItem(), System.currentTimeMillis());
        } else {
            playerIn.getCooldownTracker().setCooldown(this, 20);
        }
        if (!worldIn.isRemote) {
            EnderPearlEntity enderpearlentity = new EnderPearlEntity(worldIn, playerIn);
            enderpearlentity.setItem(itemstack);
            enderpearlentity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0f, 1.5f, 1.0f);
            worldIn.addEntity(enderpearlentity);
        }
        playerIn.addStat(Stats.ITEM_USED.get(this));
        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }
}
