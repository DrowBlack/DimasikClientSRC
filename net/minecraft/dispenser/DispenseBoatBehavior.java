package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class DispenseBoatBehavior
extends DefaultDispenseItemBehavior {
    private final DefaultDispenseItemBehavior dispenseItemBehaviour = new DefaultDispenseItemBehavior();
    private final BoatEntity.Type type;

    public DispenseBoatBehavior(BoatEntity.Type typeIn) {
        this.type = typeIn;
    }

    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        double d3;
        Direction direction = source.getBlockState().get(DispenserBlock.FACING);
        ServerWorld world = source.getWorld();
        double d0 = source.getX() + (double)((float)direction.getXOffset() * 1.125f);
        double d1 = source.getY() + (double)((float)direction.getYOffset() * 1.125f);
        double d2 = source.getZ() + (double)((float)direction.getZOffset() * 1.125f);
        BlockPos blockpos = source.getBlockPos().offset(direction);
        if (world.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
            d3 = 1.0;
        } else {
            if (!world.getBlockState(blockpos).isAir() || !world.getFluidState(blockpos.down()).isTagged(FluidTags.WATER)) {
                return this.dispenseItemBehaviour.dispense(source, stack);
            }
            d3 = 0.0;
        }
        BoatEntity boatentity = new BoatEntity(world, d0, d1 + d3, d2);
        boatentity.setBoatType(this.type);
        boatentity.rotationYaw = direction.getHorizontalAngle();
        world.addEntity(boatentity);
        stack.shrink(1);
        return stack;
    }

    @Override
    protected void playDispenseSound(IBlockSource source) {
        source.getWorld().playEvent(1000, source.getBlockPos(), 0);
    }
}
