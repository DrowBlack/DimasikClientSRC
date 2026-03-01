package net.minecraft.entity.passive;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;

public class PolarBearEntity
extends AnimalEntity
implements IAngerable {
    private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.createKey(PolarBearEntity.class, DataSerializers.BOOLEAN);
    private float clientSideStandAnimation0;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    private static final RangedInteger field_234217_by_ = TickRangeConverter.convertRange(20, 39);
    private int field_234218_bz_;
    private UUID field_234216_bA_;

    public PolarBearEntity(EntityType<? extends PolarBearEntity> type, World worldIn) {
        super((EntityType<? extends AnimalEntity>)type, worldIn);
    }

    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return EntityType.POLAR_BEAR.create(p_241840_1_);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal());
        this.goalSelector.addGoal(1, new PanicGoal());
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal());
        this.targetSelector.addGoal(2, new AttackPlayerGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<FoxEntity>(this, FoxEntity.class, 10, true, true, null));
        this.targetSelector.addGoal(5, new ResetAngerGoal<PolarBearEntity>(this, false));
    }

    public static AttributeModifierMap.MutableAttribute func_234219_eI_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 30.0).createMutableAttribute(Attributes.FOLLOW_RANGE, 20.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25).createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0);
    }

    public static boolean func_223320_c(EntityType<PolarBearEntity> p_223320_0_, IWorld p_223320_1_, SpawnReason reason, BlockPos p_223320_3_, Random p_223320_4_) {
        Optional<RegistryKey<Biome>> optional = p_223320_1_.func_242406_i(p_223320_3_);
        if (!Objects.equals(optional, Optional.of(Biomes.FROZEN_OCEAN)) && !Objects.equals(optional, Optional.of(Biomes.DEEP_FROZEN_OCEAN))) {
            return PolarBearEntity.canAnimalSpawn(p_223320_0_, p_223320_1_, reason, p_223320_3_, p_223320_4_);
        }
        return p_223320_1_.getLightSubtracted(p_223320_3_, 0) > 8 && p_223320_1_.getBlockState(p_223320_3_.down()).isIn(Blocks.ICE);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.readAngerNBT((ServerWorld)this.world, compound);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.writeAngerNBT(compound);
    }

    @Override
    public void func_230258_H__() {
        this.setAngerTime(field_234217_by_.getRandomWithinRange(this.rand));
    }

    @Override
    public void setAngerTime(int time) {
        this.field_234218_bz_ = time;
    }

    @Override
    public int getAngerTime() {
        return this.field_234218_bz_;
    }

    @Override
    public void setAngerTarget(@Nullable UUID target) {
        this.field_234216_bA_ = target;
    }

    @Override
    public UUID getAngerTarget() {
        return this.field_234216_bA_;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isChild() ? SoundEvents.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15f, 1.0f);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0f, this.getSoundPitch());
            this.warningSoundTicks = 40;
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(IS_STANDING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isRemote) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimation0) {
                this.recalculateSize();
            }
            this.clientSideStandAnimation0 = this.clientSideStandAnimation;
            this.clientSideStandAnimation = this.isStanding() ? MathHelper.clamp(this.clientSideStandAnimation + 1.0f, 0.0f, 6.0f) : MathHelper.clamp(this.clientSideStandAnimation - 1.0f, 0.0f, 6.0f);
        }
        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }
        if (!this.world.isRemote) {
            this.func_241359_a_((ServerWorld)this.world, true);
        }
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        if (this.clientSideStandAnimation > 0.0f) {
            float f = this.clientSideStandAnimation / 6.0f;
            float f1 = 1.0f + f;
            return super.getSize(poseIn).scale(1.0f, f1);
        }
        return super.getSize(poseIn);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (int)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (flag) {
            this.applyEnchantments(this, entityIn);
        }
        return flag;
    }

    public boolean isStanding() {
        return this.dataManager.get(IS_STANDING);
    }

    public void setStanding(boolean standing) {
        this.dataManager.set(IS_STANDING, standing);
    }

    public float getStandingAnimationScale(float p_189795_1_) {
        return MathHelper.lerp(p_189795_1_, this.clientSideStandAnimation0, this.clientSideStandAnimation) / 6.0f;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98f;
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableEntity.AgeableData(1.0f);
        }
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    class MeleeAttackGoal
    extends net.minecraft.entity.ai.goal.MeleeAttackGoal {
        public MeleeAttackGoal() {
            super(PolarBearEntity.this, 1.25, true);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.func_234040_h_()) {
                this.func_234039_g_();
                this.attacker.attackEntityAsMob(enemy);
                PolarBearEntity.this.setStanding(false);
            } else if (distToEnemySqr <= d0 * 2.0) {
                if (this.func_234040_h_()) {
                    PolarBearEntity.this.setStanding(false);
                    this.func_234039_g_();
                }
                if (this.func_234041_j_() <= 10) {
                    PolarBearEntity.this.setStanding(true);
                    PolarBearEntity.this.playWarningSound();
                }
            } else {
                this.func_234039_g_();
                PolarBearEntity.this.setStanding(false);
            }
        }

        @Override
        public void resetTask() {
            PolarBearEntity.this.setStanding(false);
            super.resetTask();
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return 4.0f + attackTarget.getWidth();
        }
    }

    class PanicGoal
    extends net.minecraft.entity.ai.goal.PanicGoal {
        public PanicGoal() {
            super(PolarBearEntity.this, 2.0);
        }

        @Override
        public boolean shouldExecute() {
            return !PolarBearEntity.this.isChild() && !PolarBearEntity.this.isBurning() ? false : super.shouldExecute();
        }
    }

    class HurtByTargetGoal
    extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(PolarBearEntity.this, new Class[0]);
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            if (PolarBearEntity.this.isChild()) {
                this.alertOthers();
                this.resetTask();
            }
        }

        @Override
        protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
            if (mobIn instanceof PolarBearEntity && !mobIn.isChild()) {
                super.setAttackTarget(mobIn, targetIn);
            }
        }
    }

    class AttackPlayerGoal
    extends NearestAttackableTargetGoal<PlayerEntity> {
        public AttackPlayerGoal() {
            super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, null);
        }

        @Override
        public boolean shouldExecute() {
            if (PolarBearEntity.this.isChild()) {
                return false;
            }
            if (super.shouldExecute()) {
                for (PolarBearEntity polarbearentity : PolarBearEntity.this.world.getEntitiesWithinAABB(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().grow(8.0, 4.0, 8.0))) {
                    if (!polarbearentity.isChild()) continue;
                    return true;
                }
            }
            return false;
        }

        @Override
        protected double getTargetDistance() {
            return super.getTargetDistance() * 0.5;
        }
    }
}
