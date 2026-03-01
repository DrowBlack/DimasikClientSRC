package net.minecraft.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;

public class DyeableArmorItem
extends ArmorItem
implements IDyeableArmorItem {
    public DyeableArmorItem(IArmorMaterial p_i50048_1_, EquipmentSlotType p_i50048_2_, Item.Properties p_i50048_3_) {
        super(p_i50048_1_, p_i50048_2_, p_i50048_3_);
    }
}
