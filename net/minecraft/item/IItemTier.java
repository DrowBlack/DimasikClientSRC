package net.minecraft.item;

import net.minecraft.item.crafting.Ingredient;

public interface IItemTier {
    public int getMaxUses();

    public float getEfficiency();

    public float getAttackDamage();

    public int getHarvestLevel();

    public int getEnchantability();

    public Ingredient getRepairMaterial();
}
