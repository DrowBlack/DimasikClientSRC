package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

public class LeapAtTargetGoal
extends Goal {
    private final MobEntity leaper;
    private LivingEntity leapTarget;
    private final float leapMotionY;

    public LeapAtTargetGoal(MobEntity leapingEntity, float leapMotionYIn) {
        this.leaper = leapingEntity;
        this.leapMotionY = leapMotionYIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (this.leaper.isBeingRidden()) {
            return false;
        }
        this.leapTarget = this.leaper.getAttackTarget();
        if (this.leapTarget == null) {
            return false;
        }
        double d0 = this.leaper.getDistanceSq(this.leapTarget);
        if (!(d0 < 4.0) && !(d0 > 16.0)) {
            if (!this.leaper.isOnGround()) {
                return false;
            }
            return this.leaper.getRNG().nextInt(5) == 0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.leaper.isOnGround();
    }

    @Override
    public void startExecuting() {
        Vector3d vector3d = this.leaper.getMotion();
        Vector3d vector3d1 = new Vector3d(this.leapTarget.getPosX() - this.leaper.getPosX(), 0.0, this.leapTarget.getPosZ() - this.leaper.getPosZ());
        if (vector3d1.lengthSquared() > 1.0E-7) {
            vector3d1 = vector3d1.normalize().scale(0.4).add(vector3d.scale(0.2));
        }
        this.leaper.setMotion(vector3d1.x, this.leapMotionY, vector3d1.z);
    }
}
