package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class ReversePortalParticle
extends PortalParticle {
    private ReversePortalParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.particleScale = (float)((double)this.particleScale * 1.5);
        this.maxAge = (int)(Math.random() * 2.0) + 60;
    }

    @Override
    public float getScale(float scaleFactor) {
        float f = 1.0f - ((float)this.age + scaleFactor) / ((float)this.maxAge * 1.5f);
        return this.particleScale * f;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            float f = (float)this.age / (float)this.maxAge;
            this.posX += this.motionX * (double)f;
            this.posY += this.motionY * (double)f;
            this.posZ += this.motionZ * (double)f;
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
            ReversePortalParticle reverseportalparticle = new ReversePortalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            reverseportalparticle.selectSpriteRandomly(this.spriteSet);
            return reverseportalparticle;
        }
    }
}
