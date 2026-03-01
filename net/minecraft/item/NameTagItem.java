package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class NameTagItem
extends Item {
    public NameTagItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (stack.hasDisplayName() && !(target instanceof PlayerEntity)) {
            if (!playerIn.world.isRemote && target.isAlive()) {
                target.setCustomName(stack.getDisplayName());
                if (target instanceof MobEntity) {
                    ((MobEntity)target).enablePersistence();
                }
                stack.shrink(1);
            }
            return ActionResultType.func_233537_a_(playerIn.world.isRemote);
        }
        return ActionResultType.PASS;
    }
}
