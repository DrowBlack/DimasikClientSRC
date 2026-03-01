package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;

public abstract class TargetGoal
extends Goal {
    protected final MobEntity goalOwner;
    protected final boolean shouldCheckSight;
    private final boolean nearbyOnly;
    private int targetSearchStatus;
    private int targetSearchDelay;
    private int targetUnseenTicks;
    protected LivingEntity target;
    protected int unseenMemoryTicks = 60;

    public TargetGoal(MobEntity mobIn, boolean checkSight) {
        this(mobIn, checkSight, false);
    }

    public TargetGoal(MobEntity mobIn, boolean checkSight, boolean nearbyOnlyIn) {
        this.goalOwner = mobIn;
        this.shouldCheckSight = checkSight;
        this.nearbyOnly = nearbyOnlyIn;
    }

    @Override
    public boolean shouldContinueExecuting() {
        LivingEntity livingentity = this.goalOwner.getAttackTarget();
        if (livingentity == null) {
            livingentity = this.target;
        }
        if (livingentity == null) {
            return false;
        }
        if (!livingentity.isAlive()) {
            return false;
        }
        Team team = this.goalOwner.getTeam();
        Team team1 = livingentity.getTeam();
        if (team != null && team1 == team) {
            return false;
        }
        double d0 = this.getTargetDistance();
        if (this.goalOwner.getDistanceSq(livingentity) > d0 * d0) {
            return false;
        }
        if (this.shouldCheckSight) {
            if (this.goalOwner.getEntitySenses().canSee(livingentity)) {
                this.targetUnseenTicks = 0;
            } else if (++this.targetUnseenTicks > this.unseenMemoryTicks) {
                return false;
            }
        }
        if (livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.disableDamage) {
            return false;
        }
        this.goalOwner.setAttackTarget(livingentity);
        return true;
    }

    protected double getTargetDistance() {
        return this.goalOwner.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    @Override
    public void startExecuting() {
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.targetUnseenTicks = 0;
    }

    @Override
    public void resetTask() {
        this.goalOwner.setAttackTarget(null);
        this.target = null;
    }

    protected boolean isSuitableTarget(@Nullable LivingEntity potentialTarget, EntityPredicate targetPredicate) {
        if (potentialTarget == null) {
            return false;
        }
        if (!targetPredicate.canTarget(this.goalOwner, potentialTarget)) {
            return false;
        }
        if (!this.goalOwner.isWithinHomeDistanceFromPosition(potentialTarget.getPosition())) {
            return false;
        }
        if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
                this.targetSearchStatus = 0;
            }
            if (this.targetSearchStatus == 0) {
                int n = this.targetSearchStatus = this.canEasilyReach(potentialTarget) ? 1 : 2;
            }
            if (this.targetSearchStatus == 2) {
                return false;
            }
        }
        return true;
    }

    private boolean canEasilyReach(LivingEntity target) {
        int j;
        this.targetSearchDelay = 10 + this.goalOwner.getRNG().nextInt(5);
        Path path = this.goalOwner.getNavigator().getPathToEntity(target, 0);
        if (path == null) {
            return false;
        }
        PathPoint pathpoint = path.getFinalPathPoint();
        if (pathpoint == null) {
            return false;
        }
        int i = pathpoint.x - MathHelper.floor(target.getPosX());
        return (double)(i * i + (j = pathpoint.z - MathHelper.floor(target.getPosZ())) * j) <= 2.25;
    }

    public TargetGoal setUnseenMemoryTicks(int unseenMemoryTicksIn) {
        this.unseenMemoryTicks = unseenMemoryTicksIn;
        return this;
    }
}
