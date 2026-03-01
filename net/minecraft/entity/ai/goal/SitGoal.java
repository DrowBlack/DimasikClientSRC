package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;

public class SitGoal
extends Goal {
    private final TameableEntity tameable;

    public SitGoal(TameableEntity entityIn) {
        this.tameable = entityIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.tameable.isSitting();
    }

    @Override
    public boolean shouldExecute() {
        if (!this.tameable.isTamed()) {
            return false;
        }
        if (this.tameable.isInWaterOrBubbleColumn()) {
            return false;
        }
        if (!this.tameable.isOnGround()) {
            return false;
        }
        LivingEntity livingentity = this.tameable.getOwner();
        if (livingentity == null) {
            return true;
        }
        return this.tameable.getDistanceSq(livingentity) < 144.0 && livingentity.getRevengeTarget() != null ? false : this.tameable.isSitting();
    }

    @Override
    public void startExecuting() {
        this.tameable.getNavigator().clearPath();
        this.tameable.setSleeping(true);
    }

    @Override
    public void resetTask() {
        this.tameable.setSleeping(false);
    }
}
