package dimasik.itemics.utils;

import dimasik.itemics.Itemics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TieredItem;
import net.minecraft.potion.Effects;

public class ToolSet {
    private final Map<Block, Double> breakStrengthCache = new HashMap<Block, Double>();
    private final Function<Block, Double> backendCalculation;
    private final ClientPlayerEntity player;

    public ToolSet(ClientPlayerEntity player) {
        this.player = player;
        if (((Boolean)Itemics.settings().considerPotionEffects.value).booleanValue()) {
            double amplifier = this.potionAmplifier();
            Function<Double, Double> amplify = x -> amplifier * x;
            this.backendCalculation = amplify.compose(this::getBestDestructionTime);
        } else {
            this.backendCalculation = this::getBestDestructionTime;
        }
    }

    public double getStrVsBlock(BlockState state) {
        return this.breakStrengthCache.computeIfAbsent(state.getBlock(), this.backendCalculation);
    }

    private int getMaterialCost(ItemStack itemStack) {
        if (itemStack.getItem() instanceof TieredItem) {
            TieredItem tool = (TieredItem)itemStack.getItem();
            return tool.getTier().getHarvestLevel();
        }
        return -1;
    }

    public boolean hasSilkTouch(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
    }

    public int getBestSlot(Block b, boolean preferSilkTouch) {
        return this.getBestSlot(b, preferSilkTouch, false);
    }

    public int getBestSlot(Block b, boolean preferSilkTouch, boolean pathingCalculation) {
        if (!((Boolean)Itemics.settings().autoTool.value).booleanValue() && pathingCalculation) {
            return this.player.inventory.currentItem;
        }
        int best = 0;
        double highestSpeed = Double.NEGATIVE_INFINITY;
        int lowestCost = Integer.MIN_VALUE;
        boolean bestSilkTouch = false;
        BlockState blockState = b.getDefaultState();
        for (int i = 0; i < 9; ++i) {
            int cost;
            ItemStack itemStack = this.player.inventory.getStackInSlot(i);
            if (!((Boolean)Itemics.settings().useSwordToMine.value).booleanValue() && itemStack.getItem() instanceof SwordItem || ((Boolean)Itemics.settings().itemSaver.value).booleanValue() && itemStack.getDamage() + (Integer)Itemics.settings().itemSaverThreshold.value >= itemStack.getMaxDamage() && itemStack.getMaxDamage() > 1) continue;
            double speed = ToolSet.calculateSpeedVsBlock(itemStack, blockState);
            boolean silkTouch = this.hasSilkTouch(itemStack);
            if (speed > highestSpeed) {
                highestSpeed = speed;
                best = i;
                lowestCost = this.getMaterialCost(itemStack);
                bestSilkTouch = silkTouch;
                continue;
            }
            if (speed != highestSpeed || ((cost = this.getMaterialCost(itemStack)) >= lowestCost || !silkTouch && bestSilkTouch) && (!preferSilkTouch || bestSilkTouch || !silkTouch)) continue;
            highestSpeed = speed;
            best = i;
            lowestCost = cost;
            bestSilkTouch = silkTouch;
        }
        return best;
    }

    private double getBestDestructionTime(Block b) {
        ItemStack stack = this.player.inventory.getStackInSlot(this.getBestSlot(b, false, true));
        return ToolSet.calculateSpeedVsBlock(stack, b.getDefaultState()) * this.avoidanceMultiplier(b);
    }

    private double avoidanceMultiplier(Block b) {
        return ((List)Itemics.settings().blocksToAvoidBreaking.value).contains(b) ? (Double)Itemics.settings().avoidBreakingMultiplier.value : 1.0;
    }

    public static double calculateSpeedVsBlock(ItemStack item, BlockState state) {
        int effLevel;
        float hardness = state.getBlockHardness(null, null);
        if (hardness < 0.0f) {
            return -1.0;
        }
        float speed = item.getDestroySpeed(state);
        if (speed > 1.0f && (effLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, item)) > 0 && !item.isEmpty()) {
            speed += (float)(effLevel * effLevel + 1);
        }
        speed /= hardness;
        if (!state.getRequiresTool() || !item.isEmpty() && item.canHarvestBlock(state)) {
            return speed / 30.0f;
        }
        return speed / 100.0f;
    }

    private double potionAmplifier() {
        double speed = 1.0;
        if (this.player.isPotionActive(Effects.HASTE)) {
            speed *= 1.0 + (double)(this.player.getActivePotionEffect(Effects.HASTE).getAmplifier() + 1) * 0.2;
        }
        if (this.player.isPotionActive(Effects.MINING_FATIGUE)) {
            switch (this.player.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) {
                case 0: {
                    speed *= 0.3;
                    break;
                }
                case 1: {
                    speed *= 0.09;
                    break;
                }
                case 2: {
                    speed *= 0.0027;
                    break;
                }
                default: {
                    speed *= 8.1E-4;
                }
            }
        }
        return speed;
    }
}
