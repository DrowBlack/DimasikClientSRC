package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlintAndSteelItem
extends Item {
    public FlintAndSteelItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos blockpos;
        PlayerEntity playerentity = context.getPlayer();
        World world = context.getWorld();
        BlockState blockstate = world.getBlockState(blockpos = context.getPos());
        if (CampfireBlock.canBeLit(blockstate)) {
            world.playSound(playerentity, blockpos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.4f + 0.8f);
            world.setBlockState(blockpos, (BlockState)blockstate.with(BlockStateProperties.LIT, true), 11);
            if (playerentity != null) {
                context.getItem().damageItem(1, playerentity, p_219999_1_ -> p_219999_1_.sendBreakAnimation(context.getHand()));
            }
            return ActionResultType.func_233537_a_(world.isRemote());
        }
        BlockPos blockpos1 = blockpos.offset(context.getFace());
        if (AbstractFireBlock.canLightBlock(world, blockpos1, context.getPlacementHorizontalFacing())) {
            world.playSound(playerentity, blockpos1, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, random.nextFloat() * 0.4f + 0.8f);
            BlockState blockstate1 = AbstractFireBlock.getFireForPlacement(world, blockpos1);
            world.setBlockState(blockpos1, blockstate1, 11);
            ItemStack itemstack = context.getItem();
            if (playerentity instanceof ServerPlayerEntity) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, blockpos1, itemstack);
                itemstack.damageItem(1, playerentity, p_219998_1_ -> p_219998_1_.sendBreakAnimation(context.getHand()));
            }
            return ActionResultType.func_233537_a_(world.isRemote());
        }
        return ActionResultType.FAIL;
    }
}
