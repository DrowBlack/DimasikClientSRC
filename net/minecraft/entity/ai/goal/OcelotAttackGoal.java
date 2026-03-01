package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.IBlockReader;

public class OcelotAttackGoal
extends Goal {
    private final IBlockReader world;
    private final MobEntity entity;
    private LivingEntity target;
    private int attackCountdown;

    public OcelotAttackGoal(MobEntity theEntityIn) {
        this.entity = theEntityIn;
        this.world = theEntityIn.world;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity livingentity = this.entity.getAttackTarget();
        if (livingentity == null) {
            return false;
        }
        this.target = livingentity;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (!this.target.isAlive()) {
            return false;
        }
        if (this.entity.getDistanceSq(this.target) > 225.0) {
            return false;
        }
        return !this.entity.getNavigator().noPath() || this.shouldExecute();
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.entity.getNavigator().clearPath();
    }

    @Override
    public void tick() {
        this.entity.getLookController().setLookPositionWithEntity(this.target, 30.0f, 30.0f);
        double d0 = this.entity.getWidth() * 2.0f * this.entity.getWidth() * 2.0f;
        double d1 = this.entity.getDistanceSq(this.target.getPosX(), this.target.getPosY(), this.target.getPosZ());
        double d2 = 0.8;
        if (d1 > d0 && d1 < 16.0) {
            d2 = 1.33;
        } else if (d1 < 225.0) {
            d2 = 0.6;
        }
        this.entity.getNavigator().tryMoveToEntityLiving(this.target, d2);
        this.attackCountdown = Math.max(this.attackCountdown - 1, 0);
        if (!(d1 > d0) && this.attackCountdown <= 0) {
            this.attackCountdown = 20;
            this.entity.attackEntityAsMob(this.target);
        }
    }
}
