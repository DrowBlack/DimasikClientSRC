package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public class PanicGoal
extends Goal {
    protected final CreatureEntity creature;
    protected final double speed;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;
    protected boolean running;

    public PanicGoal(CreatureEntity creature, double speedIn) {
        this.creature = creature;
        this.speed = speedIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        BlockPos blockpos;
        if (this.creature.getRevengeTarget() == null && !this.creature.isBurning()) {
            return false;
        }
        if (this.creature.isBurning() && (blockpos = this.getRandPos(this.creature.world, this.creature, 5, 4)) != null) {
            this.randPosX = blockpos.getX();
            this.randPosY = blockpos.getY();
            this.randPosZ = blockpos.getZ();
            return true;
        }
        return this.findRandomPosition();
    }

    protected boolean findRandomPosition() {
        Vector3d vector3d = RandomPositionGenerator.findRandomTarget(this.creature, 5, 4);
        if (vector3d == null) {
            return false;
        }
        this.randPosX = vector3d.x;
        this.randPosY = vector3d.y;
        this.randPosZ = vector3d.z;
        return true;
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void startExecuting() {
        this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
        this.running = true;
    }

    @Override
    public void resetTask() {
        this.running = false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath();
    }

    @Nullable
    protected BlockPos getRandPos(IBlockReader worldIn, Entity entityIn, int horizontalRange, int verticalRange) {
        BlockPos blockpos = entityIn.getPosition();
        int i = blockpos.getX();
        int j = blockpos.getY();
        int k = blockpos.getZ();
        float f = horizontalRange * horizontalRange * verticalRange * 2;
        BlockPos blockpos1 = null;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int l = i - horizontalRange; l <= i + horizontalRange; ++l) {
            for (int i1 = j - verticalRange; i1 <= j + verticalRange; ++i1) {
                for (int j1 = k - horizontalRange; j1 <= k + horizontalRange; ++j1) {
                    float f1;
                    blockpos$mutable.setPos(l, i1, j1);
                    if (!worldIn.getFluidState(blockpos$mutable).isTagged(FluidTags.WATER) || !((f1 = (float)((l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k))) < f)) continue;
                    f = f1;
                    blockpos1 = new BlockPos(blockpos$mutable);
                }
            }
        }
        return blockpos1;
    }
}
