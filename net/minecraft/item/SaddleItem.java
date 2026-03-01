package net.minecraft.item;

import net.minecraft.entity.IEquipable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;

public class SaddleItem
extends Item {
    public SaddleItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        IEquipable iequipable;
        if (target instanceof IEquipable && target.isAlive() && !(iequipable = (IEquipable)((Object)target)).isHorseSaddled() && iequipable.func_230264_L__()) {
            if (!playerIn.world.isRemote) {
                iequipable.func_230266_a_(SoundCategory.NEUTRAL);
                stack.shrink(1);
            }
            return ActionResultType.func_233537_a_(playerIn.world.isRemote);
        }
        return ActionResultType.PASS;
    }
}
