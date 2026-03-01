package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class DragonFireballEntity
extends DamagingProjectileEntity {
    public DragonFireballEntity(EntityType<? extends DragonFireballEntity> p_i50171_1_, World p_i50171_2_) {
        super((EntityType<? extends DamagingProjectileEntity>)p_i50171_1_, p_i50171_2_);
    }

    public DragonFireballEntity(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(EntityType.DRAGON_FIREBALL, x, y, z, accelX, accelY, accelZ, worldIn);
    }

    public DragonFireballEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(EntityType.DRAGON_FIREBALL, shooter, accelX, accelY, accelZ, worldIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        Entity entity = this.func_234616_v_();
        if (!(result.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult)result).getEntity().isEntityEqual(entity) || this.world.isRemote)) {
            List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(4.0, 2.0, 4.0));
            AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ());
            if (entity instanceof LivingEntity) {
                areaeffectcloudentity.setOwner((LivingEntity)entity);
            }
            areaeffectcloudentity.setParticleData(ParticleTypes.DRAGON_BREATH);
            areaeffectcloudentity.setRadius(3.0f);
            areaeffectcloudentity.setDuration(600);
            areaeffectcloudentity.setRadiusPerTick((7.0f - areaeffectcloudentity.getRadius()) / (float)areaeffectcloudentity.getDuration());
            areaeffectcloudentity.addEffect(new EffectInstance(Effects.INSTANT_DAMAGE, 1, 1));
            if (!list.isEmpty()) {
                for (LivingEntity livingentity : list) {
                    double d0 = this.getDistanceSq(livingentity);
                    if (!(d0 < 16.0)) continue;
                    areaeffectcloudentity.setPosition(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ());
                    break;
                }
            }
            this.world.playEvent(2006, this.getPosition(), this.isSilent() ? -1 : 1);
            this.world.addEntity(areaeffectcloudentity);
            this.remove();
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected IParticleData getParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    @Override
    protected boolean isFireballFiery() {
        return false;
    }
}
