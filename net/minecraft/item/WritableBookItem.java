package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WritableBookItem
extends Item {
    public WritableBookItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos blockpos;
        World world = context.getWorld();
        BlockState blockstate = world.getBlockState(blockpos = context.getPos());
        if (blockstate.isIn(Blocks.LECTERN)) {
            return LecternBlock.tryPlaceBook(world, blockpos, blockstate, context.getItem()) ? ActionResultType.func_233537_a_(world.isRemote) : ActionResultType.PASS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.openBook(itemstack, handIn);
        playerIn.addStat(Stats.ITEM_USED.get(this));
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }

    public static boolean isNBTValid(@Nullable CompoundNBT nbt) {
        if (nbt == null) {
            return false;
        }
        if (!nbt.contains("pages", 9)) {
            return false;
        }
        ListNBT listnbt = nbt.getList("pages", 8);
        for (int i = 0; i < listnbt.size(); ++i) {
            String s = listnbt.getString(i);
            if (s.length() <= Short.MAX_VALUE) continue;
            return false;
        }
        return true;
    }
}
