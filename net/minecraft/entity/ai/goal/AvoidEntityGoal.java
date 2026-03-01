package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.vector.Vector3d;

public class AvoidEntityGoal<T extends LivingEntity>
extends Goal {
    protected final CreatureEntity entity;
    private final double farSpeed;
    private final double nearSpeed;
    protected T avoidTarget;
    protected final float avoidDistance;
    protected Path path;
    protected final PathNavigator navigation;
    protected final Class<T> classToAvoid;
    protected final Predicate<LivingEntity> avoidTargetSelector;
    protected final Predicate<LivingEntity> field_203784_k;
    private final EntityPredicate builtTargetSelector;

    public AvoidEntityGoal(CreatureEntity entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        this(entityIn, classToAvoidIn, p_200828_0_ -> true, avoidDistanceIn, farSpeedIn, nearSpeedIn, EntityPredicates.CAN_AI_TARGET::test);
    }

    public AvoidEntityGoal(CreatureEntity entityIn, Class<T> avoidClass, Predicate<LivingEntity> targetPredicate, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> p_i48859_9_) {
        this.entity = entityIn;
        this.classToAvoid = avoidClass;
        this.avoidTargetSelector = targetPredicate;
        this.avoidDistance = distance;
        this.farSpeed = nearSpeedIn;
        this.nearSpeed = farSpeedIn;
        this.field_203784_k = p_i48859_9_;
        this.navigation = entityIn.getNavigator();
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        this.builtTargetSelector = new EntityPredicate().setDistance(distance).setCustomPredicate(p_i48859_9_.and(targetPredicate));
    }

    public AvoidEntityGoal(CreatureEntity entityIn, Class<T> avoidClass, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> targetPredicate) {
        this(entityIn, avoidClass, p_203782_0_ -> true, distance, nearSpeedIn, farSpeedIn, targetPredicate);
    }

    @Override
    public boolean shouldExecute() {
        this.avoidTarget = this.entity.world.func_225318_b(this.classToAvoid, this.builtTargetSelector, this.entity, this.entity.getPosX(), this.entity.getPosY(), this.entity.getPosZ(), this.entity.getBoundingBox().grow(this.avoidDistance, 3.0, this.avoidDistance));
        if (this.avoidTarget == null) {
            return false;
        }
        Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, ((Entity)this.avoidTarget).getPositionVec());
        if (vector3d == null) {
            return false;
        }
        if (((Entity)this.avoidTarget).getDistanceSq(vector3d.x, vector3d.y, vector3d.z) < ((Entity)this.avoidTarget).getDistanceSq(this.entity)) {
            return false;
        }
        this.path = this.navigation.getPathToPos(vector3d.x, vector3d.y, vector3d.z, 0);
        return this.path != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.navigation.noPath();
    }

    @Override
    public void startExecuting() {
        this.navigation.setPath(this.path, this.farSpeed);
    }

    @Override
    public void resetTask() {
        this.avoidTarget = null;
    }

    @Override
    public void tick() {
        if (this.entity.getDistanceSq((Entity)this.avoidTarget) < 49.0) {
            this.entity.getNavigator().setSpeed(this.nearSpeed);
        } else {
            this.entity.getNavigator().setSpeed(this.farSpeed);
        }
    }
}
