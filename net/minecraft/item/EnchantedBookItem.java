package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class EnchantedBookItem
extends Item {
    public EnchantedBookItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public static ListNBT getEnchantments(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getTag();
        return compoundnbt != null ? compoundnbt.getList("StoredEnchantments", 10) : new ListNBT();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        ItemStack.addEnchantmentTooltips(tooltip, EnchantedBookItem.getEnchantments(stack));
    }

    public static void addEnchantment(ItemStack p_92115_0_, EnchantmentData stack) {
        ListNBT listnbt = EnchantedBookItem.getEnchantments(p_92115_0_);
        boolean flag = true;
        ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(stack.enchantment);
        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            ResourceLocation resourcelocation1 = ResourceLocation.tryCreate(compoundnbt.getString("id"));
            if (resourcelocation1 == null || !resourcelocation1.equals(resourcelocation)) continue;
            if (compoundnbt.getInt("lvl") < stack.enchantmentLevel) {
                compoundnbt.putShort("lvl", (short)stack.enchantmentLevel);
            }
            flag = false;
            break;
        }
        if (flag) {
            CompoundNBT compoundnbt1 = new CompoundNBT();
            compoundnbt1.putString("id", String.valueOf(resourcelocation));
            compoundnbt1.putShort("lvl", (short)stack.enchantmentLevel);
            listnbt.add(compoundnbt1);
        }
        p_92115_0_.getOrCreateTag().put("StoredEnchantments", listnbt);
    }

    public static ItemStack getEnchantedItemStack(EnchantmentData enchantData) {
        ItemStack itemstack = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(itemstack, enchantData);
        return itemstack;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        block4: {
            block3: {
                if (group != ItemGroup.SEARCH) break block3;
                for (Enchantment enchantment : Registry.ENCHANTMENT) {
                    if (enchantment.type == null) continue;
                    for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
                        items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(enchantment, i)));
                    }
                }
                break block4;
            }
            if (group.getRelevantEnchantmentTypes().length == 0) break block4;
            for (Enchantment enchantment1 : Registry.ENCHANTMENT) {
                if (!group.hasRelevantEnchantmentType(enchantment1.type)) continue;
                items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(enchantment1, enchantment1.getMaxLevel())));
            }
        }
    }
}
