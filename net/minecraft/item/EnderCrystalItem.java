package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.server.ServerWorld;

public class EnderCrystalItem
extends Item {
    public EnderCrystalItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        double d2;
        double d1;
        BlockPos blockpos;
        World world = context.getWorld();
        BlockState blockstate = world.getBlockState(blockpos = context.getPos());
        if (!blockstate.isIn(Blocks.OBSIDIAN) && !blockstate.isIn(Blocks.BEDROCK)) {
            return ActionResultType.FAIL;
        }
        BlockPos blockpos1 = blockpos.up();
        if (!world.isAirBlock(blockpos1)) {
            return ActionResultType.FAIL;
        }
        double d0 = blockpos1.getX();
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(d0, d1 = (double)blockpos1.getY(), d2 = (double)blockpos1.getZ(), d0 + 1.0, d1 + 2.0, d2 + 1.0));
        if (!list.isEmpty()) {
            return ActionResultType.FAIL;
        }
        if (world instanceof ServerWorld) {
            EnderCrystalEntity endercrystalentity = new EnderCrystalEntity(world, d0 + 0.5, d1, d2 + 0.5);
            endercrystalentity.setShowBottom(false);
            world.addEntity(endercrystalentity);
            DragonFightManager dragonfightmanager = ((ServerWorld)world).func_241110_C_();
            if (dragonfightmanager != null) {
                dragonfightmanager.tryRespawnDragon();
            }
        }
        context.getItem().shrink(1);
        return ActionResultType.func_233537_a_(world.isRemote);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
