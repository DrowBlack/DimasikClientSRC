package net.minecraft.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class ThrowableEntity
extends ProjectileEntity {
    protected ThrowableEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super((EntityType<? extends ProjectileEntity>)type, worldIn);
    }

    protected ThrowableEntity(EntityType<? extends ThrowableEntity> type, double x, double y, double z, World worldIn) {
        this(type, worldIn);
        this.setPosition(x, y, z);
    }

    protected ThrowableEntity(EntityType<? extends ThrowableEntity> type, LivingEntity livingEntityIn, World worldIn) {
        this(type, livingEntityIn.getPosX(), livingEntityIn.getPosYEye() - (double)0.1f, livingEntityIn.getPosZ(), worldIn);
        this.setShooter(livingEntityIn);
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
        float f;
        super.tick();
        RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
        boolean flag = false;
        if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
            BlockState blockstate = this.world.getBlockState(blockpos);
            if (blockstate.isIn(Blocks.NETHER_PORTAL)) {
                this.setPortal(blockpos);
                flag = true;
            } else if (blockstate.isIn(Blocks.END_GATEWAY)) {
                TileEntity tileentity = this.world.getTileEntity(blockpos);
                if (tileentity instanceof EndGatewayTileEntity && EndGatewayTileEntity.func_242690_a(this)) {
                    ((EndGatewayTileEntity)tileentity).teleportEntity(this);
                }
                flag = true;
            }
        }
        if (raytraceresult.getType() != RayTraceResult.Type.MISS && !flag) {
            this.onImpact(raytraceresult);
        }
        this.doBlockCollisions();
        Vector3d vector3d = this.getMotion();
        double d2 = this.getPosX() + vector3d.x;
        double d0 = this.getPosY() + vector3d.y;
        double d1 = this.getPosZ() + vector3d.z;
        this.func_234617_x_();
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f1 = 0.25f;
                this.world.addParticle(ParticleTypes.BUBBLE, d2 - vector3d.x * 0.25, d0 - vector3d.y * 0.25, d1 - vector3d.z * 0.25, vector3d.x, vector3d.y, vector3d.z);
            }
            f = 0.8f;
        } else {
            f = 0.99f;
        }
        this.setMotion(vector3d.scale(f));
        if (!this.hasNoGravity()) {
            Vector3d vector3d1 = this.getMotion();
            this.setMotion(vector3d1.x, vector3d1.y - (double)this.getGravityVelocity(), vector3d1.z);
        }
        this.setPosition(d2, d0, d1);
    }

    public float getGravityVelocity() {
        return 0.03f;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}
