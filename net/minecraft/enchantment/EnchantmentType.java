package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum EnchantmentType {
    ARMOR{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ArmorItem;
        }
    }
    ,
    ARMOR_FEET{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ArmorItem && ((ArmorItem)itemIn).getEquipmentSlot() == EquipmentSlotType.FEET;
        }
    }
    ,
    ARMOR_LEGS{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ArmorItem && ((ArmorItem)itemIn).getEquipmentSlot() == EquipmentSlotType.LEGS;
        }
    }
    ,
    ARMOR_CHEST{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ArmorItem && ((ArmorItem)itemIn).getEquipmentSlot() == EquipmentSlotType.CHEST;
        }
    }
    ,
    ARMOR_HEAD{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ArmorItem && ((ArmorItem)itemIn).getEquipmentSlot() == EquipmentSlotType.HEAD;
        }
    }
    ,
    WEAPON{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof SwordItem;
        }
    }
    ,
    DIGGER{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ToolItem;
        }
    }
    ,
    FISHING_ROD{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof FishingRodItem;
        }
    }
    ,
    TRIDENT{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof TridentItem;
        }
    }
    ,
    BREAKABLE{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn.isDamageable();
        }
    }
    ,
    BOW{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof BowItem;
        }
    }
    ,
    WEARABLE{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof IArmorVanishable || Block.getBlockFromItem(itemIn) instanceof IArmorVanishable;
        }
    }
    ,
    CROSSBOW{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof CrossbowItem;
        }
    }
    ,
    VANISHABLE{

        @Override
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof IVanishable || Block.getBlockFromItem(itemIn) instanceof IVanishable || BREAKABLE.canEnchantItem(itemIn);
        }
    };


    public abstract boolean canEnchantItem(Item var1);
}
