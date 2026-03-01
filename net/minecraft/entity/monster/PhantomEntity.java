package net.minecraft.entity.monster;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

public class PhantomEntity
extends FlyingEntity
implements IMob {
    private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(PhantomEntity.class, DataSerializers.VARINT);
    private Vector3d orbitOffset = Vector3d.ZERO;
    private BlockPos orbitPosition = BlockPos.ZERO;
    private AttackPhase attackPhase = AttackPhase.CIRCLE;

    public PhantomEntity(EntityType<? extends PhantomEntity> type, World worldIn) {
        super((EntityType<? extends FlyingEntity>)type, worldIn);
        this.experienceValue = 5;
        this.moveController = new MoveHelperController(this);
        this.lookController = new LookHelperController(this);
    }

    @Override
    protected BodyController createBodyController() {
        return new BodyHelperController(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PickAttackGoal());
        this.goalSelector.addGoal(2, new SweepAttackGoal());
        this.goalSelector.addGoal(3, new OrbitPointGoal());
        this.targetSelector.addGoal(1, new AttackPlayerGoal());
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(SIZE, 0);
    }

    public void setPhantomSize(int sizeIn) {
        this.dataManager.set(SIZE, MathHelper.clamp(sizeIn, 0, 64));
    }

    private void updatePhantomSize() {
        this.recalculateSize();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
    }

    public int getPhantomSize() {
        return this.dataManager.get(SIZE);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.35f;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SIZE.equals(key)) {
            this.updatePhantomSize();
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    protected boolean isDespawnPeaceful() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isRemote) {
            float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted) * 0.13f + (float)Math.PI);
            float f1 = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted + 1) * 0.13f + (float)Math.PI);
            if (f > 0.0f && f1 <= 0.0f) {
                this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95f + this.rand.nextFloat() * 0.05f, 0.95f + this.rand.nextFloat() * 0.05f, false);
            }
            int i = this.getPhantomSize();
            float f2 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)i);
            float f3 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)i);
            float f4 = (0.3f + f * 0.45f) * ((float)i * 0.2f + 1.0f);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getPosX() + (double)f2, this.getPosY() + (double)f4, this.getPosZ() + (double)f3, 0.0, 0.0, 0.0);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getPosX() - (double)f2, this.getPosY() + (double)f4, this.getPosZ() - (double)f3, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void livingTick() {
        if (this.isAlive() && this.isInDaylight()) {
            this.setFire(8);
        }
        super.livingTick();
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.orbitPosition = this.getPosition().up(5);
        this.setPhantomSize(0);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("AX")) {
            this.orbitPosition = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
        }
        this.setPhantomSize(compound.getInt("Size"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("AX", this.orbitPosition.getX());
        compound.putInt("AY", this.orbitPosition.getY());
        compound.putInt("AZ", this.orbitPosition.getZ());
        compound.putInt("Size", this.getPhantomSize());
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PHANTOM_DEATH;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0f;
    }

    @Override
    public boolean canAttack(EntityType<?> typeIn) {
        return true;
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        int i = this.getPhantomSize();
        EntitySize entitysize = super.getSize(poseIn);
        float f = (entitysize.width + 0.2f * (float)i) / entitysize.width;
        return entitysize.scale(f);
    }

    static enum AttackPhase {
        CIRCLE,
        SWOOP;

    }

    class MoveHelperController
    extends MovementController {
        private float speedFactor;

        public MoveHelperController(MobEntity entityIn) {
            super(entityIn);
            this.speedFactor = 0.1f;
        }

        @Override
        public void tick() {
            float f7;
            if (PhantomEntity.this.collidedHorizontally) {
                PhantomEntity.this.rotationYaw += 180.0f;
                this.speedFactor = 0.1f;
            }
            float f = (float)(PhantomEntity.this.orbitOffset.x - PhantomEntity.this.getPosX());
            float f1 = (float)(PhantomEntity.this.orbitOffset.y - PhantomEntity.this.getPosY());
            float f2 = (float)(PhantomEntity.this.orbitOffset.z - PhantomEntity.this.getPosZ());
            double d0 = MathHelper.sqrt(f * f + f2 * f2);
            double d1 = 1.0 - (double)MathHelper.abs(f1 * 0.7f) / d0;
            f = (float)((double)f * d1);
            f2 = (float)((double)f2 * d1);
            d0 = MathHelper.sqrt(f * f + f2 * f2);
            double d2 = MathHelper.sqrt(f * f + f2 * f2 + f1 * f1);
            float f3 = PhantomEntity.this.rotationYaw;
            float f4 = (float)MathHelper.atan2(f2, f);
            float f5 = MathHelper.wrapDegrees(PhantomEntity.this.rotationYaw + 90.0f);
            float f6 = MathHelper.wrapDegrees(f4 * 57.295776f);
            PhantomEntity.this.renderYawOffset = PhantomEntity.this.rotationYaw = MathHelper.approachDegrees(f5, f6, 4.0f) - 90.0f;
            this.speedFactor = MathHelper.degreesDifferenceAbs(f3, PhantomEntity.this.rotationYaw) < 3.0f ? MathHelper.approach(this.speedFactor, 1.8f, 0.005f * (1.8f / this.speedFactor)) : MathHelper.approach(this.speedFactor, 0.2f, 0.025f);
            PhantomEntity.this.rotationPitch = f7 = (float)(-(MathHelper.atan2(-f1, d0) * 57.2957763671875));
            float f8 = PhantomEntity.this.rotationYaw + 90.0f;
            double d3 = (double)(this.speedFactor * MathHelper.cos(f8 * ((float)Math.PI / 180))) * Math.abs((double)f / d2);
            double d4 = (double)(this.speedFactor * MathHelper.sin(f8 * ((float)Math.PI / 180))) * Math.abs((double)f2 / d2);
            double d5 = (double)(this.speedFactor * MathHelper.sin(f7 * ((float)Math.PI / 180))) * Math.abs((double)f1 / d2);
            Vector3d vector3d = PhantomEntity.this.getMotion();
            PhantomEntity.this.setMotion(vector3d.add(new Vector3d(d3, d5, d4).subtract(vector3d).scale(0.2)));
        }
    }

    class LookHelperController
    extends LookController {
        public LookHelperController(MobEntity entityIn) {
            super(entityIn);
        }

        @Override
        public void tick() {
        }
    }

    class BodyHelperController
    extends BodyController {
        public BodyHelperController(MobEntity mob) {
            super(mob);
        }

        @Override
        public void updateRenderAngles() {
            PhantomEntity.this.rotationYawHead = PhantomEntity.this.renderYawOffset;
            PhantomEntity.this.renderYawOffset = PhantomEntity.this.rotationYaw;
        }
    }

    class PickAttackGoal
    extends Goal {
        private int tickDelay;

        private PickAttackGoal() {
        }

        @Override
        public boolean shouldExecute() {
            LivingEntity livingentity = PhantomEntity.this.getAttackTarget();
            return livingentity != null ? PhantomEntity.this.canAttack(PhantomEntity.this.getAttackTarget(), EntityPredicate.DEFAULT) : false;
        }

        @Override
        public void startExecuting() {
            this.tickDelay = 10;
            PhantomEntity.this.attackPhase = AttackPhase.CIRCLE;
            this.func_203143_f();
        }

        @Override
        public void resetTask() {
            PhantomEntity.this.orbitPosition = PhantomEntity.this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, PhantomEntity.this.orbitPosition).up(10 + PhantomEntity.this.rand.nextInt(20));
        }

        @Override
        public void tick() {
            if (PhantomEntity.this.attackPhase == AttackPhase.CIRCLE) {
                --this.tickDelay;
                if (this.tickDelay <= 0) {
                    PhantomEntity.this.attackPhase = AttackPhase.SWOOP;
                    this.func_203143_f();
                    this.tickDelay = (8 + PhantomEntity.this.rand.nextInt(4)) * 20;
                    PhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0f, 0.95f + PhantomEntity.this.rand.nextFloat() * 0.1f);
                }
            }
        }

        private void func_203143_f() {
            PhantomEntity.this.orbitPosition = PhantomEntity.this.getAttackTarget().getPosition().up(20 + PhantomEntity.this.rand.nextInt(20));
            if (PhantomEntity.this.orbitPosition.getY() < PhantomEntity.this.world.getSeaLevel()) {
                PhantomEntity.this.orbitPosition = new BlockPos(PhantomEntity.this.orbitPosition.getX(), PhantomEntity.this.world.getSeaLevel() + 1, PhantomEntity.this.orbitPosition.getZ());
            }
        }
    }

    class SweepAttackGoal
    extends MoveGoal {
        private SweepAttackGoal() {
        }

        @Override
        public boolean shouldExecute() {
            return PhantomEntity.this.getAttackTarget() != null && PhantomEntity.this.attackPhase == AttackPhase.SWOOP;
        }

        @Override
        public boolean shouldContinueExecuting() {
            LivingEntity livingentity = PhantomEntity.this.getAttackTarget();
            if (livingentity == null) {
                return false;
            }
            if (!livingentity.isAlive()) {
                return false;
            }
            if (!(livingentity instanceof PlayerEntity) || !((PlayerEntity)livingentity).isSpectator() && !((PlayerEntity)livingentity).isCreative()) {
                List<Entity> list;
                if (!this.shouldExecute()) {
                    return false;
                }
                if (PhantomEntity.this.ticksExisted % 20 == 0 && !(list = PhantomEntity.this.world.getEntitiesWithinAABB(CatEntity.class, PhantomEntity.this.getBoundingBox().grow(16.0), EntityPredicates.IS_ALIVE)).isEmpty()) {
                    for (CatEntity catEntity : list) {
                        catEntity.func_213420_ej();
                    }
                    return false;
                }
                return true;
            }
            return false;
        }

        @Override
        public void startExecuting() {
        }

        @Override
        public void resetTask() {
            PhantomEntity.this.setAttackTarget(null);
            PhantomEntity.this.attackPhase = AttackPhase.CIRCLE;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = PhantomEntity.this.getAttackTarget();
            PhantomEntity.this.orbitOffset = new Vector3d(livingentity.getPosX(), livingentity.getPosYHeight(0.5), livingentity.getPosZ());
            if (PhantomEntity.this.getBoundingBox().grow(0.2f).intersects(livingentity.getBoundingBox())) {
                PhantomEntity.this.attackEntityAsMob(livingentity);
                PhantomEntity.this.attackPhase = AttackPhase.CIRCLE;
                if (!PhantomEntity.this.isSilent()) {
                    PhantomEntity.this.world.playEvent(1039, PhantomEntity.this.getPosition(), 0);
                }
            } else if (PhantomEntity.this.collidedHorizontally || PhantomEntity.this.hurtTime > 0) {
                PhantomEntity.this.attackPhase = AttackPhase.CIRCLE;
            }
        }
    }

    class OrbitPointGoal
    extends MoveGoal {
        private float field_203150_c;
        private float field_203151_d;
        private float field_203152_e;
        private float field_203153_f;

        private OrbitPointGoal() {
        }

        @Override
        public boolean shouldExecute() {
            return PhantomEntity.this.getAttackTarget() == null || PhantomEntity.this.attackPhase == AttackPhase.CIRCLE;
        }

        @Override
        public void startExecuting() {
            this.field_203151_d = 5.0f + PhantomEntity.this.rand.nextFloat() * 10.0f;
            this.field_203152_e = -4.0f + PhantomEntity.this.rand.nextFloat() * 9.0f;
            this.field_203153_f = PhantomEntity.this.rand.nextBoolean() ? 1.0f : -1.0f;
            this.func_203148_i();
        }

        @Override
        public void tick() {
            if (PhantomEntity.this.rand.nextInt(350) == 0) {
                this.field_203152_e = -4.0f + PhantomEntity.this.rand.nextFloat() * 9.0f;
            }
            if (PhantomEntity.this.rand.nextInt(250) == 0) {
                this.field_203151_d += 1.0f;
                if (this.field_203151_d > 15.0f) {
                    this.field_203151_d = 5.0f;
                    this.field_203153_f = -this.field_203153_f;
                }
            }
            if (PhantomEntity.this.rand.nextInt(450) == 0) {
                this.field_203150_c = PhantomEntity.this.rand.nextFloat() * 2.0f * (float)Math.PI;
                this.func_203148_i();
            }
            if (this.func_203146_f()) {
                this.func_203148_i();
            }
            if (PhantomEntity.this.orbitOffset.y < PhantomEntity.this.getPosY() && !PhantomEntity.this.world.isAirBlock(PhantomEntity.this.getPosition().down(1))) {
                this.field_203152_e = Math.max(1.0f, this.field_203152_e);
                this.func_203148_i();
            }
            if (PhantomEntity.this.orbitOffset.y > PhantomEntity.this.getPosY() && !PhantomEntity.this.world.isAirBlock(PhantomEntity.this.getPosition().up(1))) {
                this.field_203152_e = Math.min(-1.0f, this.field_203152_e);
                this.func_203148_i();
            }
        }

        private void func_203148_i() {
            if (BlockPos.ZERO.equals(PhantomEntity.this.orbitPosition)) {
                PhantomEntity.this.orbitPosition = PhantomEntity.this.getPosition();
            }
            this.field_203150_c += this.field_203153_f * 15.0f * ((float)Math.PI / 180);
            PhantomEntity.this.orbitOffset = Vector3d.copy(PhantomEntity.this.orbitPosition).add(this.field_203151_d * MathHelper.cos(this.field_203150_c), -4.0f + this.field_203152_e, this.field_203151_d * MathHelper.sin(this.field_203150_c));
        }
    }

    class AttackPlayerGoal
    extends Goal {
        private final EntityPredicate field_220842_b = new EntityPredicate().setDistance(64.0);
        private int tickDelay = 20;

        private AttackPlayerGoal() {
        }

        @Override
        public boolean shouldExecute() {
            if (this.tickDelay > 0) {
                --this.tickDelay;
                return false;
            }
            this.tickDelay = 60;
            List<PlayerEntity> list = PhantomEntity.this.world.getTargettablePlayersWithinAABB(this.field_220842_b, PhantomEntity.this, PhantomEntity.this.getBoundingBox().grow(16.0, 64.0, 16.0));
            if (!list.isEmpty()) {
                list.sort(Comparator.comparing(Entity::getPosY).reversed());
                for (PlayerEntity playerentity : list) {
                    if (!PhantomEntity.this.canAttack(playerentity, EntityPredicate.DEFAULT)) continue;
                    PhantomEntity.this.setAttackTarget(playerentity);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldContinueExecuting() {
            LivingEntity livingentity = PhantomEntity.this.getAttackTarget();
            return livingentity != null ? PhantomEntity.this.canAttack(livingentity, EntityPredicate.DEFAULT) : false;
        }
    }

    abstract class MoveGoal
    extends Goal {
        public MoveGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean func_203146_f() {
            return PhantomEntity.this.orbitOffset.squareDistanceTo(PhantomEntity.this.getPosX(), PhantomEntity.this.getPosY(), PhantomEntity.this.getPosZ()) < 4.0;
        }
    }
}
