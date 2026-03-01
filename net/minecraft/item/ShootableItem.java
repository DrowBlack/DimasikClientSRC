package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;

public abstract class ShootableItem
extends Item {
    public static final Predicate<ItemStack> ARROWS = stack -> stack.getItem().isIn(ItemTags.ARROWS);
    public static final Predicate<ItemStack> ARROWS_OR_FIREWORKS = ARROWS.or(stack -> stack.getItem() == Items.FIREWORK_ROCKET);

    public ShootableItem(Item.Properties builder) {
        super(builder);
    }

    public Predicate<ItemStack> getAmmoPredicate() {
        return this.getInventoryAmmoPredicate();
    }

    public abstract Predicate<ItemStack> getInventoryAmmoPredicate();

    public static ItemStack getHeldAmmo(LivingEntity living, Predicate<ItemStack> isAmmo) {
        if (isAmmo.test(living.getHeldItem(Hand.OFF_HAND))) {
            return living.getHeldItem(Hand.OFF_HAND);
        }
        return isAmmo.test(living.getHeldItem(Hand.MAIN_HAND)) ? living.getHeldItem(Hand.MAIN_HAND) : ItemStack.EMPTY;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    public abstract int func_230305_d_();
}
