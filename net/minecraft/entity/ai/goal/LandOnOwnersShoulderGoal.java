package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.ShoulderRidingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class LandOnOwnersShoulderGoal
extends Goal {
    private final ShoulderRidingEntity entity;
    private ServerPlayerEntity owner;
    private boolean isSittingOnShoulder;

    public LandOnOwnersShoulderGoal(ShoulderRidingEntity entityIn) {
        this.entity = entityIn;
    }

    @Override
    public boolean shouldExecute() {
        ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)this.entity.getOwner();
        boolean flag = serverplayerentity != null && !serverplayerentity.isSpectator() && !serverplayerentity.abilities.isFlying && !serverplayerentity.isInWater();
        return !this.entity.isSitting() && flag && this.entity.canSitOnShoulder();
    }

    @Override
    public boolean isPreemptible() {
        return !this.isSittingOnShoulder;
    }

    @Override
    public void startExecuting() {
        this.owner = (ServerPlayerEntity)this.entity.getOwner();
        this.isSittingOnShoulder = false;
    }

    @Override
    public void tick() {
        if (!this.isSittingOnShoulder && !this.entity.isSleeping() && !this.entity.getLeashed() && this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
            this.isSittingOnShoulder = this.entity.func_213439_d(this.owner);
        }
    }
}
