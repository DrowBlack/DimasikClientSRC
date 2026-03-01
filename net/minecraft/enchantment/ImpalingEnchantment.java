package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EquipmentSlotType;

public class ImpalingEnchantment
extends Enchantment {
    public ImpalingEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType ... slots) {
        super(rarityIn, EnchantmentType.TRIDENT, slots);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 8;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float calcDamageByCreature(int level, CreatureAttribute creatureType) {
        return creatureType == CreatureAttribute.WATER ? (float)level * 2.5f : 0.0f;
    }
}
