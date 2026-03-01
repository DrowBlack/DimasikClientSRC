package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.MathHelper;

public class RedstoneParticle
extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteWithAge;

    private RedstoneParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, RedstoneParticleData particleData, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.spriteWithAge = spriteWithAge;
        this.motionX *= (double)0.1f;
        this.motionY *= (double)0.1f;
        this.motionZ *= (double)0.1f;
        float f = (float)Math.random() * 0.4f + 0.6f;
        this.particleRed = ((float)(Math.random() * (double)0.2f) + 0.8f) * particleData.getRed() * f;
        this.particleGreen = ((float)(Math.random() * (double)0.2f) + 0.8f) * particleData.getGreen() * f;
        this.particleBlue = ((float)(Math.random() * (double)0.2f) + 0.8f) * particleData.getBlue() * f;
        this.particleScale *= 0.75f * particleData.getAlpha();
        int i = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)i * particleData.getAlpha(), 1.0f);
        this.selectSpriteWithAge(spriteWithAge);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getScale(float scaleFactor) {
        return this.particleScale * MathHelper.clamp(((float)this.age + scaleFactor) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
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
            this.move(this.motionX, this.motionY, this.motionZ);
            if (this.posY == this.prevPosY) {
                this.motionX *= 1.1;
                this.motionZ *= 1.1;
            }
            this.motionX *= (double)0.96f;
            this.motionY *= (double)0.96f;
            this.motionZ *= (double)0.96f;
            if (this.onGround) {
                this.motionX *= (double)0.7f;
                this.motionZ *= (double)0.7f;
            }
        }
    }

    public static class Factory
    implements IParticleFactory<RedstoneParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(RedstoneParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new RedstoneParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, this.spriteSet);
        }
    }
}
