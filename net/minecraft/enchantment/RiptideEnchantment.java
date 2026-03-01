package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;

public class RiptideEnchantment
extends Enchantment {
    public RiptideEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType ... slots) {
        super(rarityIn, EnchantmentType.TRIDENT, slots);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 10 + enchantmentLevel * 7;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        return super.canApplyTogether(ench) && ench != Enchantments.LOYALTY && ench != Enchantments.CHANNELING;
    }
}
