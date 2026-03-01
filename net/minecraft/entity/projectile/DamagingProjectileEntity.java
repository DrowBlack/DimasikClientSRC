package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class DamagingProjectileEntity
extends ProjectileEntity {
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;

    protected DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> p_i50173_1_, World p_i50173_2_) {
        super((EntityType<? extends ProjectileEntity>)p_i50173_1_, p_i50173_2_);
    }

    public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> p_i50174_1_, double p_i50174_2_, double p_i50174_4_, double p_i50174_6_, double p_i50174_8_, double p_i50174_10_, double p_i50174_12_, World p_i50174_14_) {
        this(p_i50174_1_, p_i50174_14_);
        this.setLocationAndAngles(p_i50174_2_, p_i50174_4_, p_i50174_6_, this.rotationYaw, this.rotationPitch);
        this.recenterBoundingBox();
        double d0 = MathHelper.sqrt(p_i50174_8_ * p_i50174_8_ + p_i50174_10_ * p_i50174_10_ + p_i50174_12_ * p_i50174_12_);
        if (d0 != 0.0) {
            this.accelerationX = p_i50174_8_ / d0 * 0.1;
            this.accelerationY = p_i50174_10_ / d0 * 0.1;
            this.accelerationZ = p_i50174_12_ / d0 * 0.1;
        }
    }

    public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> p_i50175_1_, LivingEntity p_i50175_2_, double p_i50175_3_, double p_i50175_5_, double p_i50175_7_, World p_i50175_9_) {
        this(p_i50175_1_, p_i50175_2_.getPosX(), p_i50175_2_.getPosY(), p_i50175_2_.getPosZ(), p_i50175_3_, p_i50175_5_, p_i50175_7_, p_i50175_9_);
        this.setShooter(p_i50175_2_);
        this.setRotation(p_i50175_2_.rotationYaw, p_i50175_2_.rotationPitch);
    }

    @Override
    protected void registerData() {
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0;
        if (Double.isNaN(d0)) {
            d0 = 4.0;
        }
        return distance < (d0 *= 64.0) * d0;
    }

    @Override
    public void tick() {
        Entity entity = this.func_234616_v_();
        if (this.world.isRemote || (entity == null || !entity.removed) && this.world.isBlockLoaded(this.getPosition())) {
            RayTraceResult raytraceresult;
            super.tick();
            if (this.isFireballFiery()) {
                this.setFire(1);
            }
            if ((raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_)).getType() != RayTraceResult.Type.MISS) {
                this.onImpact(raytraceresult);
            }
            this.doBlockCollisions();
            Vector3d vector3d = this.getMotion();
            double d0 = this.getPosX() + vector3d.x;
            double d1 = this.getPosY() + vector3d.y;
            double d2 = this.getPosZ() + vector3d.z;
            ProjectileHelper.rotateTowardsMovement(this, 0.2f);
            float f = this.getMotionFactor();
            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    float f1 = 0.25f;
                    this.world.addParticle(ParticleTypes.BUBBLE, d0 - vector3d.x * 0.25, d1 - vector3d.y * 0.25, d2 - vector3d.z * 0.25, vector3d.x, vector3d.y, vector3d.z);
                }
                f = 0.8f;
            }
            this.setMotion(vector3d.add(this.accelerationX, this.accelerationY, this.accelerationZ).scale(f));
            this.world.addParticle(this.getParticle(), d0, d1 + 0.5, d2, 0.0, 0.0, 0.0);
            this.setPosition(d0, d1, d2);
        } else {
            this.remove();
        }
    }

    @Override
    protected boolean func_230298_a_(Entity p_230298_1_) {
        return super.func_230298_a_(p_230298_1_) && !p_230298_1_.noClip;
    }

    protected boolean isFireballFiery() {
        return true;
    }

    protected IParticleData getParticle() {
        return ParticleTypes.SMOKE;
    }

    protected float getMotionFactor() {
        return 0.95f;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("power", this.newDoubleNBTList(this.accelerationX, this.accelerationY, this.accelerationZ));
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        ListNBT listnbt;
        super.readAdditional(compound);
        if (compound.contains("power", 9) && (listnbt = compound.getList("power", 6)).size() == 3) {
            this.accelerationX = listnbt.getDouble(0);
            this.accelerationY = listnbt.getDouble(1);
            this.accelerationZ = listnbt.getDouble(2);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public float getCollisionBorderSize() {
        return 1.0f;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.markVelocityChanged();
        Entity entity = source.getTrueSource();
        if (entity != null) {
            Vector3d vector3d = entity.getLookVec();
            this.setMotion(vector3d);
            this.accelerationX = vector3d.x * 0.1;
            this.accelerationY = vector3d.y * 0.1;
            this.accelerationZ = vector3d.z * 0.1;
            this.setShooter(entity);
            return true;
        }
        return false;
    }

    @Override
    public float getBrightness() {
        return 1.0f;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        Entity entity = this.func_234616_v_();
        int i = entity == null ? 0 : entity.getEntityId();
        return new SSpawnObjectPacket(this.getEntityId(), this.getUniqueID(), this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationPitch, this.rotationYaw, this.getType(), i, new Vector3d(this.accelerationX, this.accelerationY, this.accelerationZ));
    }
}
