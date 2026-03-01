package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

public class MoveTowardsTargetGoal
extends Goal {
    private final CreatureEntity creature;
    private LivingEntity targetEntity;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private final double speed;
    private final float maxTargetDistance;

    public MoveTowardsTargetGoal(CreatureEntity creature, double speedIn, float targetMaxDistance) {
        this.creature = creature;
        this.speed = speedIn;
        this.maxTargetDistance = targetMaxDistance;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        this.targetEntity = this.creature.getAttackTarget();
        if (this.targetEntity == null) {
            return false;
        }
        if (this.targetEntity.getDistanceSq(this.creature) > (double)(this.maxTargetDistance * this.maxTargetDistance)) {
            return false;
        }
        Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.creature, 16, 7, this.targetEntity.getPositionVec());
        if (vector3d == null) {
            return false;
        }
        this.movePosX = vector3d.x;
        this.movePosY = vector3d.y;
        this.movePosZ = vector3d.z;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath() && this.targetEntity.isAlive() && this.targetEntity.getDistanceSq(this.creature) < (double)(this.maxTargetDistance * this.maxTargetDistance);
    }

    @Override
    public void resetTask() {
        this.targetEntity = null;
    }

    @Override
    public void startExecuting() {
        this.creature.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    }
}
