package net.minecraft.util.math;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public interface IPosWrapper {
    public Vector3d getPos();

    public BlockPos getBlockPos();

    public boolean isVisibleTo(LivingEntity var1);
}
