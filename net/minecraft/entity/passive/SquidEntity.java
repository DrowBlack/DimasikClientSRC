package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SquidEntity
extends WaterMobEntity {
    public float squidPitch;
    public float prevSquidPitch;
    public float squidYaw;
    public float prevSquidYaw;
    public float squidRotation;
    public float prevSquidRotation;
    public float tentacleAngle;
    public float lastTentacleAngle;
    private float randomMotionSpeed;
    private float rotationVelocity;
    private float rotateSpeed;
    private float randomMotionVecX;
    private float randomMotionVecY;
    private float randomMotionVecZ;

    public SquidEntity(EntityType<? extends SquidEntity> type, World worldIn) {
        super((EntityType<? extends WaterMobEntity>)type, worldIn);
        this.rand.setSeed(this.getEntityId());
        this.rotationVelocity = 1.0f / (this.rand.nextFloat() + 1.0f) * 0.2f;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MoveRandomGoal(this));
        this.goalSelector.addGoal(1, new FleeGoal());
    }

    public static AttributeModifierMap.MutableAttribute func_234227_m_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.5f;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SQUID_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        this.prevSquidPitch = this.squidPitch;
        this.prevSquidYaw = this.squidYaw;
        this.prevSquidRotation = this.squidRotation;
        this.lastTentacleAngle = this.tentacleAngle;
        this.squidRotation += this.rotationVelocity;
        if ((double)this.squidRotation > Math.PI * 2) {
            if (this.world.isRemote) {
                this.squidRotation = (float)Math.PI * 2;
            } else {
                this.squidRotation = (float)((double)this.squidRotation - Math.PI * 2);
                if (this.rand.nextInt(10) == 0) {
                    this.rotationVelocity = 1.0f / (this.rand.nextFloat() + 1.0f) * 0.2f;
                }
                this.world.setEntityState(this, (byte)19);
            }
        }
        if (this.isInWaterOrBubbleColumn()) {
            if (this.squidRotation < (float)Math.PI) {
                float f = this.squidRotation / (float)Math.PI;
                this.tentacleAngle = MathHelper.sin(f * f * (float)Math.PI) * (float)Math.PI * 0.25f;
                if ((double)f > 0.75) {
                    this.randomMotionSpeed = 1.0f;
                    this.rotateSpeed = 1.0f;
                } else {
                    this.rotateSpeed *= 0.8f;
                }
            } else {
                this.tentacleAngle = 0.0f;
                this.randomMotionSpeed *= 0.9f;
                this.rotateSpeed *= 0.99f;
            }
            if (!this.world.isRemote) {
                this.setMotion(this.randomMotionVecX * this.randomMotionSpeed, this.randomMotionVecY * this.randomMotionSpeed, this.randomMotionVecZ * this.randomMotionSpeed);
            }
            Vector3d vector3d = this.getMotion();
            float f1 = MathHelper.sqrt(SquidEntity.horizontalMag(vector3d));
            this.renderYawOffset += (-((float)MathHelper.atan2(vector3d.x, vector3d.z)) * 57.295776f - this.renderYawOffset) * 0.1f;
            this.rotationYaw = this.renderYawOffset;
            this.squidYaw = (float)((double)this.squidYaw + Math.PI * (double)this.rotateSpeed * 1.5);
            this.squidPitch += (-((float)MathHelper.atan2(f1, vector3d.y)) * 57.295776f - this.squidPitch) * 0.1f;
        } else {
            this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * (float)Math.PI * 0.25f;
            if (!this.world.isRemote) {
                double d0 = this.getMotion().y;
                if (this.isPotionActive(Effects.LEVITATION)) {
                    d0 = 0.05 * (double)(this.getActivePotionEffect(Effects.LEVITATION).getAmplifier() + 1);
                } else if (!this.hasNoGravity()) {
                    d0 -= 0.08;
                }
                this.setMotion(0.0, d0 * (double)0.98f, 0.0);
            }
            this.squidPitch = (float)((double)this.squidPitch + (double)(-90.0f - this.squidPitch) * 0.02);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (super.attackEntityFrom(source, amount) && this.getRevengeTarget() != null) {
            this.squirtInk();
            return true;
        }
        return false;
    }

    private Vector3d func_207400_b(Vector3d p_207400_1_) {
        Vector3d vector3d = p_207400_1_.rotatePitch(this.prevSquidPitch * ((float)Math.PI / 180));
        return vector3d.rotateYaw(-this.prevRenderYawOffset * ((float)Math.PI / 180));
    }

    private void squirtInk() {
        this.playSound(SoundEvents.ENTITY_SQUID_SQUIRT, this.getSoundVolume(), this.getSoundPitch());
        Vector3d vector3d = this.func_207400_b(new Vector3d(0.0, -1.0, 0.0)).add(this.getPosX(), this.getPosY(), this.getPosZ());
        for (int i = 0; i < 30; ++i) {
            Vector3d vector3d1 = this.func_207400_b(new Vector3d((double)this.rand.nextFloat() * 0.6 - 0.3, -1.0, (double)this.rand.nextFloat() * 0.6 - 0.3));
            Vector3d vector3d2 = vector3d1.scale(0.3 + (double)(this.rand.nextFloat() * 2.0f));
            ((ServerWorld)this.world).spawnParticle(ParticleTypes.SQUID_INK, vector3d.x, vector3d.y + 0.5, vector3d.z, 0, vector3d2.x, vector3d2.y, vector3d2.z, 0.1f);
        }
    }

    @Override
    public void travel(Vector3d travelVector) {
        this.move(MoverType.SELF, this.getMotion());
    }

    public static boolean func_223365_b(EntityType<SquidEntity> p_223365_0_, IWorld p_223365_1_, SpawnReason reason, BlockPos p_223365_3_, Random p_223365_4_) {
        return p_223365_3_.getY() > 45 && p_223365_3_.getY() < p_223365_1_.getSeaLevel();
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 19) {
            this.squidRotation = 0.0f;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void setMovementVector(float randomMotionVecXIn, float randomMotionVecYIn, float randomMotionVecZIn) {
        this.randomMotionVecX = randomMotionVecXIn;
        this.randomMotionVecY = randomMotionVecYIn;
        this.randomMotionVecZ = randomMotionVecZIn;
    }

    public boolean hasMovementVector() {
        return this.randomMotionVecX != 0.0f || this.randomMotionVecY != 0.0f || this.randomMotionVecZ != 0.0f;
    }

    class MoveRandomGoal
    extends Goal {
        private final SquidEntity squid;

        public MoveRandomGoal(SquidEntity p_i48823_2_) {
            this.squid = p_i48823_2_;
        }

        @Override
        public boolean shouldExecute() {
            return true;
        }

        @Override
        public void tick() {
            int i = this.squid.getIdleTime();
            if (i > 100) {
                this.squid.setMovementVector(0.0f, 0.0f, 0.0f);
            } else if (this.squid.getRNG().nextInt(50) == 0 || !this.squid.inWater || !this.squid.hasMovementVector()) {
                float f = this.squid.getRNG().nextFloat() * ((float)Math.PI * 2);
                float f1 = MathHelper.cos(f) * 0.2f;
                float f2 = -0.1f + this.squid.getRNG().nextFloat() * 0.2f;
                float f3 = MathHelper.sin(f) * 0.2f;
                this.squid.setMovementVector(f1, f2, f3);
            }
        }
    }

    class FleeGoal
    extends Goal {
        private int tickCounter;

        private FleeGoal() {
        }

        @Override
        public boolean shouldExecute() {
            LivingEntity livingentity = SquidEntity.this.getRevengeTarget();
            if (SquidEntity.this.isInWater() && livingentity != null) {
                return SquidEntity.this.getDistanceSq(livingentity) < 100.0;
            }
            return false;
        }

        @Override
        public void startExecuting() {
            this.tickCounter = 0;
        }

        @Override
        public void tick() {
            ++this.tickCounter;
            LivingEntity livingentity = SquidEntity.this.getRevengeTarget();
            if (livingentity != null) {
                Vector3d vector3d = new Vector3d(SquidEntity.this.getPosX() - livingentity.getPosX(), SquidEntity.this.getPosY() - livingentity.getPosY(), SquidEntity.this.getPosZ() - livingentity.getPosZ());
                BlockState blockstate = SquidEntity.this.world.getBlockState(new BlockPos(SquidEntity.this.getPosX() + vector3d.x, SquidEntity.this.getPosY() + vector3d.y, SquidEntity.this.getPosZ() + vector3d.z));
                FluidState fluidstate = SquidEntity.this.world.getFluidState(new BlockPos(SquidEntity.this.getPosX() + vector3d.x, SquidEntity.this.getPosY() + vector3d.y, SquidEntity.this.getPosZ() + vector3d.z));
                if (fluidstate.isTagged(FluidTags.WATER) || blockstate.isAir()) {
                    double d0 = vector3d.length();
                    if (d0 > 0.0) {
                        vector3d.normalize();
                        float f = 3.0f;
                        if (d0 > 5.0) {
                            f = (float)((double)f - (d0 - 5.0) / 5.0);
                        }
                        if (f > 0.0f) {
                            vector3d = vector3d.scale(f);
                        }
                    }
                    if (blockstate.isAir()) {
                        vector3d = vector3d.subtract(0.0, vector3d.y, 0.0);
                    }
                    SquidEntity.this.setMovementVector((float)vector3d.x / 20.0f, (float)vector3d.y / 20.0f, (float)vector3d.z / 20.0f);
                }
                if (this.tickCounter % 10 == 5) {
                    SquidEntity.this.world.addParticle(ParticleTypes.BUBBLE, SquidEntity.this.getPosX(), SquidEntity.this.getPosY(), SquidEntity.this.getPosZ(), 0.0, 0.0, 0.0);
                }
            }
        }
    }
}
