package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class SpellParticle
extends SpriteTexturedParticle {
    private static final Random RANDOM = new Random();
    private final IAnimatedSprite spriteWithAge;

    private SpellParticle(ClientWorld p_i232429_1_, double x, double y, double z, double p_i232429_8_, double motionY, double p_i232429_12_, IAnimatedSprite spriteWithAge) {
        super(p_i232429_1_, x, y, z, 0.5 - RANDOM.nextDouble(), motionY, 0.5 - RANDOM.nextDouble());
        this.spriteWithAge = spriteWithAge;
        this.motionY *= (double)0.2f;
        if (p_i232429_8_ == 0.0 && p_i232429_12_ == 0.0) {
            this.motionX *= (double)0.1f;
            this.motionZ *= (double)0.1f;
        }
        this.particleScale *= 0.75f;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.canCollide = false;
        this.selectSpriteWithAge(spriteWithAge);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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

    public static class WitchFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public WitchFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SpellParticle spellparticle = new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            float f = worldIn.rand.nextFloat() * 0.5f + 0.35f;
            spellparticle.setColor(1.0f * f, 0.0f * f, 1.0f * f);
            return spellparticle;
        }
    }

    public static class MobFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public MobFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SpellParticle particle = new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setColor((float)xSpeed, (float)ySpeed, (float)zSpeed);
            return particle;
        }
    }

    public static class InstantFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public InstantFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
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
            return new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    public static class AmbientMobFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public AmbientMobFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SpellParticle particle = new SpellParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setAlphaF(0.15f);
            particle.setColor((float)xSpeed, (float)ySpeed, (float)zSpeed);
            return particle;
        }
    }
}
