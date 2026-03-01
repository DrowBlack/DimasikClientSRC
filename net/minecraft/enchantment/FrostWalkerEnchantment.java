package net.minecraft.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;

public class FrostWalkerEnchantment
extends Enchantment {
    public FrostWalkerEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType ... slots) {
        super(rarityIn, EnchantmentType.ARMOR_FEET, slots);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return enchantmentLevel * 10;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    public static void freezeNearby(LivingEntity living, World worldIn, BlockPos pos, int level) {
        if (living.isOnGround()) {
            BlockState blockstate = Blocks.FROSTED_ICE.getDefaultState();
            float f = Math.min(16, 2 + level);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -1.0, -f), pos.add(f, -1.0, f))) {
                BlockState blockstate2;
                if (!blockpos.withinDistance(living.getPositionVec(), (double)f)) continue;
                blockpos$mutable.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(blockpos$mutable);
                if (!blockstate1.isAir() || (blockstate2 = worldIn.getBlockState(blockpos)).getMaterial() != Material.WATER || blockstate2.get(FlowingFluidBlock.LEVEL) != 0 || !blockstate.isValidPosition(worldIn, blockpos) || !worldIn.placedBlockCollides(blockstate, blockpos, ISelectionContext.dummy())) continue;
                worldIn.setBlockState(blockpos, blockstate);
                worldIn.getPendingBlockTicks().scheduleTick(blockpos, Blocks.FROSTED_ICE, MathHelper.nextInt(living.getRNG(), 60, 120));
            }
        }
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        return super.canApplyTogether(ench) && ench != Enchantments.DEPTH_STRIDER;
    }
}
