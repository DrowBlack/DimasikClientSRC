package net.minecraft.item;

import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;

public class DyeableHorseArmorItem
extends HorseArmorItem
implements IDyeableArmorItem {
    public DyeableHorseArmorItem(int armorValue, String p_i50047_2_, Item.Properties builder) {
        super(armorValue, p_i50047_2_, builder);
    }
}
