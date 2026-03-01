package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LeadItem
extends Item {
    public LeadItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos blockpos;
        World world = context.getWorld();
        Block block = world.getBlockState(blockpos = context.getPos()).getBlock();
        if (block.isIn(BlockTags.FENCES)) {
            PlayerEntity playerentity = context.getPlayer();
            if (!world.isRemote && playerentity != null) {
                LeadItem.bindPlayerMobs(playerentity, world, blockpos);
            }
            return ActionResultType.func_233537_a_(world.isRemote);
        }
        return ActionResultType.PASS;
    }

    public static ActionResultType bindPlayerMobs(PlayerEntity player, World world, BlockPos pos) {
        LeashKnotEntity leashknotentity = null;
        boolean flag = false;
        double d0 = 7.0;
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        for (MobEntity mobentity : world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0))) {
            if (mobentity.getLeashHolder() != player) continue;
            if (leashknotentity == null) {
                leashknotentity = LeashKnotEntity.create(world, pos);
            }
            mobentity.setLeashHolder(leashknotentity, true);
            flag = true;
        }
        return flag ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }
}
