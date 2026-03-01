package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class RainParticle
extends SpriteTexturedParticle {
    protected RainParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX *= (double)0.3f;
        this.motionY = Math.random() * (double)0.2f + (double)0.1f;
        this.motionZ *= (double)0.3f;
        this.setSize(0.01f, 0.01f);
        this.particleGravity = 0.06f;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.maxAge-- <= 0) {
            this.setExpired();
        } else {
            BlockPos blockpos;
            double d0;
            this.motionY -= (double)this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.98f;
            this.motionY *= (double)0.98f;
            this.motionZ *= (double)0.98f;
            if (this.onGround) {
                if (Math.random() < 0.5) {
                    this.setExpired();
                }
                this.motionX *= (double)0.7f;
                this.motionZ *= (double)0.7f;
            }
            if ((d0 = Math.max(this.world.getBlockState(blockpos = new BlockPos(this.posX, this.posY, this.posZ)).getCollisionShape(this.world, blockpos).max(Direction.Axis.Y, this.posX - (double)blockpos.getX(), this.posZ - (double)blockpos.getZ()), (double)this.world.getFluidState(blockpos).getActualHeight(this.world, blockpos))) > 0.0 && this.posY < (double)blockpos.getY() + d0) {
                this.setExpired();
            }
        }
    }

    public static class Factory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            RainParticle rainparticle = new RainParticle(worldIn, x, y, z);
            rainparticle.selectSpriteRandomly(this.spriteSet);
            return rainparticle;
        }
    }
}
