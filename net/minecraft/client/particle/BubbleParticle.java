package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;

public class BubbleParticle
extends SpriteTexturedParticle {
    private BubbleParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.setSize(0.02f, 0.02f);
        this.particleScale *= this.rand.nextFloat() * 0.6f + 0.2f;
        this.motionX = motionX * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.motionY = motionY * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.motionZ = motionZ * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.maxAge-- <= 0) {
            this.setExpired();
        } else {
            this.motionY += 0.002;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.85f;
            this.motionY *= (double)0.85f;
            this.motionZ *= (double)0.85f;
            if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).isTagged(FluidTags.WATER)) {
                this.setExpired();
            }
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BubbleParticle bubbleparticle = new BubbleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            bubbleparticle.selectSpriteRandomly(this.spriteSet);
            return bubbleparticle;
        }
    }
}
