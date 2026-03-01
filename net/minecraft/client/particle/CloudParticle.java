package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;

public class CloudParticle
extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteSetWithAge;

    private CloudParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteSetWithAge) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        float f1;
        this.spriteSetWithAge = spriteSetWithAge;
        float f = 2.5f;
        this.motionX *= (double)0.1f;
        this.motionY *= (double)0.1f;
        this.motionZ *= (double)0.1f;
        this.motionX += motionX;
        this.motionY += motionY;
        this.motionZ += motionZ;
        this.particleRed = f1 = 1.0f - (float)(Math.random() * (double)0.3f);
        this.particleGreen = f1;
        this.particleBlue = f1;
        this.particleScale *= 1.875f;
        int i = (int)(8.0 / (Math.random() * 0.8 + 0.3));
        this.maxAge = (int)Math.max((float)i * 2.5f, 1.0f);
        this.canCollide = false;
        this.selectSpriteWithAge(spriteSetWithAge);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            double d0;
            this.selectSpriteWithAge(this.spriteSetWithAge);
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.96f;
            this.motionY *= (double)0.96f;
            this.motionZ *= (double)0.96f;
            PlayerEntity playerentity = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 2.0, false);
            if (playerentity != null && this.posY > (d0 = playerentity.getPosY())) {
                this.posY += (d0 - this.posY) * 0.2;
                this.motionY += (playerentity.getMotion().y - this.motionY) * 0.2;
                this.setPosition(this.posX, this.posY, this.posZ);
            }
            if (this.onGround) {
                this.motionX *= (double)0.7f;
                this.motionZ *= (double)0.7f;
            }
        }
    }

    public static class SneezeFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public SneezeFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CloudParticle particle = new CloudParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setColor(200.0f, 50.0f, 120.0f);
            particle.setAlphaF(0.4f);
            return particle;
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
            return new CloudParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
