package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;

public class CritParticle
extends SpriteTexturedParticle {
    private CritParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        float f;
        this.motionX *= (double)0.1f;
        this.motionY *= (double)0.1f;
        this.motionZ *= (double)0.1f;
        this.motionX += motionX * 0.4;
        this.motionY += motionY * 0.4;
        this.motionZ += motionZ * 0.4;
        this.particleRed = f = (float)(Math.random() * (double)0.3f + (double)0.6f);
        this.particleGreen = f;
        this.particleBlue = f;
        this.particleScale *= 0.75f;
        this.maxAge = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
        this.canCollide = false;
        this.tick();
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
            this.move(this.motionX, this.motionY, this.motionZ);
            this.particleGreen = (float)((double)this.particleGreen * 0.96);
            this.particleBlue = (float)((double)this.particleBlue * 0.9);
            this.motionX *= (double)0.7f;
            this.motionY *= (double)0.7f;
            this.motionZ *= (double)0.7f;
            this.motionY -= (double)0.02f;
            if (this.onGround) {
                this.motionX *= (double)0.7f;
                this.motionZ *= (double)0.7f;
            }
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class MagicFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public MagicFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CritParticle critparticle = new CritParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            critparticle.particleRed *= 0.3f;
            critparticle.particleGreen *= 0.8f;
            critparticle.selectSpriteRandomly(this.spriteSet);
            return critparticle;
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
            CritParticle critparticle = new CritParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            critparticle.selectSpriteRandomly(this.spriteSet);
            return critparticle;
        }
    }

    public static class DamageIndicatorFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public DamageIndicatorFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CritParticle critparticle = new CritParticle(worldIn, x, y, z, xSpeed, ySpeed + 1.0, zSpeed);
            critparticle.setMaxAge(20);
            critparticle.selectSpriteRandomly(this.spriteSet);
            return critparticle;
        }
    }
}
