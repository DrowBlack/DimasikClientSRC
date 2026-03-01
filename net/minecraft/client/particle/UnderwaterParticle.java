package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class UnderwaterParticle
extends SpriteTexturedParticle {
    private UnderwaterParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y - 0.125, z);
        this.particleRed = 0.4f;
        this.particleGreen = 0.4f;
        this.particleBlue = 0.7f;
        this.setSize(0.01f, 0.01f);
        this.particleScale *= this.rand.nextFloat() * 0.6f + 0.2f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.canCollide = false;
    }

    private UnderwaterParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y - 0.125, z, motionX, motionY, motionZ);
        this.setSize(0.01f, 0.01f);
        this.particleScale *= this.rand.nextFloat() * 0.6f + 0.6f;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.canCollide = false;
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
            this.move(this.motionX, this.motionY, this.motionZ);
        }
    }

    public static class WarpedSporeFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public WarpedSporeFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            double d0 = (double)worldIn.rand.nextFloat() * -1.9 * (double)worldIn.rand.nextFloat() * 0.1;
            UnderwaterParticle underwaterparticle = new UnderwaterParticle(worldIn, x, y, z, 0.0, d0, 0.0);
            underwaterparticle.selectSpriteRandomly(this.spriteSet);
            underwaterparticle.setColor(0.1f, 0.1f, 0.3f);
            underwaterparticle.setSize(0.001f, 0.001f);
            return underwaterparticle;
        }
    }

    public static class UnderwaterFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public UnderwaterFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            UnderwaterParticle underwaterparticle = new UnderwaterParticle(worldIn, x, y, z);
            underwaterparticle.selectSpriteRandomly(this.spriteSet);
            return underwaterparticle;
        }
    }

    public static class CrimsonSporeFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public CrimsonSporeFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Random random = worldIn.rand;
            double d0 = random.nextGaussian() * (double)1.0E-6f;
            double d1 = random.nextGaussian() * (double)1.0E-4f;
            double d2 = random.nextGaussian() * (double)1.0E-6f;
            UnderwaterParticle underwaterparticle = new UnderwaterParticle(worldIn, x, y, z, d0, d1, d2);
            underwaterparticle.selectSpriteRandomly(this.spriteSet);
            underwaterparticle.setColor(0.9f, 0.4f, 0.5f);
            return underwaterparticle;
        }
    }
}
