package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class UnbreakingEnchantment
extends Enchantment {
    protected UnbreakingEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType ... slots) {
        super(rarityIn, EnchantmentType.BREAKABLE, slots);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 5 + (enchantmentLevel - 1) * 8;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.isDamageable() ? true : super.canApply(stack);
    }

    public static boolean negateDamage(ItemStack stack, int level, Random rand) {
        if (stack.getItem() instanceof ArmorItem && rand.nextFloat() < 0.6f) {
            return false;
        }
        return rand.nextInt(level + 1) > 0;
    }
}
