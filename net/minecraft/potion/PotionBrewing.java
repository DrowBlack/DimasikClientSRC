package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.registry.Registry;

public class PotionBrewing {
    private static final List<MixPredicate<Potion>> POTION_TYPE_CONVERSIONS = Lists.newArrayList();
    private static final List<MixPredicate<Item>> POTION_ITEM_CONVERSIONS = Lists.newArrayList();
    private static final List<Ingredient> POTION_ITEMS = Lists.newArrayList();
    private static final Predicate<ItemStack> IS_POTION_ITEM = p_210319_0_ -> {
        for (Ingredient ingredient : POTION_ITEMS) {
            if (!ingredient.test((ItemStack)p_210319_0_)) continue;
            return true;
        }
        return false;
    };

    public static boolean isReagent(ItemStack stack) {
        return PotionBrewing.isItemConversionReagent(stack) || PotionBrewing.isTypeConversionReagent(stack);
    }

    protected static boolean isItemConversionReagent(ItemStack stack) {
        int j = POTION_ITEM_CONVERSIONS.size();
        for (int i = 0; i < j; ++i) {
            if (!PotionBrewing.POTION_ITEM_CONVERSIONS.get((int)i).reagent.test(stack)) continue;
            return true;
        }
        return false;
    }

    protected static boolean isTypeConversionReagent(ItemStack stack) {
        int j = POTION_TYPE_CONVERSIONS.size();
        for (int i = 0; i < j; ++i) {
            if (!PotionBrewing.POTION_TYPE_CONVERSIONS.get((int)i).reagent.test(stack)) continue;
            return true;
        }
        return false;
    }

    public static boolean isBrewablePotion(Potion potion) {
        int j = POTION_TYPE_CONVERSIONS.size();
        for (int i = 0; i < j; ++i) {
            if (PotionBrewing.POTION_TYPE_CONVERSIONS.get((int)i).output != potion) continue;
            return true;
        }
        return false;
    }

    public static boolean hasConversions(ItemStack input, ItemStack reagent) {
        if (!IS_POTION_ITEM.test(input)) {
            return false;
        }
        return PotionBrewing.hasItemConversions(input, reagent) || PotionBrewing.hasTypeConversions(input, reagent);
    }

    protected static boolean hasItemConversions(ItemStack input, ItemStack reagent) {
        Item item = input.getItem();
        int j = POTION_ITEM_CONVERSIONS.size();
        for (int i = 0; i < j; ++i) {
            MixPredicate<Item> mixpredicate = POTION_ITEM_CONVERSIONS.get(i);
            if (mixpredicate.input != item || !mixpredicate.reagent.test(reagent)) continue;
            return true;
        }
        return false;
    }

    protected static boolean hasTypeConversions(ItemStack input, ItemStack reagent) {
        Potion potion = PotionUtils.getPotionFromItem(input);
        int j = POTION_TYPE_CONVERSIONS.size();
        for (int i = 0; i < j; ++i) {
            MixPredicate<Potion> mixpredicate = POTION_TYPE_CONVERSIONS.get(i);
            if (mixpredicate.input != potion || !mixpredicate.reagent.test(reagent)) continue;
            return true;
        }
        return false;
    }

    public static ItemStack doReaction(ItemStack reagent, ItemStack potionIn) {
        if (!potionIn.isEmpty()) {
            int i;
            Potion potion = PotionUtils.getPotionFromItem(potionIn);
            Item item = potionIn.getItem();
            int j = POTION_ITEM_CONVERSIONS.size();
            for (i = 0; i < j; ++i) {
                MixPredicate<Item> mixpredicate = POTION_ITEM_CONVERSIONS.get(i);
                if (mixpredicate.input != item || !mixpredicate.reagent.test(reagent)) continue;
                return PotionUtils.addPotionToItemStack(new ItemStack((IItemProvider)mixpredicate.output), potion);
            }
            int k = POTION_TYPE_CONVERSIONS.size();
            for (i = 0; i < k; ++i) {
                MixPredicate<Potion> mixpredicate1 = POTION_TYPE_CONVERSIONS.get(i);
                if (mixpredicate1.input != potion || !mixpredicate1.reagent.test(reagent)) continue;
                return PotionUtils.addPotionToItemStack(new ItemStack(item), (Potion)mixpredicate1.output);
            }
        }
        return potionIn;
    }

    public static void init() {
        PotionBrewing.addContainer(Items.POTION);
        PotionBrewing.addContainer(Items.SPLASH_POTION);
        PotionBrewing.addContainer(Items.LINGERING_POTION);
        PotionBrewing.addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        PotionBrewing.addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        PotionBrewing.addMix(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
        PotionBrewing.addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        PotionBrewing.addMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        PotionBrewing.addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
        PotionBrewing.addMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        PotionBrewing.addMix(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
        PotionBrewing.addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
        PotionBrewing.addMix(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        PotionBrewing.addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
        PotionBrewing.addMix(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
        PotionBrewing.addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
        PotionBrewing.addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
        PotionBrewing.addMix(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        PotionBrewing.addMix(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        PotionBrewing.addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
        PotionBrewing.addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
        PotionBrewing.addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        PotionBrewing.addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
        PotionBrewing.addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
        PotionBrewing.addMix(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        PotionBrewing.addMix(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        PotionBrewing.addMix(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
        PotionBrewing.addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
        PotionBrewing.addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
        PotionBrewing.addMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        PotionBrewing.addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
        PotionBrewing.addMix(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        PotionBrewing.addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
        PotionBrewing.addMix(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        PotionBrewing.addMix(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        PotionBrewing.addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
        PotionBrewing.addMix(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        PotionBrewing.addMix(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        PotionBrewing.addMix(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        PotionBrewing.addMix(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
        PotionBrewing.addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
        PotionBrewing.addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
        PotionBrewing.addMix(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        PotionBrewing.addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
        PotionBrewing.addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
        PotionBrewing.addMix(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        PotionBrewing.addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
        PotionBrewing.addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
        PotionBrewing.addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        PotionBrewing.addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
        PotionBrewing.addMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        PotionBrewing.addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
    }

    private static void addContainerRecipe(Item p_196207_0_, Item p_196207_1_, Item p_196207_2_) {
        if (!(p_196207_0_ instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + String.valueOf(Registry.ITEM.getKey(p_196207_0_)));
        }
        if (!(p_196207_2_ instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + String.valueOf(Registry.ITEM.getKey(p_196207_2_)));
        }
        POTION_ITEM_CONVERSIONS.add(new MixPredicate<Item>(p_196207_0_, Ingredient.fromItems(p_196207_1_), p_196207_2_));
    }

    private static void addContainer(Item p_196208_0_) {
        if (!(p_196208_0_ instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + String.valueOf(Registry.ITEM.getKey(p_196208_0_)));
        }
        POTION_ITEMS.add(Ingredient.fromItems(p_196208_0_));
    }

    private static void addMix(Potion potionEntry, Item potionIngredient, Potion potionResult) {
        POTION_TYPE_CONVERSIONS.add(new MixPredicate<Potion>(potionEntry, Ingredient.fromItems(potionIngredient), potionResult));
    }

    static class MixPredicate<T> {
        private final T input;
        private final Ingredient reagent;
        private final T output;

        public MixPredicate(T inputIn, Ingredient reagentIn, T outputIn) {
            this.input = inputIn;
            this.reagent = reagentIn;
            this.output = outputIn;
        }
    }
}
