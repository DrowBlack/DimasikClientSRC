package net.minecraft.entity.projectile;

import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class LlamaSpitEntity
extends ProjectileEntity {
    public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> p_i50162_1_, World p_i50162_2_) {
        super((EntityType<? extends ProjectileEntity>)p_i50162_1_, p_i50162_2_);
    }

    public LlamaSpitEntity(World worldIn, LlamaEntity p_i47273_2_) {
        this((EntityType<? extends LlamaSpitEntity>)EntityType.LLAMA_SPIT, worldIn);
        super.setShooter(p_i47273_2_);
        this.setPosition(p_i47273_2_.getPosX() - (double)(p_i47273_2_.getWidth() + 1.0f) * 0.5 * (double)MathHelper.sin(p_i47273_2_.renderYawOffset * ((float)Math.PI / 180)), p_i47273_2_.getPosYEye() - (double)0.1f, p_i47273_2_.getPosZ() + (double)(p_i47273_2_.getWidth() + 1.0f) * 0.5 * (double)MathHelper.cos(p_i47273_2_.renderYawOffset * ((float)Math.PI / 180)));
    }

    public LlamaSpitEntity(World worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
        this((EntityType<? extends LlamaSpitEntity>)EntityType.LLAMA_SPIT, worldIn);
        this.setPosition(x, y, z);
        for (int i = 0; i < 7; ++i) {
            double d0 = 0.4 + 0.1 * (double)i;
            worldIn.addParticle(ParticleTypes.SPIT, x, y, z, p_i47274_8_ * d0, p_i47274_10_, p_i47274_12_ * d0);
        }
        this.setMotion(p_i47274_8_, p_i47274_10_, p_i47274_12_);
    }

    @Override
    public void tick() {
        super.tick();
        Vector3d vector3d = this.getMotion();
        RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
        if (raytraceresult != null) {
            this.onImpact(raytraceresult);
        }
        double d0 = this.getPosX() + vector3d.x;
        double d1 = this.getPosY() + vector3d.y;
        double d2 = this.getPosZ() + vector3d.z;
        this.func_234617_x_();
        float f = 0.99f;
        float f1 = 0.06f;
        if (this.world.func_234853_a_(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
            this.remove();
        } else if (this.isInWaterOrBubbleColumn()) {
            this.remove();
        } else {
            this.setMotion(vector3d.scale(0.99f));
            if (!this.hasNoGravity()) {
                this.setMotion(this.getMotion().add(0.0, -0.06f, 0.0));
            }
            this.setPosition(d0, d1, d2);
        }
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        Entity entity = this.func_234616_v_();
        if (entity instanceof LivingEntity) {
            p_213868_1_.getEntity().attackEntityFrom(DamageSource.causeIndirectDamage(this, (LivingEntity)entity).setProjectile(), 1.0f);
        }
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        super.func_230299_a_(p_230299_1_);
        if (!this.world.isRemote) {
            this.remove();
        }
    }

    @Override
    protected void registerData() {
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}
