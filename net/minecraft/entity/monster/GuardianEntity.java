package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class GuardianEntity
extends MonsterEntity {
    private static final DataParameter<Boolean> MOVING = EntityDataManager.createKey(GuardianEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(GuardianEntity.class, DataSerializers.VARINT);
    private float clientSideTailAnimation;
    private float clientSideTailAnimationO;
    private float clientSideTailAnimationSpeed;
    private float clientSideSpikesAnimation;
    private float clientSideSpikesAnimationO;
    private LivingEntity targetedEntity;
    private int clientSideAttackTime;
    private boolean clientSideTouchedGround;
    protected RandomWalkingGoal wander;

    public GuardianEntity(EntityType<? extends GuardianEntity> type, World worldIn) {
        super((EntityType<? extends MonsterEntity>)type, worldIn);
        this.experienceValue = 10;
        this.setPathPriority(PathNodeType.WATER, 0.0f);
        this.moveController = new MoveHelperController(this);
        this.clientSideTailAnimationO = this.clientSideTailAnimation = this.rand.nextFloat();
    }

    @Override
    protected void registerGoals() {
        MoveTowardsRestrictionGoal movetowardsrestrictiongoal = new MoveTowardsRestrictionGoal(this, 1.0);
        this.wander = new RandomWalkingGoal(this, 1.0, 80);
        this.goalSelector.addGoal(4, new AttackGoal(this));
        this.goalSelector.addGoal(5, movetowardsrestrictiongoal);
        this.goalSelector.addGoal(7, this.wander);
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.addGoal(8, new LookAtGoal(this, GuardianEntity.class, 12.0f, 0.01f));
        this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
        this.wander.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        movetowardsrestrictiongoal.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 10, true, false, new TargetPredicate(this)));
    }

    public static AttributeModifierMap.MutableAttribute func_234292_eK_() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5).createMutableAttribute(Attributes.FOLLOW_RANGE, 16.0).createMutableAttribute(Attributes.MAX_HEALTH, 30.0);
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return new SwimmerPathNavigator(this, worldIn);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(MOVING, false);
        this.dataManager.register(TARGET_ENTITY, 0);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.WATER;
    }

    public boolean isMoving() {
        return this.dataManager.get(MOVING);
    }

    private void setMoving(boolean moving) {
        this.dataManager.set(MOVING, moving);
    }

    public int getAttackDuration() {
        return 80;
    }

    private void setTargetedEntity(int entityId) {
        this.dataManager.set(TARGET_ENTITY, entityId);
    }

    public boolean hasTargetedEntity() {
        return this.dataManager.get(TARGET_ENTITY) != 0;
    }

    @Nullable
    public LivingEntity getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.world.isRemote) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.world.getEntityByID(this.dataManager.get(TARGET_ENTITY));
            if (entity instanceof LivingEntity) {
                this.targetedEntity = (LivingEntity)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getAttackTarget();
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (TARGET_ENTITY.equals(key)) {
            this.clientSideAttackTime = 0;
            this.targetedEntity = null;
        }
    }

    @Override
    public int getTalkInterval() {
        return 160;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_AMBIENT : SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_HURT : SoundEvents.ENTITY_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_DEATH : SoundEvents.ENTITY_GUARDIAN_DEATH_LAND;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.5f;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return worldIn.getFluidState(pos).isTagged(FluidTags.WATER) ? 10.0f + worldIn.getBrightness(pos) - 0.5f : super.getBlockPathWeight(pos, worldIn);
    }

    @Override
    public void livingTick() {
        if (this.isAlive()) {
            if (this.world.isRemote) {
                this.clientSideTailAnimationO = this.clientSideTailAnimation;
                if (!this.isInWater()) {
                    this.clientSideTailAnimationSpeed = 2.0f;
                    Vector3d vector3d = this.getMotion();
                    if (vector3d.y > 0.0 && this.clientSideTouchedGround && !this.isSilent()) {
                        this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), this.getFlopSound(), this.getSoundCategory(), 1.0f, 1.0f, false);
                    }
                    this.clientSideTouchedGround = vector3d.y < 0.0 && this.world.isTopSolid(this.getPosition().down(), this);
                } else {
                    this.clientSideTailAnimationSpeed = this.isMoving() ? (this.clientSideTailAnimationSpeed < 0.5f ? 4.0f : (this.clientSideTailAnimationSpeed += (0.5f - this.clientSideTailAnimationSpeed) * 0.1f)) : (this.clientSideTailAnimationSpeed += (0.125f - this.clientSideTailAnimationSpeed) * 0.2f);
                }
                this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
                this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
                this.clientSideSpikesAnimation = !this.isInWaterOrBubbleColumn() ? this.rand.nextFloat() : (this.isMoving() ? (this.clientSideSpikesAnimation += (0.0f - this.clientSideSpikesAnimation) * 0.25f) : (this.clientSideSpikesAnimation += (1.0f - this.clientSideSpikesAnimation) * 0.06f));
                if (this.isMoving() && this.isInWater()) {
                    Vector3d vector3d1 = this.getLook(0.0f);
                    for (int i = 0; i < 2; ++i) {
                        this.world.addParticle(ParticleTypes.BUBBLE, this.getPosXRandom(0.5) - vector3d1.x * 1.5, this.getPosYRandom() - vector3d1.y * 1.5, this.getPosZRandom(0.5) - vector3d1.z * 1.5, 0.0, 0.0, 0.0);
                    }
                }
                if (this.hasTargetedEntity()) {
                    LivingEntity livingentity;
                    if (this.clientSideAttackTime < this.getAttackDuration()) {
                        ++this.clientSideAttackTime;
                    }
                    if ((livingentity = this.getTargetedEntity()) != null) {
                        this.getLookController().setLookPositionWithEntity(livingentity, 90.0f, 90.0f);
                        this.getLookController().tick();
                        double d5 = this.getAttackAnimationScale(0.0f);
                        double d0 = livingentity.getPosX() - this.getPosX();
                        double d1 = livingentity.getPosYHeight(0.5) - this.getPosYEye();
                        double d2 = livingentity.getPosZ() - this.getPosZ();
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        double d4 = this.rand.nextDouble();
                        while (d4 < d3) {
                            this.world.addParticle(ParticleTypes.BUBBLE, this.getPosX() + d0 * (d4 += 1.8 - d5 + this.rand.nextDouble() * (1.7 - d5)), this.getPosYEye() + d1 * d4, this.getPosZ() + d2 * d4, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
            if (this.isInWaterOrBubbleColumn()) {
                this.setAir(300);
            } else if (this.onGround) {
                this.setMotion(this.getMotion().add((this.rand.nextFloat() * 2.0f - 1.0f) * 0.4f, 0.5, (this.rand.nextFloat() * 2.0f - 1.0f) * 0.4f));
                this.rotationYaw = this.rand.nextFloat() * 360.0f;
                this.onGround = false;
                this.isAirBorne = true;
            }
            if (this.hasTargetedEntity()) {
                this.rotationYaw = this.rotationYawHead;
            }
        }
        super.livingTick();
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_GUARDIAN_FLOP;
    }

    public float getTailAnimation(float p_175471_1_) {
        return MathHelper.lerp(p_175471_1_, this.clientSideTailAnimationO, this.clientSideTailAnimation);
    }

    public float getSpikesAnimation(float p_175469_1_) {
        return MathHelper.lerp(p_175469_1_, this.clientSideSpikesAnimationO, this.clientSideSpikesAnimation);
    }

    public float getAttackAnimationScale(float p_175477_1_) {
        return ((float)this.clientSideAttackTime + p_175477_1_) / (float)this.getAttackDuration();
    }

    @Override
    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    public static boolean func_223329_b(EntityType<? extends GuardianEntity> p_223329_0_, IWorld p_223329_1_, SpawnReason reason, BlockPos p_223329_3_, Random p_223329_4_) {
        return !(p_223329_4_.nextInt(20) != 0 && p_223329_1_.canBlockSeeSky(p_223329_3_) || p_223329_1_.getDifficulty() == Difficulty.PEACEFUL || reason != SpawnReason.SPAWNER && !p_223329_1_.getFluidState(p_223329_3_).isTagged(FluidTags.WATER));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.isMoving() && !source.isMagicDamage() && source.getImmediateSource() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)source.getImmediateSource();
            if (!source.isExplosion()) {
                livingentity.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0f);
            }
        }
        if (this.wander != null) {
            this.wander.makeUpdate();
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public int getVerticalFaceSpeed() {
        return 180;
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(0.1f, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9));
            if (!this.isMoving() && this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(travelVector);
        }
    }

    static class MoveHelperController
    extends MovementController {
        private final GuardianEntity entityGuardian;

        public MoveHelperController(GuardianEntity guardian) {
            super(guardian);
            this.entityGuardian = guardian;
        }

        @Override
        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO && !this.entityGuardian.getNavigator().noPath()) {
                Vector3d vector3d = new Vector3d(this.posX - this.entityGuardian.getPosX(), this.posY - this.entityGuardian.getPosY(), this.posZ - this.entityGuardian.getPosZ());
                double d0 = vector3d.length();
                double d1 = vector3d.x / d0;
                double d2 = vector3d.y / d0;
                double d3 = vector3d.z / d0;
                float f = (float)(MathHelper.atan2(vector3d.z, vector3d.x) * 57.2957763671875) - 90.0f;
                this.entityGuardian.renderYawOffset = this.entityGuardian.rotationYaw = this.limitAngle(this.entityGuardian.rotationYaw, f, 90.0f);
                float f1 = (float)(this.speed * this.entityGuardian.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = MathHelper.lerp(0.125f, this.entityGuardian.getAIMoveSpeed(), f1);
                this.entityGuardian.setAIMoveSpeed(f2);
                double d4 = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.5) * 0.05;
                double d5 = Math.cos(this.entityGuardian.rotationYaw * ((float)Math.PI / 180));
                double d6 = Math.sin(this.entityGuardian.rotationYaw * ((float)Math.PI / 180));
                double d7 = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.75) * 0.05;
                this.entityGuardian.setMotion(this.entityGuardian.getMotion().add(d4 * d5, d7 * (d6 + d5) * 0.25 + (double)f2 * d2 * 0.1, d4 * d6));
                LookController lookcontroller = this.entityGuardian.getLookController();
                double d8 = this.entityGuardian.getPosX() + d1 * 2.0;
                double d9 = this.entityGuardian.getPosYEye() + d2 / d0;
                double d10 = this.entityGuardian.getPosZ() + d3 * 2.0;
                double d11 = lookcontroller.getLookPosX();
                double d12 = lookcontroller.getLookPosY();
                double d13 = lookcontroller.getLookPosZ();
                if (!lookcontroller.getIsLooking()) {
                    d11 = d8;
                    d12 = d9;
                    d13 = d10;
                }
                this.entityGuardian.getLookController().setLookPosition(MathHelper.lerp(0.125, d11, d8), MathHelper.lerp(0.125, d12, d9), MathHelper.lerp(0.125, d13, d10), 10.0f, 40.0f);
                this.entityGuardian.setMoving(true);
            } else {
                this.entityGuardian.setAIMoveSpeed(0.0f);
                this.entityGuardian.setMoving(false);
            }
        }
    }

    static class AttackGoal
    extends Goal {
        private final GuardianEntity guardian;
        private int tickCounter;
        private final boolean isElder;

        public AttackGoal(GuardianEntity guardian) {
            this.guardian = guardian;
            this.isElder = guardian instanceof ElderGuardianEntity;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean shouldExecute() {
            LivingEntity livingentity = this.guardian.getAttackTarget();
            return livingentity != null && livingentity.isAlive();
        }

        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && (this.isElder || this.guardian.getDistanceSq(this.guardian.getAttackTarget()) > 9.0);
        }

        @Override
        public void startExecuting() {
            this.tickCounter = -10;
            this.guardian.getNavigator().clearPath();
            this.guardian.getLookController().setLookPositionWithEntity(this.guardian.getAttackTarget(), 90.0f, 90.0f);
            this.guardian.isAirBorne = true;
        }

        @Override
        public void resetTask() {
            this.guardian.setTargetedEntity(0);
            this.guardian.setAttackTarget(null);
            this.guardian.wander.makeUpdate();
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.guardian.getAttackTarget();
            this.guardian.getNavigator().clearPath();
            this.guardian.getLookController().setLookPositionWithEntity(livingentity, 90.0f, 90.0f);
            if (!this.guardian.canEntityBeSeen(livingentity)) {
                this.guardian.setAttackTarget(null);
            } else {
                ++this.tickCounter;
                if (this.tickCounter == 0) {
                    this.guardian.setTargetedEntity(this.guardian.getAttackTarget().getEntityId());
                    if (!this.guardian.isSilent()) {
                        this.guardian.world.setEntityState(this.guardian, (byte)21);
                    }
                } else if (this.tickCounter >= this.guardian.getAttackDuration()) {
                    float f = 1.0f;
                    if (this.guardian.world.getDifficulty() == Difficulty.HARD) {
                        f += 2.0f;
                    }
                    if (this.isElder) {
                        f += 2.0f;
                    }
                    livingentity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.guardian, this.guardian), f);
                    livingentity.attackEntityFrom(DamageSource.causeMobDamage(this.guardian), (float)this.guardian.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    this.guardian.setAttackTarget(null);
                }
                super.tick();
            }
        }
    }

    static class TargetPredicate
    implements Predicate<LivingEntity> {
        private final GuardianEntity parentEntity;

        public TargetPredicate(GuardianEntity guardian) {
            this.parentEntity = guardian;
        }

        @Override
        public boolean test(@Nullable LivingEntity p_test_1_) {
            return (p_test_1_ instanceof PlayerEntity || p_test_1_ instanceof SquidEntity) && p_test_1_.getDistanceSq(this.parentEntity) > 9.0;
        }
    }
}
