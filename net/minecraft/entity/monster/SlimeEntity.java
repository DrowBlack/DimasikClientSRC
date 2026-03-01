package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

public class SlimeEntity
extends MobEntity
implements IMob {
    private static final DataParameter<Integer> SLIME_SIZE = EntityDataManager.createKey(SlimeEntity.class, DataSerializers.VARINT);
    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;
    private boolean wasOnGround;

    public SlimeEntity(EntityType<? extends SlimeEntity> type, World worldIn) {
        super((EntityType<? extends MobEntity>)type, worldIn);
        this.moveController = new MoveHelperController(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AttackGoal(this));
        this.goalSelector.addGoal(3, new FaceRandomGoal(this));
        this.goalSelector.addGoal(5, new HopGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, p_213811_1_ -> Math.abs(p_213811_1_.getPosY() - this.getPosY()) <= 4.0));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(SLIME_SIZE, 1);
    }

    protected void setSlimeSize(int size, boolean resetHealth) {
        this.dataManager.set(SLIME_SIZE, size);
        this.recenterBoundingBox();
        this.recalculateSize();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(size * size);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2f + 0.1f * (float)size);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(size);
        if (resetHealth) {
            this.setHealth(this.getMaxHealth());
        }
        this.experienceValue = size;
    }

    public int getSlimeSize() {
        return this.dataManager.get(SLIME_SIZE);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Size", this.getSlimeSize() - 1);
        compound.putBoolean("wasOnGround", this.wasOnGround);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        int i = compound.getInt("Size");
        if (i < 0) {
            i = 0;
        }
        this.setSlimeSize(i + 1, false);
        super.readAdditional(compound);
        this.wasOnGround = compound.getBoolean("wasOnGround");
    }

    public boolean isSmallSlime() {
        return this.getSlimeSize() <= 1;
    }

    protected IParticleData getSquishParticle() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    protected boolean isDespawnPeaceful() {
        return this.getSlimeSize() > 0;
    }

    @Override
    public void tick() {
        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5f;
        this.prevSquishFactor = this.squishFactor;
        super.tick();
        if (this.onGround && !this.wasOnGround) {
            int i = this.getSlimeSize();
            for (int j = 0; j < i * 8; ++j) {
                float f = this.rand.nextFloat() * ((float)Math.PI * 2);
                float f1 = this.rand.nextFloat() * 0.5f + 0.5f;
                float f2 = MathHelper.sin(f) * (float)i * 0.5f * f1;
                float f3 = MathHelper.cos(f) * (float)i * 0.5f * f1;
                this.world.addParticle(this.getSquishParticle(), this.getPosX() + (double)f2, this.getPosY(), this.getPosZ() + (double)f3, 0.0, 0.0, 0.0);
            }
            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) / 0.8f);
            this.squishAmount = -0.5f;
        } else if (!this.onGround && this.wasOnGround) {
            this.squishAmount = 1.0f;
        }
        this.wasOnGround = this.onGround;
        this.alterSquishAmount();
    }

    protected void alterSquishAmount() {
        this.squishAmount *= 0.6f;
    }

    protected int getJumpDelay() {
        return this.rand.nextInt(20) + 10;
    }

    @Override
    public void recalculateSize() {
        double d0 = this.getPosX();
        double d1 = this.getPosY();
        double d2 = this.getPosZ();
        super.recalculateSize();
        this.setPosition(d0, d1, d2);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SLIME_SIZE.equals(key)) {
            this.recalculateSize();
            this.rotationYaw = this.rotationYawHead;
            this.renderYawOffset = this.rotationYawHead;
            if (this.isInWater() && this.rand.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }
        super.notifyDataManagerChange(key);
    }

    public EntityType<? extends SlimeEntity> getType() {
        return super.getType();
    }

    @Override
    public void remove() {
        int i = this.getSlimeSize();
        if (!this.world.isRemote && i > 1 && this.getShouldBeDead()) {
            ITextComponent itextcomponent = this.getCustomName();
            boolean flag = this.isAIDisabled();
            float f = (float)i / 4.0f;
            int j = i / 2;
            int k = 2 + this.rand.nextInt(3);
            for (int l = 0; l < k; ++l) {
                float f1 = ((float)(l % 2) - 0.5f) * f;
                float f2 = ((float)(l / 2) - 0.5f) * f;
                SlimeEntity slimeentity = this.getType().create(this.world);
                if (this.isNoDespawnRequired()) {
                    slimeentity.enablePersistence();
                }
                slimeentity.setCustomName(itextcomponent);
                slimeentity.setNoAI(flag);
                slimeentity.setInvulnerable(this.isInvulnerable());
                slimeentity.setSlimeSize(j, true);
                slimeentity.setLocationAndAngles(this.getPosX() + (double)f1, this.getPosY() + 0.5, this.getPosZ() + (double)f2, this.rand.nextFloat() * 360.0f, 0.0f);
                this.world.addEntity(slimeentity);
            }
        }
        super.remove();
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        super.applyEntityCollision(entityIn);
        if (entityIn instanceof IronGolemEntity && this.canDamagePlayer()) {
            this.dealDamage((LivingEntity)entityIn);
        }
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        if (this.canDamagePlayer()) {
            this.dealDamage(entityIn);
        }
    }

    protected void dealDamage(LivingEntity entityIn) {
        if (this.isAlive()) {
            int i = this.getSlimeSize();
            if (this.getDistanceSq(entityIn) < 0.6 * (double)i * 0.6 * (double)i && this.canEntityBeSeen(entityIn) && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_225512_er_())) {
                this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
                this.applyEnchantments(this, entityIn);
            }
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.625f * sizeIn.height;
    }

    protected boolean canDamagePlayer() {
        return !this.isSmallSlime() && this.isServerWorld();
    }

    protected float func_225512_er_() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_HURT_SMALL : SoundEvents.ENTITY_SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_DEATH_SMALL : SoundEvents.ENTITY_SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_SQUISH_SMALL : SoundEvents.ENTITY_SLIME_SQUISH;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return this.getSlimeSize() == 1 ? this.getType().getLootTable() : LootTables.EMPTY;
    }

    public static boolean func_223366_c(EntityType<SlimeEntity> p_223366_0_, IWorld p_223366_1_, SpawnReason reason, BlockPos p_223366_3_, Random randomIn) {
        if (p_223366_1_.getDifficulty() != Difficulty.PEACEFUL) {
            boolean flag;
            if (Objects.equals(p_223366_1_.func_242406_i(p_223366_3_), Optional.of(Biomes.SWAMP)) && p_223366_3_.getY() > 50 && p_223366_3_.getY() < 70 && randomIn.nextFloat() < 0.5f && randomIn.nextFloat() < p_223366_1_.getMoonFactor() && p_223366_1_.getLight(p_223366_3_) <= randomIn.nextInt(8)) {
                return SlimeEntity.canSpawnOn(p_223366_0_, p_223366_1_, reason, p_223366_3_, randomIn);
            }
            if (!(p_223366_1_ instanceof ISeedReader)) {
                return false;
            }
            ChunkPos chunkpos = new ChunkPos(p_223366_3_);
            boolean bl = flag = SharedSeedRandom.seedSlimeChunk(chunkpos.x, chunkpos.z, ((ISeedReader)p_223366_1_).getSeed(), 987234911L).nextInt(10) == 0;
            if (randomIn.nextInt(10) == 0 && flag && p_223366_3_.getY() < 40) {
                return SlimeEntity.canSpawnOn(p_223366_0_, p_223366_1_, reason, p_223366_3_, randomIn);
            }
        }
        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f * (float)this.getSlimeSize();
    }

    @Override
    public int getVerticalFaceSpeed() {
        return 0;
    }

    protected boolean makesSoundOnJump() {
        return this.getSlimeSize() > 0;
    }

    @Override
    protected void jump() {
        Vector3d vector3d = this.getMotion();
        this.setMotion(vector3d.x, this.getJumpUpwardsMotion(), vector3d.z);
        this.isAirBorne = true;
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        int i = this.rand.nextInt(3);
        if (i < 2 && this.rand.nextFloat() < 0.5f * difficultyIn.getClampedAdditionalDifficulty()) {
            ++i;
        }
        int j = 1 << i;
        this.setSlimeSize(j, true);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private float func_234304_m_() {
        float f = this.isSmallSlime() ? 1.4f : 0.8f;
        return ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * f;
    }

    protected SoundEvent getJumpSound() {
        return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return super.getSize(poseIn).scale(0.255f * (float)this.getSlimeSize());
    }

    static class MoveHelperController
    extends MovementController {
        private float yRot;
        private int jumpDelay;
        private final SlimeEntity slime;
        private boolean isAggressive;

        public MoveHelperController(SlimeEntity slimeIn) {
            super(slimeIn);
            this.slime = slimeIn;
            this.yRot = 180.0f * slimeIn.rotationYaw / (float)Math.PI;
        }

        public void setDirection(float yRotIn, boolean aggressive) {
            this.yRot = yRotIn;
            this.isAggressive = aggressive;
        }

        public void setSpeed(double speedIn) {
            this.speed = speedIn;
            this.action = MovementController.Action.MOVE_TO;
        }

        @Override
        public void tick() {
            this.mob.rotationYawHead = this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, this.yRot, 90.0f);
            this.mob.renderYawOffset = this.mob.rotationYaw;
            if (this.action != MovementController.Action.MOVE_TO) {
                this.mob.setMoveForward(0.0f);
            } else {
                this.action = MovementController.Action.WAIT;
                if (this.mob.isOnGround()) {
                    this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }
                        this.slime.getJumpController().setJumping();
                        if (this.slime.makesSoundOnJump()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.func_234304_m_());
                        }
                    } else {
                        this.slime.moveStrafing = 0.0f;
                        this.slime.moveForward = 0.0f;
                        this.mob.setAIMoveSpeed(0.0f);
                    }
                } else {
                    this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }
        }
    }

    static class FloatGoal
    extends Goal {
        private final SlimeEntity slime;

        public FloatGoal(SlimeEntity slimeIn) {
            this.slime = slimeIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            slimeIn.getNavigator().setCanSwim(true);
        }

        @Override
        public boolean shouldExecute() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveHelper() instanceof MoveHelperController;
        }

        @Override
        public void tick() {
            if (this.slime.getRNG().nextFloat() < 0.8f) {
                this.slime.getJumpController().setJumping();
            }
            ((MoveHelperController)this.slime.getMoveHelper()).setSpeed(1.2);
        }
    }

    static class AttackGoal
    extends Goal {
        private final SlimeEntity slime;
        private int growTieredTimer;

        public AttackGoal(SlimeEntity slimeIn) {
            this.slime = slimeIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean shouldExecute() {
            LivingEntity livingentity = this.slime.getAttackTarget();
            if (livingentity == null) {
                return false;
            }
            if (!livingentity.isAlive()) {
                return false;
            }
            return livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.disableDamage ? false : this.slime.getMoveHelper() instanceof MoveHelperController;
        }

        @Override
        public void startExecuting() {
            this.growTieredTimer = 300;
            super.startExecuting();
        }

        @Override
        public boolean shouldContinueExecuting() {
            LivingEntity livingentity = this.slime.getAttackTarget();
            if (livingentity == null) {
                return false;
            }
            if (!livingentity.isAlive()) {
                return false;
            }
            if (livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.disableDamage) {
                return false;
            }
            return --this.growTieredTimer > 0;
        }

        @Override
        public void tick() {
            this.slime.faceEntity(this.slime.getAttackTarget(), 10.0f, 10.0f);
            ((MoveHelperController)this.slime.getMoveHelper()).setDirection(this.slime.rotationYaw, this.slime.canDamagePlayer());
        }
    }

    static class FaceRandomGoal
    extends Goal {
        private final SlimeEntity slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public FaceRandomGoal(SlimeEntity slimeIn) {
            this.slime = slimeIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean shouldExecute() {
            return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.isPotionActive(Effects.LEVITATION)) && this.slime.getMoveHelper() instanceof MoveHelperController;
        }

        @Override
        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = 40 + this.slime.getRNG().nextInt(60);
                this.chosenDegrees = this.slime.getRNG().nextInt(360);
            }
            ((MoveHelperController)this.slime.getMoveHelper()).setDirection(this.chosenDegrees, false);
        }
    }

    static class HopGoal
    extends Goal {
        private final SlimeEntity slime;

        public HopGoal(SlimeEntity slimeIn) {
            this.slime = slimeIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            return !this.slime.isPassenger();
        }

        @Override
        public void tick() {
            ((MoveHelperController)this.slime.getMoveHelper()).setSpeed(1.0);
        }
    }
}
