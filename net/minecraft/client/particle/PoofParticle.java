package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class PoofParticle
extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteWithAge;

    protected PoofParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z);
        float f;
        this.spriteWithAge = spriteWithAge;
        this.motionX = motionX + (Math.random() * 2.0 - 1.0) * (double)0.05f;
        this.motionY = motionY + (Math.random() * 2.0 - 1.0) * (double)0.05f;
        this.motionZ = motionZ + (Math.random() * 2.0 - 1.0) * (double)0.05f;
        this.particleRed = f = this.rand.nextFloat() * 0.3f + 0.7f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.particleScale = 0.1f * (this.rand.nextFloat() * this.rand.nextFloat() * 6.0f + 1.0f);
        this.maxAge = (int)(16.0 / ((double)this.rand.nextFloat() * 0.8 + 0.2)) + 2;
        this.selectSpriteWithAge(spriteWithAge);
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
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.selectSpriteWithAge(this.spriteWithAge);
            this.motionY += 0.004;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.9f;
            this.motionY *= (double)0.9f;
            this.motionZ *= (double)0.9f;
            if (this.onGround) {
                this.motionX *= (double)0.7f;
                this.motionZ *= (double)0.7f;
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
            return new PoofParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
