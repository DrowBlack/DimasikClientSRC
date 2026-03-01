package net.minecraft.util.math;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.math.vector.Vector3d;

public class BlockPosWrapper
implements IPosWrapper {
    private final BlockPos pos;
    private final Vector3d centerPos;

    public BlockPosWrapper(BlockPos pos) {
        this.pos = pos;
        this.centerPos = Vector3d.copyCentered(pos);
    }

    @Override
    public Vector3d getPos() {
        return this.centerPos;
    }

    @Override
    public BlockPos getBlockPos() {
        return this.pos;
    }

    @Override
    public boolean isVisibleTo(LivingEntity entity) {
        return true;
    }

    public String toString() {
        return "BlockPosTracker{blockPos=" + String.valueOf(this.pos) + ", centerPosition=" + String.valueOf(this.centerPos) + "}";
    }
}
