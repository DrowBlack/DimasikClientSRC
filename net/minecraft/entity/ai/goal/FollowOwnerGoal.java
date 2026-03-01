package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class FollowOwnerGoal
extends Goal {
    private final TameableEntity tameable;
    private LivingEntity owner;
    private final IWorldReader world;
    private final double followSpeed;
    private final PathNavigator navigator;
    private int timeToRecalcPath;
    private final float maxDist;
    private final float minDist;
    private float oldWaterCost;
    private final boolean teleportToLeaves;

    public FollowOwnerGoal(TameableEntity tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
        this.tameable = tameable;
        this.world = tameable.world;
        this.followSpeed = speed;
        this.navigator = tameable.getNavigator();
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.teleportToLeaves = teleportToLeaves;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(tameable.getNavigator() instanceof GroundPathNavigator) && !(tameable.getNavigator() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity livingentity = this.tameable.getOwner();
        if (livingentity == null) {
            return false;
        }
        if (livingentity.isSpectator()) {
            return false;
        }
        if (this.tameable.isSitting()) {
            return false;
        }
        if (this.tameable.getDistanceSq(livingentity) < (double)(this.minDist * this.minDist)) {
            return false;
        }
        this.owner = livingentity;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.navigator.noPath()) {
            return false;
        }
        if (this.tameable.isSitting()) {
            return false;
        }
        return !(this.tameable.getDistanceSq(this.owner) <= (double)(this.maxDist * this.maxDist));
    }

    @Override
    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
        this.tameable.setPathPriority(PathNodeType.WATER, 0.0f);
    }

    @Override
    public void resetTask() {
        this.owner = null;
        this.navigator.clearPath();
        this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        this.tameable.getLookController().setLookPositionWithEntity(this.owner, 10.0f, this.tameable.getVerticalFaceSpeed());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.tameable.getLeashed() && !this.tameable.isPassenger()) {
                if (this.tameable.getDistanceSq(this.owner) >= 144.0) {
                    this.tryToTeleportNearEntity();
                } else {
                    this.navigator.tryMoveToEntityLiving(this.owner, this.followSpeed);
                }
            }
        }
    }

    private void tryToTeleportNearEntity() {
        BlockPos blockpos = this.owner.getPosition();
        for (int i = 0; i < 10; ++i) {
            int j = this.getRandomNumber(-3, 3);
            int k = this.getRandomNumber(-1, 1);
            int l = this.getRandomNumber(-3, 3);
            boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (!flag) continue;
            return;
        }
    }

    private boolean tryToTeleportToLocation(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getPosX()) < 2.0 && Math.abs((double)z - this.owner.getPosZ()) < 2.0) {
            return false;
        }
        if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
            return false;
        }
        this.tameable.setLocationAndAngles((double)x + 0.5, y, (double)z + 0.5, this.tameable.rotationYaw, this.tameable.rotationPitch);
        this.navigator.clearPath();
        return true;
    }

    private boolean isTeleportFriendlyBlock(BlockPos pos) {
        PathNodeType pathnodetype = WalkNodeProcessor.func_237231_a_(this.world, pos.toMutable());
        if (pathnodetype != PathNodeType.WALKABLE) {
            return false;
        }
        BlockState blockstate = this.world.getBlockState(pos.down());
        if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
            return false;
        }
        BlockPos blockpos = pos.subtract(this.tameable.getPosition());
        return this.world.hasNoCollisions(this.tameable, this.tameable.getBoundingBox().offset(blockpos));
    }

    private int getRandomNumber(int min, int max) {
        return this.tameable.getRNG().nextInt(max - min + 1) + min;
    }
}
