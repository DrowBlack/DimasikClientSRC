package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;

public class LavaParticle
extends SpriteTexturedParticle {
    private LavaParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX *= (double)0.8f;
        this.motionY *= (double)0.8f;
        this.motionZ *= (double)0.8f;
        this.motionY = this.rand.nextFloat() * 0.4f + 0.05f;
        this.particleScale *= this.rand.nextFloat() * 2.0f + 0.2f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        int i = super.getBrightnessForRender(partialTick);
        int j = 240;
        int k = i >> 16 & 0xFF;
        return 0xF0 | k << 16;
    }

    @Override
    public float getScale(float scaleFactor) {
        float f = ((float)this.age + scaleFactor) / (float)this.maxAge;
        return this.particleScale * (1.0f - f * f);
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        float f = (float)this.age / (float)this.maxAge;
        if (this.rand.nextFloat() > f) {
            this.world.addParticle(ParticleTypes.SMOKE, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
        }
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.motionY -= 0.03;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.999f;
            this.motionY *= (double)0.999f;
            this.motionZ *= (double)0.999f;
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
            LavaParticle lavaparticle = new LavaParticle(worldIn, x, y, z);
            lavaparticle.selectSpriteRandomly(this.spriteSet);
            return lavaparticle;
        }
    }
}
