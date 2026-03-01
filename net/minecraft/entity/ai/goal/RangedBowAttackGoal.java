package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;

public class RangedBowAttackGoal<T extends MonsterEntity>
extends Goal {
    private final T entity;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public RangedBowAttackGoal(T mob, double moveSpeedAmpIn, int attackCooldownIn, float maxAttackDistanceIn) {
        this.entity = mob;
        this.moveSpeedAmp = moveSpeedAmpIn;
        this.attackCooldown = attackCooldownIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setAttackCooldown(int attackCooldownIn) {
        this.attackCooldown = attackCooldownIn;
    }

    @Override
    public boolean shouldExecute() {
        return ((MobEntity)this.entity).getAttackTarget() == null ? false : this.isBowInMainhand();
    }

    protected boolean isBowInMainhand() {
        return ((LivingEntity)this.entity).canEquip(Items.BOW);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (this.shouldExecute() || !((MobEntity)this.entity).getNavigator().noPath()) && this.isBowInMainhand();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        ((MobEntity)this.entity).setAggroed(true);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        ((MobEntity)this.entity).setAggroed(false);
        this.seeTime = 0;
        this.attackTime = -1;
        ((LivingEntity)this.entity).resetActiveHand();
    }

    @Override
    public void tick() {
        LivingEntity livingentity = ((MobEntity)this.entity).getAttackTarget();
        if (livingentity != null) {
            boolean flag1;
            double d0 = ((Entity)this.entity).getDistanceSq(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ());
            boolean flag = ((MobEntity)this.entity).getEntitySenses().canSee(livingentity);
            boolean bl = flag1 = this.seeTime > 0;
            if (flag != flag1) {
                this.seeTime = 0;
            }
            this.seeTime = flag ? ++this.seeTime : --this.seeTime;
            if (!(d0 > (double)this.maxAttackDistance) && this.seeTime >= 20) {
                ((MobEntity)this.entity).getNavigator().clearPath();
                ++this.strafingTime;
            } else {
                ((MobEntity)this.entity).getNavigator().tryMoveToEntityLiving(livingentity, this.moveSpeedAmp);
                this.strafingTime = -1;
            }
            if (this.strafingTime >= 20) {
                if ((double)((LivingEntity)this.entity).getRNG().nextFloat() < 0.3) {
                    boolean bl2 = this.strafingClockwise = !this.strafingClockwise;
                }
                if ((double)((LivingEntity)this.entity).getRNG().nextFloat() < 0.3) {
                    this.strafingBackwards = !this.strafingBackwards;
                }
                this.strafingTime = 0;
            }
            if (this.strafingTime > -1) {
                if (d0 > (double)(this.maxAttackDistance * 0.75f)) {
                    this.strafingBackwards = false;
                } else if (d0 < (double)(this.maxAttackDistance * 0.25f)) {
                    this.strafingBackwards = true;
                }
                ((MobEntity)this.entity).getMoveHelper().strafe(this.strafingBackwards ? -0.5f : 0.5f, this.strafingClockwise ? 0.5f : -0.5f);
                ((MobEntity)this.entity).faceEntity(livingentity, 30.0f, 30.0f);
            } else {
                ((MobEntity)this.entity).getLookController().setLookPositionWithEntity(livingentity, 30.0f, 30.0f);
            }
            if (((LivingEntity)this.entity).isHandActive()) {
                int i;
                if (!flag && this.seeTime < -60) {
                    ((LivingEntity)this.entity).resetActiveHand();
                } else if (flag && (i = ((LivingEntity)this.entity).getItemInUseMaxCount()) >= 20) {
                    ((LivingEntity)this.entity).resetActiveHand();
                    ((IRangedAttackMob)this.entity).attackEntityWithRangedAttack(livingentity, BowItem.getArrowVelocity(i));
                    this.attackTime = this.attackCooldown;
                }
            } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
                ((LivingEntity)this.entity).setActiveHand(ProjectileHelper.getHandWith(this.entity, Items.BOW));
            }
        }
    }
}
