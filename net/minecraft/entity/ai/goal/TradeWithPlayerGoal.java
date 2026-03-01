package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class TradeWithPlayerGoal
extends Goal {
    private final AbstractVillagerEntity villager;

    public TradeWithPlayerGoal(AbstractVillagerEntity villager) {
        this.villager = villager;
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.villager.isAlive()) {
            return false;
        }
        if (this.villager.isInWater()) {
            return false;
        }
        if (!this.villager.isOnGround()) {
            return false;
        }
        if (this.villager.velocityChanged) {
            return false;
        }
        PlayerEntity playerentity = this.villager.getCustomer();
        if (playerentity == null) {
            return false;
        }
        if (this.villager.getDistanceSq(playerentity) > 16.0) {
            return false;
        }
        return playerentity.openContainer != null;
    }

    @Override
    public void startExecuting() {
        this.villager.getNavigator().clearPath();
    }

    @Override
    public void resetTask() {
        this.villager.setCustomer(null);
    }
}
