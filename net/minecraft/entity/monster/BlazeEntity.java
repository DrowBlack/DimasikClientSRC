package net.minecraft.entity.monster;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BlazeEntity
extends MonsterEntity {
    private float heightOffset = 0.5f;
    private int heightOffsetUpdateTime;
    private static final DataParameter<Byte> ON_FIRE = EntityDataManager.createKey(BlazeEntity.class, DataSerializers.BYTE);

    public BlazeEntity(EntityType<? extends BlazeEntity> type, World world) {
        super((EntityType<? extends MonsterEntity>)type, world);
        this.setPathPriority(PathNodeType.WATER, -1.0f);
        this.setPathPriority(PathNodeType.LAVA, 8.0f);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0f);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0f);
        this.experienceValue = 10;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new FireballAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal((CreatureEntity)this, 1.0, 0.0f));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setCallsForHelp(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23f).createMutableAttribute(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ON_FIRE, (byte)0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    public float getBrightness() {
        return 1.0f;
    }

    @Override
    public void livingTick() {
        if (!this.onGround && this.getMotion().y < 0.0) {
            this.setMotion(this.getMotion().mul(1.0, 0.6, 1.0));
        }
        if (this.world.isRemote) {
            if (this.rand.nextInt(24) == 0 && !this.isSilent()) {
                this.world.playSound(this.getPosX() + 0.5, this.getPosY() + 0.5, this.getPosZ() + 0.5, SoundEvents.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0f + this.rand.nextFloat(), this.rand.nextFloat() * 0.7f + 0.3f, false);
            }
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.getPosXRandom(0.5), this.getPosYRandom(), this.getPosZRandom(0.5), 0.0, 0.0, 0.0);
            }
        }
        super.livingTick();
    }

    @Override
    public boolean isWaterSensitive() {
        return true;
    }

    @Override
    protected void updateAITasks() {
        LivingEntity livingentity;
        --this.heightOffsetUpdateTime;
        if (this.heightOffsetUpdateTime <= 0) {
            this.heightOffsetUpdateTime = 100;
            this.heightOffset = 0.5f + (float)this.rand.nextGaussian() * 3.0f;
        }
        if ((livingentity = this.getAttackTarget()) != null && livingentity.getPosYEye() > this.getPosYEye() + (double)this.heightOffset && this.canAttack(livingentity)) {
            Vector3d vector3d = this.getMotion();
            this.setMotion(this.getMotion().add(0.0, ((double)0.3f - vector3d.y) * (double)0.3f, 0.0));
            this.isAirBorne = true;
        }
        super.updateAITasks();
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    public boolean isBurning() {
        return this.isCharged();
    }

    private boolean isCharged() {
        return (this.dataManager.get(ON_FIRE) & 1) != 0;
    }

    private void setOnFire(boolean onFire) {
        byte b0 = this.dataManager.get(ON_FIRE);
        b0 = onFire ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE);
        this.dataManager.set(ON_FIRE, b0);
    }

    static class FireballAttackGoal
    extends Goal {
        private final BlazeEntity blaze;
        private int attackStep;
        private int attackTime;
        private int firedRecentlyTimer;

        public FireballAttackGoal(BlazeEntity blazeIn) {
            this.blaze = blazeIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean shouldExecute() {
            LivingEntity livingentity = this.blaze.getAttackTarget();
            return livingentity != null && livingentity.isAlive() && this.blaze.canAttack(livingentity);
        }

        @Override
        public void startExecuting() {
            this.attackStep = 0;
        }

        @Override
        public void resetTask() {
            this.blaze.setOnFire(false);
            this.firedRecentlyTimer = 0;
        }

        @Override
        public void tick() {
            --this.attackTime;
            LivingEntity livingentity = this.blaze.getAttackTarget();
            if (livingentity != null) {
                boolean flag = this.blaze.getEntitySenses().canSee(livingentity);
                this.firedRecentlyTimer = flag ? 0 : ++this.firedRecentlyTimer;
                double d0 = this.blaze.getDistanceSq(livingentity);
                if (d0 < 4.0) {
                    if (!flag) {
                        return;
                    }
                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.blaze.attackEntityAsMob(livingentity);
                    }
                    this.blaze.getMoveHelper().setMoveTo(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), 1.0);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    double d1 = livingentity.getPosX() - this.blaze.getPosX();
                    double d2 = livingentity.getPosYHeight(0.5) - this.blaze.getPosYHeight(0.5);
                    double d3 = livingentity.getPosZ() - this.blaze.getPosZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                            this.blaze.setOnFire(true);
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                            this.blaze.setOnFire(false);
                        }
                        if (this.attackStep > 1) {
                            float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5f;
                            if (!this.blaze.isSilent()) {
                                this.blaze.world.playEvent(null, 1018, this.blaze.getPosition(), 0);
                            }
                            for (int i = 0; i < 1; ++i) {
                                SmallFireballEntity smallfireballentity = new SmallFireballEntity(this.blaze.world, this.blaze, d1 + this.blaze.getRNG().nextGaussian() * (double)f, d2, d3 + this.blaze.getRNG().nextGaussian() * (double)f);
                                smallfireballentity.setPosition(smallfireballentity.getPosX(), this.blaze.getPosYHeight(0.5) + 0.5, smallfireballentity.getPosZ());
                                this.blaze.world.addEntity(smallfireballentity);
                            }
                        }
                    }
                    this.blaze.getLookController().setLookPositionWithEntity(livingentity, 10.0f, 10.0f);
                } else if (this.firedRecentlyTimer < 5) {
                    this.blaze.getMoveHelper().setMoveTo(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), 1.0);
                }
                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}
