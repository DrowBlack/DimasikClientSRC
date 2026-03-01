package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class WaterWakeParticle
extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteWithAge;

    private WaterWakeParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.spriteWithAge = spriteWithAge;
        this.motionX *= (double)0.3f;
        this.motionY = Math.random() * (double)0.2f + (double)0.1f;
        this.motionZ *= (double)0.3f;
        this.setSize(0.01f, 0.01f);
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.selectSpriteWithAge(spriteWithAge);
        this.particleGravity = 0.0f;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
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
        int i = 60 - this.maxAge;
        if (this.maxAge-- <= 0) {
            this.setExpired();
        } else {
            this.motionY -= (double)this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.98f;
            this.motionY *= (double)0.98f;
            this.motionZ *= (double)0.98f;
            float f = (float)i * 0.001f;
            this.setSize(f, f);
            this.setSprite(this.spriteWithAge.get(i % 4, 4));
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
            return new WaterWakeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
