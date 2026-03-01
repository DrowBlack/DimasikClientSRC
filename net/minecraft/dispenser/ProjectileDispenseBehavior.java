package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class ProjectileDispenseBehavior
extends DefaultDispenseItemBehavior {
    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        ServerWorld world = source.getWorld();
        IPosition iposition = DispenserBlock.getDispensePosition(source);
        Direction direction = source.getBlockState().get(DispenserBlock.FACING);
        ProjectileEntity projectileentity = this.getProjectileEntity(world, iposition, stack);
        projectileentity.shoot(direction.getXOffset(), (float)direction.getYOffset() + 0.1f, direction.getZOffset(), this.getProjectileVelocity(), this.getProjectileInaccuracy());
        world.addEntity(projectileentity);
        stack.shrink(1);
        return stack;
    }

    @Override
    protected void playDispenseSound(IBlockSource source) {
        source.getWorld().playEvent(1002, source.getBlockPos(), 0);
    }

    protected abstract ProjectileEntity getProjectileEntity(World var1, IPosition var2, ItemStack var3);

    protected float getProjectileInaccuracy() {
        return 6.0f;
    }

    protected float getProjectileVelocity() {
        return 1.1f;
    }
}
