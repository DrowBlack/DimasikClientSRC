package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;

public class LookAtGoal
extends Goal {
    protected final MobEntity entity;
    protected Entity closestEntity;
    protected final float maxDistance;
    private int lookTime;
    protected final float chance;
    protected final Class<? extends LivingEntity> watchedClass;
    protected final EntityPredicate field_220716_e;

    public LookAtGoal(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance) {
        this(entityIn, watchTargetClass, maxDistance, 0.02f);
    }

    public LookAtGoal(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, float chanceIn) {
        this.entity = entityIn;
        this.watchedClass = watchTargetClass;
        this.maxDistance = maxDistance;
        this.chance = chanceIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
        this.field_220716_e = watchTargetClass == PlayerEntity.class ? new EntityPredicate().setDistance(maxDistance).allowFriendlyFire().allowInvulnerable().setSkipAttackChecks().setCustomPredicate(target -> EntityPredicates.notRiding(entityIn).test((Entity)target)) : new EntityPredicate().setDistance(maxDistance).allowFriendlyFire().allowInvulnerable().setSkipAttackChecks();
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.getRNG().nextFloat() >= this.chance) {
            return false;
        }
        if (this.entity.getAttackTarget() != null) {
            this.closestEntity = this.entity.getAttackTarget();
        }
        this.closestEntity = this.watchedClass == PlayerEntity.class ? this.entity.world.getClosestPlayer(this.field_220716_e, this.entity, this.entity.getPosX(), this.entity.getPosYEye(), this.entity.getPosZ()) : this.entity.world.func_225318_b(this.watchedClass, this.field_220716_e, this.entity, this.entity.getPosX(), this.entity.getPosYEye(), this.entity.getPosZ(), this.entity.getBoundingBox().grow(this.maxDistance, 3.0, this.maxDistance));
        return this.closestEntity != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (!this.closestEntity.isAlive()) {
            return false;
        }
        if (this.entity.getDistanceSq(this.closestEntity) > (double)(this.maxDistance * this.maxDistance)) {
            return false;
        }
        return this.lookTime > 0;
    }

    @Override
    public void startExecuting() {
        this.lookTime = 40 + this.entity.getRNG().nextInt(40);
    }

    @Override
    public void resetTask() {
        this.closestEntity = null;
    }

    @Override
    public void tick() {
        this.entity.getLookController().setLookPosition(this.closestEntity.getPosX(), this.closestEntity.getPosYEye(), this.closestEntity.getPosZ());
        --this.lookTime;
    }
}
