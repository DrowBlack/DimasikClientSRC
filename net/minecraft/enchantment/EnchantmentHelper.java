package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
    public static int getEnchantmentLevel(Enchantment enchID, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(enchID);
        ListNBT listnbt = stack.getEnchantmentTagList();
        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            ResourceLocation resourcelocation1 = ResourceLocation.tryCreate(compoundnbt.getString("id"));
            if (resourcelocation1 == null || !resourcelocation1.equals(resourcelocation)) continue;
            return MathHelper.clamp(compoundnbt.getInt("lvl"), 0, 255);
        }
        return 0;
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
        ListNBT listnbt = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(stack) : stack.getEnchantmentTagList();
        return EnchantmentHelper.deserializeEnchantments(listnbt);
    }

    public static Map<Enchantment, Integer> deserializeEnchantments(ListNBT serialized) {
        LinkedHashMap<Enchantment, Integer> map = Maps.newLinkedHashMap();
        for (int i = 0; i < serialized.size(); ++i) {
            CompoundNBT compoundnbt = serialized.getCompound(i);
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryCreate(compoundnbt.getString("id"))).ifPresent(enchantment -> {
                Integer integer = map.put((Enchantment)enchantment, compoundnbt.getInt("lvl"));
            });
        }
        return map;
    }

    public static void setEnchantments(Map<Enchantment, Integer> enchMap, ItemStack stack) {
        ListNBT listnbt = new ListNBT();
        for (Map.Entry<Enchantment, Integer> entry : enchMap.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment == null) continue;
            int i = entry.getValue();
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(enchantment)));
            compoundnbt.putShort("lvl", (short)i);
            listnbt.add(compoundnbt);
            if (stack.getItem() != Items.ENCHANTED_BOOK) continue;
            EnchantedBookItem.addEnchantment(stack, new EnchantmentData(enchantment, i));
        }
        if (listnbt.isEmpty()) {
            stack.removeChildTag("Enchantments");
        } else if (stack.getItem() != Items.ENCHANTED_BOOK) {
            stack.setTagInfo("Enchantments", listnbt);
        }
    }

    private static void applyEnchantmentModifier(IEnchantmentVisitor modifier, ItemStack stack) {
        if (!stack.isEmpty()) {
            ListNBT listnbt = stack.getEnchantmentTagList();
            for (int i = 0; i < listnbt.size(); ++i) {
                String s = listnbt.getCompound(i).getString("id");
                int j = listnbt.getCompound(i).getInt("lvl");
                Registry.ENCHANTMENT.getOptional(ResourceLocation.tryCreate(s)).ifPresent(enchantment -> modifier.accept((Enchantment)enchantment, j));
            }
        }
    }

    private static void applyEnchantmentModifierArray(IEnchantmentVisitor modifier, Iterable<ItemStack> stacks) {
        for (ItemStack itemstack : stacks) {
            EnchantmentHelper.applyEnchantmentModifier(modifier, itemstack);
        }
    }

    public static int getEnchantmentModifierDamage(Iterable<ItemStack> stacks, DamageSource source) {
        MutableInt mutableint = new MutableInt();
        EnchantmentHelper.applyEnchantmentModifierArray((enchantment, level) -> mutableint.add(enchantment.calcModifierDamage(level, source)), stacks);
        return mutableint.intValue();
    }

    public static float getModifierForCreature(ItemStack stack, CreatureAttribute creatureAttribute) {
        MutableFloat mutablefloat = new MutableFloat();
        EnchantmentHelper.applyEnchantmentModifier((enchantment, level) -> mutablefloat.add(enchantment.calcDamageByCreature(level, creatureAttribute)), stack);
        return mutablefloat.floatValue();
    }

    public static float getSweepingDamageRatio(LivingEntity entityIn) {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SWEEPING, entityIn);
        return i > 0 ? SweepingEnchantment.getSweepingDamageRatio(i) : 0.0f;
    }

    public static void applyThornEnchantments(LivingEntity user, Entity attacker) {
        IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (enchantment, level) -> enchantment.onUserHurt(user, attacker, level);
        if (user != null) {
            EnchantmentHelper.applyEnchantmentModifierArray(enchantmenthelper$ienchantmentvisitor, user.getEquipmentAndArmor());
        }
        if (attacker instanceof PlayerEntity) {
            EnchantmentHelper.applyEnchantmentModifier(enchantmenthelper$ienchantmentvisitor, user.getHeldItemMainhand());
        }
    }

    public static void applyArthropodEnchantments(LivingEntity user, Entity target) {
        IEnchantmentVisitor enchantmenthelper$ienchantmentvisitor = (enchantment, level) -> enchantment.onEntityDamaged(user, target, level);
        if (user != null) {
            EnchantmentHelper.applyEnchantmentModifierArray(enchantmenthelper$ienchantmentvisitor, user.getEquipmentAndArmor());
        }
        if (user instanceof PlayerEntity) {
            EnchantmentHelper.applyEnchantmentModifier(enchantmenthelper$ienchantmentvisitor, user.getHeldItemMainhand());
        }
    }

    public static int getMaxEnchantmentLevel(Enchantment enchantmentIn, LivingEntity entityIn) {
        Collection<ItemStack> iterable = enchantmentIn.getEntityEquipment(entityIn).values();
        if (iterable == null) {
            return 0;
        }
        int i = 0;
        for (ItemStack itemstack : iterable) {
            int j = EnchantmentHelper.getEnchantmentLevel(enchantmentIn, itemstack);
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    public static int getKnockbackModifier(LivingEntity player) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.KNOCKBACK, player);
    }

    public static int getFireAspectModifier(LivingEntity player) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FIRE_ASPECT, player);
    }

    public static int getRespirationModifier(LivingEntity entityIn) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.RESPIRATION, entityIn);
    }

    public static int getDepthStriderModifier(LivingEntity entityIn) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.DEPTH_STRIDER, entityIn);
    }

    public static int getEfficiencyModifier(LivingEntity entityIn) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.EFFICIENCY, entityIn);
    }

    public static int getFishingLuckBonus(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, stack);
    }

    public static int getFishingSpeedBonus(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.LURE, stack);
    }

    public static int getLootingModifier(LivingEntity entityIn) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.LOOTING, entityIn);
    }

    public static boolean hasAquaAffinity(LivingEntity entityIn) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.AQUA_AFFINITY, entityIn) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity player) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FROST_WALKER, player) > 0;
    }

    public static boolean hasSoulSpeed(LivingEntity entity) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SOUL_SPEED, entity) > 0;
    }

    public static boolean hasBindingCurse(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack) > 0;
    }

    public static int getLoyaltyModifier(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.LOYALTY, stack);
    }

    public static int getRiptideModifier(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.RIPTIDE, stack);
    }

    public static boolean hasChanneling(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.CHANNELING, stack) > 0;
    }

    @Nullable
    public static Map.Entry<EquipmentSlotType, ItemStack> getRandomItemWithEnchantment(Enchantment targetEnchantment, LivingEntity entityIn) {
        return EnchantmentHelper.getRandomEquippedWithEnchantment(targetEnchantment, entityIn, stack -> true);
    }

    @Nullable
    public static Map.Entry<EquipmentSlotType, ItemStack> getRandomEquippedWithEnchantment(Enchantment enchantment, LivingEntity livingEntity, Predicate<ItemStack> stackCondition) {
        Map<EquipmentSlotType, ItemStack> map = enchantment.getEntityEquipment(livingEntity);
        if (map.isEmpty()) {
            return null;
        }
        ArrayList<Map.Entry<EquipmentSlotType, ItemStack>> list = Lists.newArrayList();
        for (Map.Entry<EquipmentSlotType, ItemStack> entry : map.entrySet()) {
            ItemStack itemstack = entry.getValue();
            if (itemstack.isEmpty() || EnchantmentHelper.getEnchantmentLevel(enchantment, itemstack) <= 0 || !stackCondition.test(itemstack)) continue;
            list.add(entry);
        }
        return list.isEmpty() ? null : (Map.Entry)list.get(livingEntity.getRNG().nextInt(list.size()));
    }

    public static int calcItemStackEnchantability(Random rand, int enchantNum, int power, ItemStack stack) {
        Item item = stack.getItem();
        int i = item.getItemEnchantability();
        if (i <= 0) {
            return 0;
        }
        if (power > 15) {
            power = 15;
        }
        int j = rand.nextInt(8) + 1 + (power >> 1) + rand.nextInt(power + 1);
        if (enchantNum == 0) {
            return Math.max(j / 3, 1);
        }
        return enchantNum == 1 ? j * 2 / 3 + 1 : Math.max(j, power * 2);
    }

    public static ItemStack addRandomEnchantment(Random random, ItemStack stack, int level, boolean allowTreasure) {
        boolean flag;
        List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(random, stack, level, allowTreasure);
        boolean bl = flag = stack.getItem() == Items.BOOK;
        if (flag) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (EnchantmentData enchantmentdata : list) {
            if (flag) {
                EnchantedBookItem.addEnchantment(stack, enchantmentdata);
                continue;
            }
            stack.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
        }
        return stack;
    }

    public static List<EnchantmentData> buildEnchantmentList(Random randomIn, ItemStack itemStackIn, int level, boolean allowTreasure) {
        ArrayList<EnchantmentData> list = Lists.newArrayList();
        Item item = itemStackIn.getItem();
        int i = item.getItemEnchantability();
        if (i <= 0) {
            return list;
        }
        level = level + 1 + randomIn.nextInt(i / 4 + 1) + randomIn.nextInt(i / 4 + 1);
        float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentData> list1 = EnchantmentHelper.getEnchantmentDatas(level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE), itemStackIn, allowTreasure);
        if (!list1.isEmpty()) {
            list.add(WeightedRandom.getRandomItem(randomIn, list1));
            while (randomIn.nextInt(50) <= level) {
                EnchantmentHelper.removeIncompatible(list1, Util.getLast(list));
                if (list1.isEmpty()) break;
                list.add(WeightedRandom.getRandomItem(randomIn, list1));
                level /= 2;
            }
        }
        return list;
    }

    public static void removeIncompatible(List<EnchantmentData> dataList, EnchantmentData data) {
        Iterator<EnchantmentData> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            if (data.enchantment.isCompatibleWith(iterator.next().enchantment)) continue;
            iterator.remove();
        }
    }

    public static boolean areAllCompatibleWith(Collection<Enchantment> enchantmentsIn, Enchantment enchantmentIn) {
        for (Enchantment enchantment : enchantmentsIn) {
            if (enchantment.isCompatibleWith(enchantmentIn)) continue;
            return false;
        }
        return true;
    }

    public static List<EnchantmentData> getEnchantmentDatas(int level, ItemStack stack, boolean allowTreasure) {
        ArrayList<EnchantmentData> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean flag = stack.getItem() == Items.BOOK;
        block0: for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.isTreasureEnchantment() && !allowTreasure || !enchantment.canGenerateInLoot() || !enchantment.type.canEnchantItem(item) && !flag) continue;
            for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                if (level < enchantment.getMinEnchantability(i) || level > enchantment.getMaxEnchantability(i)) continue;
                list.add(new EnchantmentData(enchantment, i));
                continue block0;
            }
        }
        return list;
    }

    @FunctionalInterface
    static interface IEnchantmentVisitor {
        public void accept(Enchantment var1, int var2);
    }
}
