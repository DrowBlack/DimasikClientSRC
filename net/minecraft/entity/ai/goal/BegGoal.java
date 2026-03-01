package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BegGoal
extends Goal {
    private final WolfEntity wolf;
    private PlayerEntity player;
    private final World world;
    private final float minPlayerDistance;
    private int timeoutCounter;
    private final EntityPredicate playerPredicate;

    public BegGoal(WolfEntity wolf, float minDistance) {
        this.wolf = wolf;
        this.world = wolf.world;
        this.minPlayerDistance = minDistance;
        this.playerPredicate = new EntityPredicate().setDistance(minDistance).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks();
        this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        this.player = this.world.getClosestPlayer(this.playerPredicate, this.wolf);
        return this.player == null ? false : this.hasTemptationItemInHand(this.player);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (!this.player.isAlive()) {
            return false;
        }
        if (this.wolf.getDistanceSq(this.player) > (double)(this.minPlayerDistance * this.minPlayerDistance)) {
            return false;
        }
        return this.timeoutCounter > 0 && this.hasTemptationItemInHand(this.player);
    }

    @Override
    public void startExecuting() {
        this.wolf.setBegging(true);
        this.timeoutCounter = 40 + this.wolf.getRNG().nextInt(40);
    }

    @Override
    public void resetTask() {
        this.wolf.setBegging(false);
        this.player = null;
    }

    @Override
    public void tick() {
        this.wolf.getLookController().setLookPosition(this.player.getPosX(), this.player.getPosYEye(), this.player.getPosZ(), 10.0f, this.wolf.getVerticalFaceSpeed());
        --this.timeoutCounter;
    }

    private boolean hasTemptationItemInHand(PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            ItemStack itemstack = player.getHeldItem(hand);
            if (this.wolf.isTamed() && itemstack.getItem() == Items.BONE) {
                return true;
            }
            if (!this.wolf.isBreedingItem(itemstack)) continue;
            return true;
        }
        return false;
    }
}
